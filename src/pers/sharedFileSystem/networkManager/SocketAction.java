package pers.sharedFileSystem.networkManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import pers.sharedFileSystem.bloomFilterManager.BloomFilter;
import pers.sharedFileSystem.communicationObject.*;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.SenderType;
import pers.sharedFileSystem.entity.ServerNode;
import pers.sharedFileSystem.entity.SystemConfig;
import pers.sharedFileSystem.logManager.LogRecord;

/**
 * 监听某个连接（客户端）发来的消息
 */
public class SocketAction implements Runnable {
	/**
	 * 监听的连接
	 */
	private Socket socket;
	/**
	 * 该线程是否正在监听
	 */
	private boolean run = true;
	/**
	 * 最近一次收到客户端发来信息的时间
	 */
	private long lastReceiveTime;
	/**
	 *  接收延迟时间间隔（毫秒）
	 */
	private long receiveTimeDelay = 30000;
	/**
	 * 如果当前线程是和存储管理子系统的通信，那么此字段是该存储服务器的配置信息
	 */
	private ServerNode serverNode =null;
	/**
	 * 集群状态
	 */
	private ClusterState clusterState = ClusterState.getInstance();

	public SocketAction(Socket s) {
		this.socket = s;
		lastReceiveTime = System.currentTimeMillis();
	}
	/**
	 * 某次查询冗余信息命令产生的线程集合
	 */
	private Hashtable<Double, List<ConnStoreServerSocketAction>> findRedundancyThreads= new Hashtable<Double,List<ConnStoreServerSocketAction>>();

	/**
	 * 资源目录树配置文件
	 */
//	private Hashtable<String, ServerNode> fileConfig= Config.getConfig();
	/**
	 * 服务端配置文件
	 */
	private SystemConfig systemConfig = Config.SYSTEMCONFIG;
	/**
	 * 给需要发送冗余验证消息的存储服务器列表中的每个服务器都发送查找冗余信息文件消息命令
	 * @param fInfo
	 * @param seq 冗余验证序列号
	 */
	private boolean sendFindRedundancyMessageToStoreNode(FingerprintInfo fInfo,double seq){
//		FindRedundancyObject findRedundancyObject=new FindRedundancyObject();
//		findRedundancyObject.fingerprintInfo=fInfo;
//		findRedundancyObject.sequenceNum= seq;
		List<ConnStoreServerSocketAction> ths=new ArrayList<ConnStoreServerSocketAction>();
		MessageProtocol mes=new MessageProtocol();
		mes.messageType=MessageType.FIND_REDUNDANCY;
		mes.content=fInfo;
//		for(String id:systemConfig.redundancyServerIds){
		for(ServerNode sn : clusterState.getAllServerNode()){
//			ServerNode sn=fileConfig.get(id);
			try {
				Socket st=new Socket(sn.Ip, sn.ServerPort);
				ObjectOutputStream oos = new ObjectOutputStream(
						st.getOutputStream());
				oos.writeObject(mes);
				oos.flush();
				LogRecord.RunningInfoLogger.info("send FIND_REDUNDANCY comand to"+sn.Ip+":"+sn.ServerPort+" fingerPrint ["+fInfo.getMd5()+"]");
				ConnStoreServerSocketAction socketAction = new ConnStoreServerSocketAction(st);
				Thread thread = new Thread(socketAction);
				ths.add(socketAction);
				thread.start();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		findRedundancyThreads.put(seq,ths);
		return true;
	}

	/**
	 * 处理冗余验证消息
	 * @param mes
	 * @return
	 */
	private MessageProtocol doCheckRedundancyAction(MessageProtocol mes){
		FingerprintInfo figurePrint=(FingerprintInfo)mes.content;
		MessageProtocol reMessage=new MessageProtocol();
		//是否找到重复的文件指纹
		String reMes="";
		//存储服务器返回的查找结果
		FingerprintInfo fingerprintInfo=null;
		//验证指纹
		if(BloomFilter.getInstance().isFingerPrintExist(figurePrint.getMd5())) {
			//此处应该返回指纹信息对应的文件的绝对路径
			double sequenceNum= CommonUtil.generateCheckId();
			boolean res=sendFindRedundancyMessageToStoreNode(figurePrint,sequenceNum);
			if(res){
				//轮询检查冗余信息文件文件返回消息
				List<ConnStoreServerSocketAction> ths=findRedundancyThreads.get(sequenceNum);
				int num=ths.size();
				boolean isRunning=true;//是否需要轮询
				while (isRunning){
					LogRecord.RunningInfoLogger.info("query if all storeNode finish FIND_REDUNDANCY.");
					int n=0;
					for(ConnStoreServerSocketAction ac:ths){
						if(ac.isStop()){
							n++;
							if(ac.getFingerprintInfo()!=null){//某个存储服务器找到了该指纹信息
								fingerprintInfo=ac.getFingerprintInfo();
								isRunning=false;
								break;
							}
						}
					}
					if(n==num || fingerprintInfo!=null){//全部查找都结束，或者某个存储服务器找到了该指纹信息
						isRunning=false;
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
						isRunning=false;
					}
				}
				//找到之后通知其他正在查找的存储服务器停止查找
				if(fingerprintInfo!=null){
					for(ConnStoreServerSocketAction ac:ths){
						if(!ac.isStop()){
							ac.stopFindRedundancy();
							ac.overThis();
						}
					}
				}
				//存储服务器监听线程都停止之后，移除本次产生的查找线程对象，这样这些线程对象会被垃圾回收，从而释放占用的内存
				findRedundancyThreads.remove(sequenceNum);
			}else{
				List<ConnStoreServerSocketAction> ths=findRedundancyThreads.get(sequenceNum);
				//查找失败通知其他正在查找的存储服务器停止查找
					for(ConnStoreServerSocketAction ac:ths){
						if(!ac.isStop()){
							ac.stopFindRedundancy();
							ac.overThis();
						}
					}
				//存储服务器监听线程都停止之后，移除本次产生的查找线程对象，这样这些线程对象会被垃圾回收，从而释放占用的内存
				findRedundancyThreads.remove(sequenceNum);
			}
			/**************************/
			//=new FingerprintInfo();//new FingerprintAdapter().getFingerprintInfoByMD5(figurePrint);
			if(fingerprintInfo==null){
				reMes="false";
				reMessage.messageCode=4002;
			}else {
				reMessage.messageCode=4000;
				reMessage.content=fingerprintInfo;
				reMes = "true  , file upload rapidly.";
			}
		}
		else {
			reMes="false";
			reMessage.messageCode=4001;
		}
		reMessage.messageType=MessageType.REPLY_CHECK_REDUNDANCY;
		LogRecord.FileHandleInfoLogger.info("BloomFilter check redundancy ["+fingerprintInfo+"] "+reMes);
		return reMessage;
	}
	/**
	 * 处理添加指纹信息消息（布隆过滤器置位）
	 * @param mes
	 * @return
	 */
	private MessageProtocol doAddFingerprintAction(MessageProtocol mes){
		FingerprintInfo fInfo=(FingerprintInfo)mes.content;//new FingerprintInfo(figurePrint,filePath,fileName);
		MessageProtocol reMessage=new MessageProtocol();
		if(fInfo!=null) {
//			new FingerprintAdapter().saveFingerprint(fInfo);
//			LogRecord.FileHandleInfoLogger.info("BloomFilter save a new fingerPrint to disk ["+fInfo.getMd5()+"]");
			double count=BloomFilter.getInstance().addFingerPrint(fInfo.getMd5());
			LogRecord.FileHandleInfoLogger.info("BloomFilter add a new fingerPrint ["+fInfo.getMd5()+"] total= "+count);
			reMessage.messageType=MessageType.REPLY_ADD_FINGERPRINT;
			reMessage.messageCode=4000;
			return reMessage;
		}
		return null;
	}
	/**
	 * 处理收到 config 信息
	 * @param mes
	 * @return
	 */
	private MessageProtocol doSendConfigAction(MessageProtocol mes){
		ServerNode serverNode=(ServerNode) mes.content;
		LogRecord.RunningInfoLogger.info("receive config: ");
		String ipPort = serverNode.Ip+":"+serverNode.ServerPort;
		this.serverNode = serverNode;
		if(mes.senderType == SenderType.STORE){
			if(clusterState.getStore(ipPort)==null){
				clusterState.addServerNode(serverNode);
				clusterState.addStore(ipPort, this);
				serverNode.print("");
			}
		}
		return null;
	}
	/**
	 * 处理收到存储端的指纹信息
	 * @param mes
	 * @return
	 */
	private MessageProtocol doSendFingerprintAction(MessageProtocol mes){
		ArrayList<String> fingers=(ArrayList<String>)mes.content;
		String str="";
		if(fingers.size()>0) {
			str="receive SEND_FINGERPRINT_LIST from "+socket.getInetAddress().toString()+" num= "+fingers.size();
			for(String s:fingers){
				BloomFilter.getInstance().addFingerPrint(s);
			}
			clusterState.addFingerprintNum(fingers.size());
		}else {
			str = "SEND_FINGERPRINT_LIST from " + socket.getInetAddress().toString() + " is ended";
		}
		LogRecord.RunningInfoLogger.info(str);
		return null;
	}

	/**
	 * 将一个消息对象同步发送给socket
	 *@param  mes 消息对象
	 */
	public void sendMessageToStoreServer(MessageProtocol mes) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(mes);
		oos.flush();
	}

	/**
	 * 获取某个存储目录结点的还未存满的扩容结点
	 * @return
	 */
	private String getIdleExpandDirectoryNodeId(String directoryNodeId){
		// 开始获取directoryNodeId的扩容文件存储信息
		ServerNode sn = clusterState.getNodeByNodeId(directoryNodeId).getServerNode();
		String ipPort = sn.Ip+":"+sn.ServerPort;
		SocketAction so = clusterState.getStore(ipPort);
		String expandDirectoryNodeId = "";
		try {
			MessageProtocol queryMessage = new MessageProtocol();
			queryMessage.messageType = MessageType.GET_EXPAND_FILE_STORE_INFO;
			queryMessage.content=directoryNodeId;
			so.sendMessageToStoreServer(queryMessage);
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			MessageProtocol replyMessage = (MessageProtocol) ois.readObject();
			if (replyMessage != null &&replyMessage.messageType==MessageType.REPLY_GET_EXPAND_FILE_STORE_INFO ) {
				ExpandFileStoreInfo expandFileStoreInfo = (ExpandFileStoreInfo)replyMessage.content;
				for(String nodeId : expandFileStoreInfo.expandNodeList){
					// 查看nodeId是否存满，即它是否需要扩容
					ServerNode sn2 = clusterState.getNodeByNodeId(nodeId).getServerNode();
					MessageProtocol ifFull = new MessageProtocol();
					ifFull.messageType = MessageType.IF_DIRECTORY_NEED_EXPAND;
					ifFull.senderType = SenderType.CLIENT;
					ifFull.content="";
					SocketAction saTmp = clusterState.getStore(sn2.Ip+":"+sn2.ServerPort);
					saTmp.sendMessageToStoreServer(ifFull);
					Socket soTmp=saTmp.getSocket();
					ObjectInputStream oisTmp = new ObjectInputStream(soTmp.getInputStream());
					MessageProtocol replyMessageTmp = (MessageProtocol) oisTmp.readObject();
					if(replyMessageTmp!=null&&replyMessageTmp.messageType==MessageType.REPLY_IF_DIRECTORY_NEED_EXPAND) {
						String reply=replyMessage.content.toString();
						if(reply.equals("0")){
							expandDirectoryNodeId = nodeId;
							break;
						}
					}
				}
			}
		}
		catch (Exception e) {
			LogRecord.RunningErrorLogger.error(e.toString());
		}
		return expandDirectoryNodeId;
	}

	/**
	 * 从当前集群中获取某台空闲的存储服务器，并返回一个空闲的存储目录编号
	 * @return
	 */
	private String getIdleDirectoryNodeId(){
		String result = "";
		// 获取所有存储服务器的运行状态
		List<ServerState>serverStates = new ArrayList<>();
		for(ServerNode snn : clusterState.getAllServerNode()){
			try {
				MessageProtocol getState = new MessageProtocol();
				getState.messageType = MessageType.GET_SERVER_STATE;
				getState.content="";
				SocketAction saTmp = clusterState.getStore(snn.Ip+":"+snn.ServerPort);
				saTmp.sendMessageToStoreServer(getState);
				LogRecord.RunningInfoLogger.info("send GET_SERVER_STATE to "+snn.Ip+":"+snn.ServerPort);
				Socket soTmp=saTmp.getSocket();
				ObjectInputStream oisTmp = new ObjectInputStream(soTmp.getInputStream());
				MessageProtocol replyMessageTmp = (MessageProtocol) oisTmp.readObject();
				if(replyMessageTmp!=null&&replyMessageTmp.messageType==MessageType.REPLY_GET_SERVER_STATE) {
					ServerState ss=(ServerState) replyMessageTmp.content;
					serverStates.add(ss);
				}
			}
			catch (Exception e) {
				LogRecord.RunningErrorLogger.error(e.toString());
			}
		}
		// 选择一个空闲服务器
		int index =-1;// 空闲服务器的索引
		double freeDisk = -1;
		for(int i=0; i<serverStates.size();i++){
			if(i==0) {
				index = i;
				freeDisk = serverStates.get(i).FreeDisk;
			}else {
				if(serverStates.get(i).FreeDisk>freeDisk){
					index = i;
					freeDisk = serverStates.get(i).FreeDisk;
				}
			}
		}
		// 找到了空闲的存储服务器，返回该服务器的一个祖先结点
		if(index>=0){
			result = clusterState.getAllServerNode().get(index).ChildNodes.get(0).Id;
		}
		return result;
	}
	/**
	 * 处理给某个存储目录结点扩容
	 * @param mes
	 * @return 扩容结点的编号
	 * TODO 这样扩容有个缺点，每个存储接口管理子系统都需要维护整个数据中心的存储目录树结构，否则扩容之后无法把文件保存到指定扩容结点
	 */
	private MessageProtocol doGetExpandDirectoryAction(MessageProtocol mes){
		LogRecord.FileHandleInfoLogger.info("receive GET_EXPAND_DIRECTORY from "+socket.getInetAddress().toString()+":"+socket.getPort());
		String directoryNodeId=mes.content.toString();//待扩容的存储目录结点
		MessageProtocol reMessage=new MessageProtocol();
		String expandDirectoryNodeId = getIdleExpandDirectoryNodeId(directoryNodeId);
		// 如果已经扩容的结点都存满了，或者之前没有给directoryNodeId扩容过
		if(CommonUtil.isEmpty(expandDirectoryNodeId)){
			expandDirectoryNodeId =getIdleDirectoryNodeId();
			// 如果集群中没有空闲的存储服务器
			if(CommonUtil.isEmpty(expandDirectoryNodeId)){
				LogRecord.FileHandleInfoLogger.info("all disk are full.");
				reMessage.messageCode=4010;
				reMessage.messageType=MessageType.REPLY_GET_EXPAND_DIRECTORY;
				return reMessage;
			}else{
				LogRecord.FileHandleInfoLogger.info("["+directoryNodeId+"] doesn't expand before, the expand node is"+expandDirectoryNodeId);
			}
		}else{
			LogRecord.FileHandleInfoLogger.info("["+directoryNodeId+"] has expanded before, the expand node "+expandDirectoryNodeId+"is free now");
		}
		reMessage.messageCode=4000;
		reMessage.messageType=MessageType.REPLY_GET_EXPAND_DIRECTORY;
		reMessage.content=expandDirectoryNodeId;
		return reMessage;
	}
	/**
	 * 收到消息之后进行分类处理
	 * @param mes
	 * @return
	 */
	private MessageProtocol doAction(MessageProtocol mes){
		switch (mes.messageType){
			case CHECK_REDUNDANCY:{
				return doCheckRedundancyAction(mes);
			}
			case ADD_FINGERPRINT:{
				return doAddFingerprintAction(mes);
			}
			case KEEP_ALIVE:{
				if(mes.senderType == SenderType.CLIENT)
					LogRecord.RunningInfoLogger.info("client handshake "+socket.getInetAddress().toString()+":"+socket.getPort());
				else if(mes.senderType == SenderType.STORE)
					LogRecord.RunningInfoLogger.info("store handshake "+socket.getInetAddress().toString()+":"+socket.getPort());
				return null;
			}
			case SEND_CONFIG:{
				return doSendConfigAction(mes);
			}
			case SEND_FINGERPRINT_LIST:{
				return doSendFingerprintAction(mes);
			}
			case GET_EXPAND_DIRECTORY:{
				return doGetExpandDirectoryAction(mes);
			}
			default:{
				return null;
			}
		}

	}

	public Socket getSocket(){
		return socket;
	}
	public void run() {
		while (run) {
			// 超过接收延迟时间（毫秒）之后，终止此客户端的连接
			if (System.currentTimeMillis() - lastReceiveTime > receiveTimeDelay) {
				overThis();
			} else {
				try {
					InputStream in = socket.getInputStream();
					if (in.available() > 0) {
						ObjectInputStream ois = new ObjectInputStream(in);
						Object obj = ois.readObject();
						MessageProtocol mes=(MessageProtocol)obj;
						lastReceiveTime = System.currentTimeMillis();
						MessageProtocol out = doAction(mes);// 处理消息，并给客户端反馈
						if (out != null) {
							ObjectOutputStream oos = new ObjectOutputStream(
									socket.getOutputStream());
							oos.writeObject(out);
							oos.flush();
						}
					} else {
						Thread.sleep(10);
					}
				} catch (Exception e) {
					e.printStackTrace();
					overThis();
				}
			}
		}
	}

	/**
	 * 关闭此socket连接，停止对该连接的监听
	 */
	public void overThis() {
		if (run)
			run = false;
		if(serverNode!=null){
//			clusterState.shutDownServerNode(serverNode);
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		LogRecord.RunningInfoLogger.info("close " + socket.getRemoteSocketAddress());
	}

}