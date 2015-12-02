package pers.sharedFileSystem.convenientUtil;

import java.io.File;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import pers.sharedFileSystem.entity.DirectoryNode;
import pers.sharedFileSystem.entity.Node;
import pers.sharedFileSystem.entity.ServerNode;
import pers.sharedFileSystem.ftpManager.FTPUtil;
import pers.sharedFileSystem.logManager.LogRecord;

/**
 * 文件操作相关的工具包
 * 
 * @author buaashuai
 *
 */
public class AdvancedFileUtil {
	/**
	 * 判断文件是否存在
	 * 
	 * @param serverNode
	 *            待验证的文件所在的根节点对象
	 * @param filePath
	 *            文件绝对路径，不带文件名
	 * @param fileName
	 *            文件名
	 * @param type
	 *            是否强制让远程服务器新开端口， (如果在同一个远程服务器上操作文件，需要2个FTPClient)
	 *            type=false从连接池获取FTPClient
	 *            ，type=true强制新开一个FTPClient，但是新开的FTPClient不加入连接池
	 * @return 文件是否存在 true存在，false不存在
	 */
	public static boolean isFileExist(DirectoryNode node, String filePath,
			String fileName, boolean type) {
		ServerNode serverNode=node.getServerNode();
		String fullPath = filePath + fileName;
		if (filePath.charAt(filePath.length() - 1) != '/')
			fullPath = filePath + "/" + fileName;
		if (!CommonUtil.isRemoteServer(serverNode.Ip)) {
			return new File(fullPath).exists();
		} else {
			FTPClient ftpClient = FTPUtil.getFTPClientByServerNode(serverNode,
					type);
			// ftpClient.setControlEncoding(encoding); // 中文支持
			boolean re = false;
			try {
				String relativePath = filePath.substring(node.StorePath
						.length());
				ftpClient.changeWorkingDirectory(new String(relativePath.getBytes(),"ISO-8859-1"));
				FTPFile[] ftpFiles = ftpClient.listFiles(new String(fileName.getBytes(),"ISO-8859-1"));
				if (ftpFiles.length > 0)
					re = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogRecord.FileHandleErrorLogger.error(e.toString());
			}
			return re;
		}
	}

	/**
	 * 验证文件夹是否存在，不存在就建立文件夹
	 * 
	 * @param directoryNode
	 *            文件夹所在目录节点对象
	 * @param route
	 *            文件绝对路径
	 */
	public static void validateDirectory(DirectoryNode directoryNode, String route) {
		ServerNode serverNode=directoryNode.getServerNode();
		if (!CommonUtil.isRemoteServer(serverNode.Ip)) {
			File file = new File(route);
			// 如果文件夹不存在则创建
			if (!file.exists() && !file.isDirectory())
				file.mkdir();
		} else {
			FTPClient ftpClient = FTPUtil.getFTPClientByServerNode(serverNode,
					false);
			try {
				String relativePath = route.substring(directoryNode.Path.length());
				ftpClient.makeDirectory(new String(relativePath.getBytes(),"ISO-8859-1"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogRecord.FileHandleErrorLogger.error(e.toString());
			}
		}

	}

	/**
	 * 删除本地单个文件
	 *
	 * @param filePath
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	private static boolean deleteLocalFile(String filePath) {
		File file = new File(filePath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			LogRecord.FileHandleInfoLogger.info("delete file successful: " + "127.0.0.1/"
					+filePath);
		}else
			LogRecord.FileHandleErrorLogger.error("file not exist: " + "127.0.0.1/"
					+filePath);
		return true;
	}

	/**
	 * 删除本地目录（文件夹）以及目录下的文件
	 * 
	 * @param filePath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	private static boolean deleteLocalDirectory(String filePath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
//		if (!filePath.endsWith(File.separator)) {
//			filePath = filePath + File.separator;
//		}
		File dirFile = new File(filePath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			LogRecord.FileHandleErrorLogger.error("file not exist: " + "127.0.0.1/"
					+ filePath);
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteLocalFile(files[i].getAbsolutePath());
			}
			else { // 删除子目录
				flag = deleteLocalDirectory(files[i].getAbsolutePath());
			}
			if (!flag) {
				LogRecord.FileHandleErrorLogger.error("delete file fail: " + "127.0.0.1/"
						+ files[i].getAbsolutePath());
				return false;
			}
		}
		// 删除当前目录
		if (dirFile.delete()) {
			LogRecord.FileHandleInfoLogger.info("delete directory successful: " + "127.0.0.1/"
					+filePath);
			return true;
		} else {
			LogRecord.FileHandleErrorLogger.error("delete directory fail: " + "127.0.0.1/"
					+ filePath);
			return false;
		}
	}

	/**
	 * 根据路径删除指定的目录或者文件
	 *
	 * @param node
	 *            删除的文件所在的节点对象
	 * @param filePath
	 *            要删除的目录或者文件的绝对路径
	 * @return 删除成功返回 true，删除失败返回 false。
	 */
	public static boolean delete(DirectoryNode node, String filePath) {
		ServerNode serverNode = node.getServerNode();
		if (!CommonUtil.isRemoteServer(serverNode.Ip)) {
			File file = new File(filePath);
			// 判断目录或文件是否存在
			if (!file.exists()) { // 不存在返回 false
				LogRecord.FileHandleErrorLogger.error("file not exist: " + "127.0.0.1/"
						+ filePath);
				return false;
			} else {
				// 判断是否为文件
				if (file.isFile()&&!file.getName().equals("Fingerprint.sys")) { // 为文件时调用删除文件方法
					return deleteLocalFile(filePath);
				} else { // 为目录时调用删除目录方法
					return deleteLocalDirectory(filePath);
				}
			}
		} else {// 远程文件
			FTPClient ftpClient = FTPUtil.getFTPClientByServerNode(serverNode,
					false);
			// ftpClient.setControlEncoding(encoding); // 中文支持
			boolean re = false;
			try {
				String relativePath = filePath.substring(node.Path.length());
				boolean flag=ftpClient.changeWorkingDirectory(new String(relativePath.getBytes(),"ISO-8859-1"));
				if(flag){//刪除的是文件夹
					ftpClient.changeToParentDirectory();
					re=deleteRemoteDirectory(node,ftpClient,relativePath);//为了删除中文文件，必须加编码
					if(re==false) {
						LogRecord.FileHandleErrorLogger.error("directory path illegal: "+serverNode.Ip + "/"
								+ filePath);
						return false;
					}
				}else{//删除的是文件
					re=ftpClient.deleteFile(new String(relativePath.getBytes(),"ISO-8859-1"));
					if(re==false)
						LogRecord.FileHandleErrorLogger.error("file path illegal: "+serverNode.Ip + "/"
								+ filePath);
					else
						LogRecord.FileHandleInfoLogger.info("delete file successful: " + serverNode.Ip + "/"
								+ filePath);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogRecord.FileHandleErrorLogger.error(e.toString());
			}
			return re;
		}
	}

	/**
	 * 删除远程的服务器上的目录或者文件
	 * @param relativePath 目录或者文件
	 * @return 删除是否成功
	 */
	private static  boolean deleteRemoteDirectory(DirectoryNode directoryNode, FTPClient ftpClient, String relativePath) {
		String sourceFilePath=relativePath;
		try {
			relativePath=new String(relativePath.getBytes(),"ISO-8859-1");
			FTPFile[] files = ftpClient.listFiles(relativePath);
			for (FTPFile file : files) {
				String fileName=new String(file.getName().getBytes("ISO-8859-1"),"gb2312");
				if (file.isDirectory()) {
					deleteRemoteDirectory(directoryNode,ftpClient,sourceFilePath+ "/" +fileName);
				}
				if (file.isFile()) {
					if(ftpClient.deleteFile(relativePath + "/" + file.getName())) {
//						fileName=new String(fileName.getBytes("gb2312"), "utf-8");
						LogRecord.FileHandleInfoLogger.info("delete file successful: " + directoryNode.getServerNode().Ip + "/"
								+ directoryNode.Path + sourceFilePath + "/" + fileName);
					}
					else {
//						fileName=new String(fileName.getBytes("gb2312"), "utf-8");
						LogRecord.FileHandleErrorLogger.error("delete file fail: " + directoryNode.getServerNode().Ip + "/"
								+ directoryNode.Path + sourceFilePath + "/" + fileName);
					}
				}
			}
			boolean re=ftpClient.removeDirectory(relativePath);
			if(re==false) {
				return false;
			}else
				LogRecord.FileHandleInfoLogger.info("delete directory successful: " + directoryNode.getServerNode().Ip + "/"
						+ directoryNode.Path+sourceFilePath);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
