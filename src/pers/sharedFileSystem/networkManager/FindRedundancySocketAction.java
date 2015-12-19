package pers.sharedFileSystem.networkManager;

import pers.sharedFileSystem.bloomFilterManager.BloomFilter;
import pers.sharedFileSystem.communicationObject.FingerprintInfo;
import pers.sharedFileSystem.communicationObject.MessageProtocol;
import pers.sharedFileSystem.communicationObject.MessageType;
import pers.sharedFileSystem.logManager.LogRecord;
import pers.sharedFileSystem.systemFileManager.FingerprintAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 监控存储服务器发来的消息线程
 */
public class FindRedundancySocketAction implements Runnable {
	/**
	 * 监听的连接
	 */
	private Socket socket;
	/**
	 * 该线程是否正在监听
	 */
	private volatile boolean run = true;
	/**
	 * 存储服务器返回的查找结果
	 */
	public volatile FingerprintInfo fingerprintInfo;
	/**
	 * 父线程
	 */
	private SocketAction socketAction;

	public FindRedundancySocketAction(Socket s, SocketAction c) {
		this.socket = s;
		this.socketAction=c;
	}

	/**
	 * 获取返回结果
	 * @return
	 */
	public FingerprintInfo getFingerprintInfo(){
		return  fingerprintInfo;
	}
	/**
	 * 判断线程是否停止和存储端是否已断开连接
	 * @return
	 */
	public boolean isStop(){
		boolean isShutdown=false;
		ObjectOutputStream oos = null;
		try {
			if(run) {
				MessageProtocol reMessage = new MessageProtocol();
				reMessage.messageType = MessageType.SOCKET_MONITOR;
				oos = new ObjectOutputStream(
						socket.getOutputStream());
				oos.writeObject(reMessage);
				oos.flush();
			}
		} catch (Exception e) {
			isShutdown=true;
		}
		return run==false||isShutdown;
	}

	/**
	 * 处理返回冗余信息文件查找结果消息
	 * @param mes
	 * @return
	 */
	private MessageProtocol doReplyFindRedundancy(MessageProtocol mes){
		FingerprintInfo fInfo=(FingerprintInfo)mes.content;
		String str="";
		if(fInfo!=null) {
			str="receive REPLY_FIND_REDUNDANCY from "+socket.getInetAddress().toString()+" fingerPrint ["+fInfo.getMd5()+"] NodeId: "+fInfo.getNodeId()+" path: "+fInfo.getFilePath()+fInfo.getFileName();
		}else
			str="receive REPLY_FIND_REDUNDANCY from "+socket.getInetAddress().toString()+" fingerPrint null";
		LogRecord.RunningInfoLogger.info(str);
		overThis();
		fingerprintInfo=fInfo;
		return null;
	}

	/**
	 * 收到消息之后进行分类处理
	 * @param mes
	 * @return
	 */
	private MessageProtocol doAction(MessageProtocol mes){
		switch (mes.messageType){
			case REPLY_FIND_REDUNDANCY:{
				return doReplyFindRedundancy(mes);
			}
			default:{
				return null;
			}
		}

	}

	/**
	 * 告诉存储服务器停止查找
	 */
	public void stopFindRedundancy(){
		ObjectOutputStream oos = null;
		MessageProtocol reMessage=new MessageProtocol();
		reMessage.messageType=MessageType.STOP_FIND_REDUNDANCY;
		try {
			oos = new ObjectOutputStream(
                    socket.getOutputStream());
			oos.writeObject(reMessage);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
			overThis();
		}
	}

	public void run() {
		while (run) {
				try {
					InputStream in = socket.getInputStream();
					if (in.available() > 0) {
						ObjectInputStream ois = new ObjectInputStream(in);
						Object obj = ois.readObject();
						MessageProtocol mes=(MessageProtocol)obj;
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