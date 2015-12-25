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
import pers.sharedFileSystem.communicationObject.MessageProtocol;
import pers.sharedFileSystem.communicationObject.MessageType;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.communicationObject.FingerprintInfo;
import pers.sharedFileSystem.entity.ServerNode;
import pers.sharedFileSystem.entity.SystemConfig;
import pers.sharedFileSystem.logManager.LogRecord;

/**
 * 监控某个连接（客户端）发来的消息
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
	 *  接收延迟时间间隔
	 */
	private long receiveTimeDelay = 8000;

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
	private Hashtable<String, ServerNode> fileConfig= Config.getConfig();
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
		for(String id:systemConfig.redundancyServerIds){
			ServerNode sn=fileConfig.get(id);
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
						if(n==num){//全部查找都结束，并且都没有找到
							isRunning=false;
							break;
						}
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
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
				LogRecord.RunningInfoLogger.info("receive handshake");
				return null;
			}
			default:{
				return null;
			}
		}

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
	 * 关闭此socket连接，停止对该连接的监控
	 */
	public void overThis() {
		if (run)
			run = false;
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