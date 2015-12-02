//package pers.sharedFileSystem.privateInterface;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.SocketException;
//import java.nio.charset.Charset;
//import java.util.Arrays;
//import java.util.List;
//
//import org.apache.commons.net.ftp.FTPClient;
//import org.apache.log4j.Logger;
//
//import pers.sharedFileSystem.entity.RootNode;
//
//public class ReadFTPFile {
//	private Logger logger = Logger.getLogger(ReadFTPFile.class);
//
//	/**
//	 * 去 服务器的FTP路径下上读取文件
//	 * 
//	 * @param ftpUserName
//	 * @param ftpPassword
//	 * @param ftpPath
//	 * @param FTPServer
//	 * @return
//	 */
//	public String readFileFromFTP(RootNode rootNode) {
//		InputStream in = null;
//		FTPClient ftpClient = null;
//		logger.info("开始读取绝对路径" + rootNode.Path + "文件!");
//		try {
//			ftpClient = FTPUtil.getFTPClient(rootNode.Ip, rootNode.Port,
//					rootNode.UserName, rootNode.Password);
//			ftpClient.setControlEncoding(fileNode.getEncoding()); // 中文支持
//			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
//			ftpClient.enterLocalPassiveMode();
//			ftpClient.changeWorkingDirectory(fileNode.getPath());
//			in = ftpClient.retrieveFileStream(fileNode.getName());
//		} catch (FileNotFoundException e) {
//			logger.error("没有找到" + fileNode.getPath() + "文件");
//			e.printStackTrace();
//			return csvFile;
//		} catch (SocketException e) {
//			logger.error("连接FTP失败.");
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//			logger.error("文件读取错误。");
//			e.printStackTrace();
//			return csvFile;
//		}
//		if (in != null) {
//			CsvReader reader = new CsvReader(in, Charset.forName(fileNode
//					.getEncoding()));
//			try {
//				CSVRow row = new CSVRow();
//				reader.readHeaders();
//				String[] headers = reader.getHeaders();
//				List<String> heads = Arrays.asList(headers);
//				row.setCells(heads);
//				csvFile.addRow(row);
//				while (reader.readRecord()) {
//					String[] data = reader.getValues();
//					List<String> dataList = Arrays.asList(data);
//					row = new CSVRow();
//					row.setCells(dataList);
//					csvFile.addRow(row);
//				}
//			} catch (IOException e) {
//				logger.error("文件读取错误。");
//				e.printStackTrace();
//				return csvFile;
//			} finally {
//				try {
//					ftpClient.disconnect();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		} else {
//			logger.error("in为空，不能读取。");
//			return csvFile;
//		}
//		return csvFile;
//	}
// }
