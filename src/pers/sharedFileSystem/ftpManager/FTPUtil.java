package pers.sharedFileSystem.ftpManager;

import java.io.IOException;
import java.net.SocketException;
import java.util.Collection;
import java.util.Hashtable;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.entity.ServerNode;
import pers.sharedFileSystem.logManager.LogRecord;

/**
 * 
 * @author buaashuai
 *
 */
public class FTPUtil {
	// private static Logger logger = Logger.getLogger(FTPUtil.class);
	/**
	 * 远程根节点ID所对应的FTP连接池
	 * 
	 */
	private static Hashtable<String, FTPClient> FTPCLIENT;

	static {
		Hashtable<String, ServerNode> rootNodeTable;
		FTPCLIENT = new Hashtable<String, FTPClient>();
		try {
			rootNodeTable = Config.getConfig();
			Collection<ServerNode> rootNodes = rootNodeTable.values();
			for (ServerNode r : rootNodes) {
				if (!r.Ip.equals("127.0.0.1") && !r.Ip.equals("localhost"))
					FTPCLIENT.put(r.Id,
							getFTPClient(r.Ip, r.Port, r.UserName, r.Password));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogRecord.RunningErrorLogger.error(e.toString());
		}
	}

	/**
	 * 获取serverNode对应的FTPClient对象
	 * 
	 * @param serverNode
	 *            根节点
	 * @param type 是否强制让远程服务器新开端口， (如果在同一个远程服务器上操作文件，需要2个FTPClient)
	 *       <p>
	 *       type=false从连接池获取FTPClient
	 *       </p>
	 *       <p>
	 *       type=true强制新开一个FTPClient
	 *       </p>
	 *       新开的FTPClient不加入连接池 ，目的是防止将连接池中正在被使用的FTPClient替换掉
	 * @return 返回 FTPClient
	 */
	public static FTPClient getFTPClientByServerNode(ServerNode serverNode,
			boolean type) {
		if (type)
			return getFTPClient(serverNode.Ip, serverNode.Port,
					serverNode.UserName, serverNode.Password);
		FTPClient ftpClient = FTPCLIENT.get(serverNode.Id);
		if (ftpClient != null)
			return ftpClient;
		else {
			FTPCLIENT.put(
					serverNode.Id,
					getFTPClient(serverNode.Ip, serverNode.Port,
							serverNode.UserName, serverNode.Password));
			return FTPCLIENT.get(serverNode);
		}
	}

	/**
	 * 获取FTPClient对象
	 * 
	 * @param ftpHost
	 *            FTP主机服务器
	 * @param ftpPassword
	 *            FTP 登录密码
	 * @param ftpUserName
	 *            FTP登录用户名
	 * @param ftpPort
	 *            FTP端口 默认为21
	 * @return FTPClient
	 */
	private static FTPClient getFTPClient(String ftpHost, int ftpPort,
			String ftpUserName, String ftpPassword) {
		FTPClient ftpClient = null;
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(ftpHost, ftpPort);// 连接FTP服务器
			ftpClient.login(ftpUserName, ftpPassword);// 登陆FTP服务器
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				LogRecord.RunningErrorLogger.error("can not connect to FTP server, username or password error");
				ftpClient.disconnect();
			} else {
				LogRecord.RunningInfoLogger.info("FTP connect successful [" + ftpHost + "]");
			}
		} catch (SocketException e) {
			LogRecord.RunningErrorLogger.error("IP address is likely to be error, please set the IP address again. [" + ftpHost + "]");
		} catch (IOException e) {
			LogRecord.RunningErrorLogger.error("port is  likely to be error, please set the port again. ");
		}
		return ftpClient;
	}
}
