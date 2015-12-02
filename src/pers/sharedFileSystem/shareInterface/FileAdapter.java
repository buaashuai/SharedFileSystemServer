package pers.sharedFileSystem.shareInterface;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.net.ftp.FTPClient;

import pers.sharedFileSystem.bloomFilterManager.hashFunctions.SHA1_MD5;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.convenientUtil.AdvancedFileUtil;
import pers.sharedFileSystem.convenientUtil.CommonFileUtil;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.*;
import pers.sharedFileSystem.ftpManager.FTPUtil;
import pers.sharedFileSystem.logManager.LogRecord;
import pers.sharedFileSystem.networkManager.FileSystemClient;

/**
 * 文件适配器提供文件操作的基本功能
 *
 * @author buaashuai
 *
 */
public class FileAdapter extends Adapter {
	/**
	 * 文件输入流
	 */
	private InputStream inputStream;
	/**
	 * 单独的文件名
	 */
	private String fileName;

	/**
	 * 通过文件输入流对文件适配器进行初始化
	 *
	 * @param inputStream
	 */
	public FileAdapter(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public FileAdapter() {

	}

	/**
	 * 根据指纹信息验证文件是否存在
	 *
	 * @param desNodeId
	 *            目的节点id
	 * @param figurePrint
	 *            文件指纹信息
	 * @return 文件存在返回文件绝对地址 ，否则返回false
	 */
	public static  JSONObject isFileExistInBloomFilter(String desNodeId, String figurePrint) {
		Feedback feedback = null;
		try {
			Node node = Config.getNodeByNodeId(desNodeId);
//			Socket socket = FileSystemClient.getSocketByServerNodeId(node
//					.getServerNode().Id);
			Socket socket=new Socket(node.getServerNode().Ip, node.getServerNode().ServerPort);
			if(socket==null){
				feedback = new Feedback(3001 ,"");
				LogRecord.RunningErrorLogger.error("socket not initial ["+node.getServerNode().Ip+"]");
				return feedback.toJsonObject();
			}
			MessageProtocol queryMessage = new MessageProtocol();
			queryMessage.messageType = MessageType.CHECK_REDUNDANCY;
			queryMessage.content.put("figurePrint",figurePrint);
			queryMessage.content.put("desNodeId",desNodeId);
			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());
			oos.writeObject(queryMessage);
			ObjectInputStream ois = new ObjectInputStream(
					socket.getInputStream());
			MessageProtocol replyMessage = (MessageProtocol) ois.readObject();
			if (replyMessage != null
					&& replyMessage.messageType == MessageType.REPLY_CHECK_REDUNDANCY) {
				if (replyMessage.content.get("messageCode").equals("4002")) {
					feedback = new Feedback(3010 ,"");
					return feedback.toJsonObject();
				}else if (replyMessage.content.get("messageCode").equals("4000")){
					feedback = new Feedback(3000 ,"");
					//并返回指纹信息
					feedback.addFeedbackInfo(replyMessage.content.get("filePath"));
					return feedback.toJsonObject();
				}else if(replyMessage.content.get("messageCode").equals("4003")){
					feedback = new Feedback(3015 ,"");
					return feedback.toJsonObject();
				}
			}
		} catch (Exception e) {
			LogRecord.RunningErrorLogger.error(e.toString());
			feedback = new Feedback(3001 ,e.toString());
			return feedback.toJsonObject();
		}
		feedback = new Feedback(3001 ,"");
		return feedback.toJsonObject();
	}

	/**
	 * 向存储服务器发送添加映射信息指令
	 * @param desNodeId
	 *            目的节点id
	 * @param keyPath 节目录节点相对路径（相对该节点的根存储根路径）
	 * @param otherFilePath 该目录节点下的存储在其他文件夹里面的冗余文件的相对路径
	 */
	private JSONObject sendRedundancyFileStoreInfoMessage(String desNodeId, String keyPath, String otherFilePath){
		Feedback feedback = null;
		try {
			Node node = Config.getNodeByNodeId(desNodeId);
//			Socket socket = FileSystemClient.getSocketByServerNodeId(node
//					.getServerNode().Id);
			Socket socket=new Socket(node.getServerNode().Ip, node.getServerNode().ServerPort);
			if(socket==null){
				feedback = new Feedback(3001 ,"");
				LogRecord.RunningErrorLogger.error("socket not initial ["+node.getServerNode().Ip+"]");
				return feedback.toJsonObject();
			}
			MessageProtocol queryMessage = new MessageProtocol();
			queryMessage.messageType = MessageType.ADD_REDUNDANCY_INFO;
			queryMessage.content.put("desNodeId",desNodeId);
			queryMessage.content.put("keyPath",keyPath);
			queryMessage.content.put("otherFilePath",otherFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());
			oos.writeObject(queryMessage);
			ObjectInputStream ois = new ObjectInputStream(
					socket.getInputStream());
			MessageProtocol replyMessage = (MessageProtocol) ois.readObject();
			if (replyMessage != null
					&& replyMessage.messageType == MessageType.REPLY_ADD_REDUNDANCY_INFO) {
				if (replyMessage.content.get("messageCode").equals("4002")) {
					feedback = new Feedback(3010 ,"");
					return feedback.toJsonObject();
				}else if (replyMessage.content.get("messageCode").equals("4000")){
					feedback = new Feedback(3000 ,"");
					//并返回指纹信息
					feedback.addFeedbackInfo(replyMessage.content.get("filePath"));
					return feedback.toJsonObject();
				}else if(replyMessage.content.get("messageCode").equals("4003")){
					feedback = new Feedback(3015 ,"");
					return feedback.toJsonObject();
				}
			}
		} catch (Exception e) {
			LogRecord.RunningErrorLogger.error(e.toString());
			feedback = new Feedback(3001 ,e.toString());
			return feedback.toJsonObject();
		}
		feedback = new Feedback(3001 ,"");
		return feedback.toJsonObject();
	}
	/**
	 * 给服务端的文件系统发送指纹置位命令
	 *
	 * @param desNodeId
	 *            目的节点id
	 * @param figurePrint
	 *            文件指纹信息
	 */
	public void sendAddFigurePrintMessage(String desNodeId, FingerprintInfo figurePrint) {
		try {
			Node node = Config.getNodeByNodeId(desNodeId);
//			Socket socket = FileSystemClient.getSocketByServerNodeId(node
//					.getServerNode().Id);
			Socket socket=new Socket(node.getServerNode().Ip, node.getServerNode().ServerPort);
			if(socket==null){
				LogRecord.RunningErrorLogger.error("socket not initial ["+node.getServerNode().Ip+"]");
				return;
			}
			MessageProtocol queryMessage = new MessageProtocol();
			queryMessage.messageType = MessageType.ADD_FINGERPRINT;
			queryMessage.content.put("figurePrint",figurePrint.Md5);
			queryMessage.content.put("FilePath",figurePrint.FilePath);
			queryMessage.content.put("FileName",figurePrint.FileName);
			queryMessage.content.put("desNodeId",desNodeId);
			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());
			oos.writeObject(queryMessage);
		} catch (Exception e) {
			LogRecord.RunningErrorLogger.error(e.toString());
		}
	}

	/**
	 *
	 * @param sourceNodeId
	 *            此文件存储节点ID
	 * @param fileName
	 *            带后缀名的文件
	 * @param parms
	 *            路径参数
	 */
	public FileAdapter(String sourceNodeId, String fileName,
					   Map<String, String> parms) {
		Node n=Config.getNodeByNodeId(sourceNodeId);
		if(n==null||n instanceof ServerNode) {
			LogRecord.FileHandleErrorLogger.error("["+sourceNodeId+"] is not a DirectoryNode id");
			return;
		}
		DirectoryNode node = (DirectoryNode)n;
		this.NODE = node;
		this.NODEID = sourceNodeId;
		this.fileName = fileName;
		// 初始化节点的相对路径
		JSONObject nodePathFeed = CommonFileUtil.initFilePath(sourceNodeId,
				parms, false);
		// 在指定的节点下生成文件夹路径
		JSONArray jsonArray = nodePathFeed.getJSONArray("Info");
		if (jsonArray.size() < 1)
			this.FILEPATH = node.StorePath + "/" + fileName;
		else {
			String filePath = jsonArray.getString(0);
			this.FILEPATH = node.StorePath + filePath + "/"
					+ fileName;
		}
		if(!new File(this.FILEPATH).exists()){
			LogRecord.FileHandleErrorLogger.error("file not exist: "
					+ node.getServerNode().Ip + "/" + this.FILEPATH);
		}
	}

	/**
	 * 通过文件路径对文件适配器进行初始化
	 *
	 */
	public FileAdapter(String filePath) {
		this.FILEPATH = filePath;
	}

	/**
	 * 获取文件的内容
	 *
	 * @return
	 */
	public JSONObject getFileContent() {
		Feedback feedback = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(this.FILEPATH));
			String line = "";
			StringBuffer buffer = new StringBuffer();
			while ((line = br.readLine()) != null) {
				buffer.append(line + "\r\n");
			}
			String fileContent = buffer.toString();
			feedback = new Feedback(3000, "");
			feedback.addFeedbackInfo(fileContent);
			return feedback.toJsonObject();
		} catch (Exception e) {
			e.printStackTrace();
			feedback = new Feedback(3001, "");
			return feedback.toJsonObject();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LogRecord.FileHandleErrorLogger.error(e.toString());
			}
		}
	}

	/**
	 * 删除该文件
	 *
	 * @return 删除过程的反馈
	 */
	public JSONObject delete() {
		Feedback feedback = null;
		if(this.fileName.equals("Fingerprint.sys")){
			feedback = new Feedback(3013, "");
			return feedback.toJsonObject();
		}
		if (AdvancedFileUtil.delete(this.NODE, this.FILEPATH))
			feedback = new Feedback(3000, "");
		else
			feedback = new Feedback(3001, "");
		return feedback.toJsonObject();
	}

	/**
	 * 生成包含该name的PNG图片
	 *
	 * @param name
	 *            图片中显示的文字
	 * @param font
	 *            图片中显示的文字的字体
	 */
	public JSONObject createPngImage(Font font, String name) {
		Feedback feedback = null;
		try {
			String str = name;
			File outFile = new File(this.FILEPATH);
			// 获取font的样式应用在str上的整个矩形
			int unitHeight = font.getSize();// 获取单个字符的高度,
			// 获取整个str用了font样式的宽度这里用四舍五入后+15保证高度绝对能容纳这个字符串作为图片的高度
			int height = 0;// unitHeight * str.length() + 16;
			int len = str.length();
			if (len == 1)
				height = 64;
			else if (len == 2)
				height = 128;
			else if (len <= 4) {
				height = 64 * 4;
			} else if (len <= 8) {
				height = 64 * 8;
			} else {
				height = 64 * 16;
			}
			int width = 64;// 把单个字符的宽度+2保证宽度绝对能容纳字符串作为图片的宽度
			System.out.println(width + "," + height);
			// 创建图片
			BufferedImage image = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_BGR);
			Graphics2D g2d = image.createGraphics();
			// ---------- 增加下面的代码使得背景透明 -----------------
			image = g2d.getDeviceConfiguration().createCompatibleImage(width,
					height, Transparency.TRANSLUCENT);
			g2d.dispose();
			g2d = image.createGraphics();
			// ---------- 背景透明代码结束 -----------------
			// 画图
			g2d.setColor(new Color(0, 0, 0));
			g2d.setFont(font);// 设置画笔字体
			int yIndex = 0;
			for (int i = 0; i < str.length(); i++) {
				yIndex += font.getSize();
				g2d.drawString(str.substring(i, i + 1), 2, yIndex);// 画出字符串
			}
			g2d.dispose();
			ImageIO.write(image, "png", outFile);// 输出png图片
			feedback = new Feedback(3000, "");
			feedback.addFeedbackInfo(this.FILEPATH.substring(this.NODE.StorePath.length()));// fileName已经带有后缀名
			return feedback.toJsonObject();// (baseFilePath.length()).replaceAll("\\\\",
			// "/");
		} catch (Exception e) {
			e.printStackTrace();
			LogRecord.FileHandleErrorLogger.error(e);
			feedback = new Feedback(3001, "");
			return feedback.toJsonObject();
		}
	}

	/**
	 * 获取文件输入流(默认GBK编码)
	 *
	 * @return 文件的输入流
	 */
	public InputStream getFileInputStream() {
		return getFileInputStream("gbk");
	}

	/**
	 * 根据编码格式获取文件输入流
	 *
	 * @param encoding
	 *            输入流的编码
	 * @return 文件的输入流
	 */
	public InputStream getFileInputStream(String encoding) {
		try {
			Node n = new Node();
			if (n instanceof DirectoryNode) {
			}
			ServerNode serverNode = this.NODE.getServerNode();
			String ip = serverNode.Ip;
			if (!CommonUtil.isRemoteServer(ip))
				return new FileInputStream(this.FILEPATH);
			else {
				FTPClient ftpClient = FTPUtil.getFTPClientByServerNode(
						serverNode, false);
				ftpClient.setControlEncoding(encoding); // 中文支持
				String relativePath = this.FILEPATH.substring(this.NODE.StorePath
						.length());
				// 因为filePath带文件名，因此需要去除文件名
				if (relativePath.length() > 0) {
					relativePath = relativePath.substring(0,
							relativePath.indexOf(this.fileName));
					ftpClient.changeWorkingDirectory(relativePath);
				}
				// FTPFile[] files = ftpClient.listFiles();
				return ftpClient.retrieveFileStream(this.fileName);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogRecord.FileHandleErrorLogger.error(e.toString());
			return null;
		}
	}

	/**
	 * 使用ByteArrayOutputStream可以让inputstream重读多次
	 *
	 * @param inStream
	 *            文件输入流
	 * @return 转换之后的ByteArrayOutputStream
	 */
	private ByteArrayOutputStream inputStreamToByte(InputStream inStream) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = inStream.read(buffer)) > -1) {
				baos.write(buffer, 0, len);
			}
			baos.flush();
			return baos;
		} catch (IOException e) {
			LogRecord.FileHandleErrorLogger.error(e.toString());
		}
		return null;
	}

	/**
	 * 将文件输入流写入磁盘
	 *
	 * @param stream2
	 *            文件输入流
	 * @param destRootNode
	 *            目的根节点对象
	 * @param destFilePath
	 *            磁盘绝对路径
	 * @param fileName
	 *            保存到磁盘的文件名
	 * @return 保存的反馈信息
	 */
	private  boolean saveFile(InputStream stream2, DirectoryNode destRootNode,
					 String destFilePath, String fileName) {
		OutputStream outputStream = null;
		String fullPath = destFilePath + fileName;
		if (destFilePath.charAt(destFilePath.length() - 1) != '/')
			fullPath = destFilePath + "/" + fileName;
		try {
			String ip = destRootNode.getServerNode().Ip;
			if (!CommonUtil.isRemoteServer(ip)) {// 保存到本地
				File newFile = new File(fullPath);
				newFile.createNewFile();
				byte[] bs = new byte[1024];// 1Kb
				int len = stream2.read(bs);
				outputStream = new FileOutputStream(newFile);
				while (len > 0) {
					outputStream.write(bs);
					bs = new byte[1024];// 1Kb
					len = stream2.read(bs);
				}
			} else {// 保存到远程服务器
				FTPClient ftpClient = null;
				if (this.NODE!=null&&destRootNode.getServerNode().Ip.equals(this.NODE.getServerNode().Ip))
					ftpClient = FTPUtil.getFTPClientByServerNode(destRootNode.getServerNode(),
							true);
				else {
					ftpClient = FTPUtil.getFTPClientByServerNode(destRootNode.getServerNode(),
							false);
				}
				String relativePath = destFilePath.substring(destRootNode.Path
						.length());
				ftpClient.changeWorkingDirectory(relativePath);
				ftpClient.storeFile(fileName, stream2);
			}
			return true;
		} catch (Exception e) {
			LogRecord.FileHandleErrorLogger.error(e.toString());
			return false;
		} finally {
			try {
				if (stream2 != null) {
					stream2.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (Exception e) {
				// TODO: handle exception
				LogRecord.FileHandleErrorLogger.error(e.toString());
			}
		}
	}

	/**
	 * 保存文件接口
	 *
	 * @param desNodeId
	 *            目的节点Id
	 * @param fileName
	 *            保存之后的文件名
	 * @param parms
	 *            动态路径相关的参数
	 */
	public JSONObject saveFileTo(String desNodeId, String fileName,
								 Map<String, String> parms) {
		Feedback feedback = null;
		String fingerPrint ="";
		Node n=Config.getNodeByNodeId(desNodeId);
		if(n==null||n instanceof ServerNode) {
			LogRecord.FileHandleErrorLogger.error("["+desNodeId+"] is not a DirectoryNode id");
			feedback = new Feedback(3011, desNodeId);
			return feedback.toJsonObject();
		}
		DirectoryNode node = (DirectoryNode)n;// 保存文件的目的节点
		ServerNode serverNode = node.getServerNode();// 保存文件的目的节点所属的根节点
		// 初始化节点的相对路径
		JSONObject nodePathFeed = CommonFileUtil.initFilePath(desNodeId,
				parms, false);
		if (nodePathFeed.getInt("Errorcode") != 3000) {
			return nodePathFeed;
		}
		// 获取文件夹路径
		JSONArray jsonArray = nodePathFeed.getJSONArray("Info");
		// 生成文件路径
		String destFilePath = node.StorePath;
		if(jsonArray.size()>0){
			destFilePath+=jsonArray.getString(0)+ "/";
		}
		try {
			if (inputStream == null && CommonUtil.validateString(this.FILEPATH)) {
				this.inputStream = getFileInputStream();
			}
			if (inputStream == null) {
				feedback = new Feedback(3009, this.fileName);
				LogRecord.FileHandleErrorLogger.error(feedback.getErrorInfo());
				return feedback.toJsonObject();
			}
			ByteArrayOutputStream baos = inputStreamToByte(inputStream);

			if (node.Redundancy.Switch) {//如果上传的节点需要进行文件删冗
				// 布隆过滤器置位
				if(node.Redundancy.FingerGenType== FingerGenerateType.CLIENT) {
					fingerPrint = parms.get(node.Redundancy.Property);
					if (!CommonUtil.validateString(fingerPrint)) {
						LogRecord.FileHandleErrorLogger.error("client miss fingerPrint");
						feedback = new Feedback(3012, "");
						return feedback.toJsonObject();
					}
				}
				else if(node.Redundancy.FingerGenType== FingerGenerateType.SERVER){
					InputStream stream3 = new ByteArrayInputStream(baos.toByteArray());
					SHA1_MD5 sha1_md5=new SHA1_MD5();
					fingerPrint = sha1_md5.digestFile(stream3,SHA1_MD5.MD5);

					if (!CommonUtil.validateString(fingerPrint)) {
						LogRecord.FileHandleErrorLogger.error("server generate fingerPrint fail");
						feedback = new Feedback(3014, "");
						return feedback.toJsonObject();
					}
					LogRecord.FileHandleInfoLogger.info("generate new md5:"+fingerPrint);
				}

				JSONObject re= isFileExistInBloomFilter(desNodeId, fingerPrint);
				if(re.getInt("Errorcode") == 3000){//表示指纹信息存在
					//给客户端返回文件存储的位置
					feedback = new Feedback(3000, "");
					String strP=re.getJSONArray("Info").getString(0).substring(node.StorePath.length());//strP是相对路径
					feedback.addFeedbackInfo(strP);
					feedback.addFeedbackInfo("repeat",true);
					String keyPath="";
					if(jsonArray.size()>0) {//存储到非根节点
						keyPath=jsonArray.getString(0) + "/";
					}
					else {//存储到根节点
						keyPath="/";
					}
					sendRedundancyFileStoreInfoMessage(desNodeId,keyPath, strP);//向存储服务器发送添加映射信息指令
					return feedback.toJsonObject();
				}

			}

			InputStream stream1 = new ByteArrayInputStream(baos.toByteArray());

			byte[] bs = new byte[1024];// 1Kb
			int len = 0;
			len = stream1.read(bs);
			// 第一次读取输入流，用来判断文件类型， 以及该文件类型是否符合节点的白名单规定
			FileType fileType = CommonFileUtil.getFileType(bs);// 目前没法识别XML、TXT这些文本文件，只能通过后缀名识别，因为他们的文件头不固定
			// 文本文件无法用文件头进行识别，因此默认为用户上传的文件后缀
			String fileSuffix=parms.get("fileSuffix");

			if (!CommonFileUtil.isLegalFile(bs, node, fileType,fileSuffix)) {
				feedback = new Feedback(3006, "文件类型：" + fileType);
				LogRecord.FileHandleErrorLogger.error(feedback.getErrorInfo());
				return feedback.toJsonObject();
			}

			// 判断fileName是否有后缀名
			if (!fileName.contains(".")) {
				if (fileType == null) {
					if(CommonUtil.validateString(fileSuffix))
						fileName += "." + fileSuffix;
					else{
						LogRecord.FileHandleErrorLogger.error("client miss fileSuffix");
						feedback = new Feedback(3016, "");
						return feedback.toJsonObject();
					}
				}else
					fileName += "." + fileType.toString();
			}
			// 判断文件是否存在
			boolean type = false;
			if (this.NODE != null
					&& serverNode.Ip.equals(this.NODE.getServerNode().Ip))
				type = true;
			if (AdvancedFileUtil.isFileExist(node, destFilePath,
					fileName, type)) {
				feedback = new Feedback(3002, "");
				LogRecord.FileHandleErrorLogger.error(feedback.getErrorInfo()
						+ " [" + serverNode.Ip + "/" + destFilePath + fileName
						+ "]");
				return feedback.toJsonObject();
			}

			InputStream stream2 = new ByteArrayInputStream(baos.toByteArray());

			// 将文件流写入磁盘
			boolean result = saveFile(stream2, node, destFilePath,
					fileName);
			if (result) {
				FingerprintInfo fInfo=new FingerprintInfo(fingerPrint,destFilePath,fileName);
				sendAddFigurePrintMessage(desNodeId, fInfo);//向布隆过滤器添加指纹
				feedback = new Feedback(3000, "");
				if(jsonArray.size()>0) {
					feedback.addFeedbackInfo(jsonArray.getString(0) + "/"
							+ fileName);// fileName已经带有后缀名
					LogRecord.FileHandleInfoLogger.info("save file successful: "
							+ serverNode.Ip + "/" + node.StorePath
							+ jsonArray.getString(0) + "/" + fileName);
				}
				else {
					feedback.addFeedbackInfo("/"
							+ fileName);// fileName已经带有后缀名
					LogRecord.FileHandleInfoLogger.info("save file successful: "
							+ serverNode.Ip + "/" + node.StorePath
							+ "/" + fileName);
				}
				feedback.addFeedbackInfo("repeat",false);
			} else {
				feedback = new Feedback(3001, "");
			}
			return feedback.toJsonObject();
		} catch (Exception e) {
			feedback = new Feedback(3001, e.toString());
			LogRecord.FileHandleErrorLogger.error(e.toString());
			return feedback.toJsonObject();
		}
	}
}
