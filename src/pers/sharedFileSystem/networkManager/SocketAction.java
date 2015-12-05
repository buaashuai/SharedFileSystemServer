package pers.sharedFileSystem.networkManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import pers.sharedFileSystem.bloomFilterManager.BloomFilter;
import pers.sharedFileSystem.communicationObject.MessageProtocol;
import pers.sharedFileSystem.communicationObject.MessageType;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.FingerprintInfo;
import pers.sharedFileSystem.logManager.LogRecord;
import pers.sharedFileSystem.systemFileManager.FingerprintAdapter;
import pers.sharedFileSystem.systemFileManager.MessageCodeHandler;

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
	 * 处理冗余验证消息
	 * @param mes
	 * @return
	 */
	private MessageProtocol doCheckRedundancyAction(MessageProtocol mes){
		String figurePrint=mes.content.get("figurePrint");
		String desNodeId=mes.content.get("desNodeId");
		MessageProtocol reMessage=new MessageProtocol();
		if(!CommonUtil.validateString(figurePrint)){
			reMessage.messageType=MessageType.REPLY_CHECK_REDUNDANCY;
			reMessage.content.put("messageCode","4001");
			LogRecord.FileHandleInfoLogger.info(MessageCodeHandler.getMessageInfo(4001, ""));
			return reMessage;
		}
		String reMes="";
		//验证指纹
		if(BloomFilter.getInstance().isFingerPrintExist(figurePrint)) {
			//此处应该返回指纹信息对应的文件的绝对路径
			FingerprintInfo fingerprintInfo=new FingerprintAdapter().getFingerprintInfoByMD5(desNodeId,figurePrint);
			if(fingerprintInfo==null){
				reMes="false";
				reMessage.content.put("messageCode","4003");
			}else {
				reMessage.content.put("messageCode","4000");
				reMessage.content.put("filePath", fingerprintInfo.FilePath+fingerprintInfo.FileName);
				reMes = "true";
				LogRecord.RunningInfoLogger.info(figurePrint+" upload rapidly.");
			}
		}
		else {
			reMes="false";
			reMessage.content.put("messageCode","4002");
		}
		reMessage.messageType=MessageType.REPLY_CHECK_REDUNDANCY;
		LogRecord.FileHandleInfoLogger.info("BloomFilter check redundancy ["+figurePrint+"] "+reMes);
		return reMessage;
	}
	/**
	 * 处理添加指纹信息消息
	 * @param mes
	 * @return
	 */
	private MessageProtocol doAddFingerprintAction(MessageProtocol mes){
		String figurePrint=mes.content.get("figurePrint");
		String filePath=mes.content.get("FilePath");
		String fileName=mes.content.get("FileName");
		String desNodeId=mes.content.get("desNodeId");
		FingerprintInfo fInfo=new FingerprintInfo(figurePrint,filePath,fileName);
		if(CommonUtil.validateString(figurePrint)) {
			new FingerprintAdapter().saveFingerprint(desNodeId,fInfo);
			BloomFilter.getInstance().addFingerPrint(figurePrint);
			LogRecord.FileHandleInfoLogger.info("BloomFilter add a new fingerPrint ["+figurePrint+"]");
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