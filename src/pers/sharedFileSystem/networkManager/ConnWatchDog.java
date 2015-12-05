package pers.sharedFileSystem.networkManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.entity.SystemConfig;
import pers.sharedFileSystem.logManager.LogRecord;

/**
 * 监控新客户端连接的线程
 * 
 */
public class ConnWatchDog implements Runnable {
	/**
	 * 已连接的客户端集合
	 */
	private Hashtable<SocketAction, Thread> threads = new Hashtable<SocketAction, Thread>();

	/**
	 * 服务端配置文件
	 */
	private SystemConfig systemConfig = Config.SYSTEMCONFIG;

	public ConnWatchDog() {
	}

	private ServerSocket serverSocket;

	/**
	 * 停止服务端监听
	 */
	public void shutDownSocket() {
		if (serverSocket != null)
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		for (SocketAction socketAction : threads.keySet()) {
			socketAction.overThis();
		}
	}

	public void run() {
		serverSocket = null;
		try {
			serverSocket = new ServerSocket(systemConfig.Port, 5);
			LogRecord.RunningInfoLogger.info("server started.");
				Socket s = serverSocket.accept();
			LogRecord.RunningInfoLogger.info("client connected.");
				SocketAction socketAction = new SocketAction(s);
				Thread thread = new Thread(socketAction);
				threads.put(socketAction, thread);
				thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null)
				try {
					serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
