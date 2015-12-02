package pers.sharedFileSystem.systemFileManager;

import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.RuntimeType;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * 文件系统错误处理器
 * 
 * @author buaashuai
 */
public class MessageCodeHandler {

	private static Hashtable<String, String> messageCodeTable = new Hashtable<String, String>();
	private static final MessageCodeHandler messageHandler = new MessageCodeHandler();

	private MessageCodeHandler() {
		try {
			InputStream in = null;
			if (Config.runtimeType== RuntimeType.DEBUG) {
				in = this
						.getClass()
						.getResourceAsStream(
								"/pers/sharedFileSystem/systemFileManager/messageCode.properties");
				// System.out
				// .println(this
				// .getClass()
				// .getResource(
				// "/pers/sharedFileSystem/exception/errorcode.properties"));
			} else if (Config.runtimeType==RuntimeType.CLIENT){
				in = this
						.getClass()
						.getResourceAsStream(
								"/pers/sharedFileSystem/systemFileManager/messageCode.properties");
				// System.out
				// .println(this
				// .getClass()
				// .getResource(
				// "/pers/sharedFileSystem/exception/errorcode.properties"));
			}
			else if (Config.runtimeType==RuntimeType.SERVER){
				in = this
						.getClass()
						.getResourceAsStream(
								"/pers/sharedFileSystem/systemFileManager/messageCode.properties");
				// System.out
				// .println(this
				// .getClass()
				// .getResource(
				// "/pers/sharedFileSystem/exception/errorcode.properties"));
			}
			Properties p = new Properties();
			p.load(in);
			Enumeration e1 = p.keys();
			while (e1.hasMoreElements()) {
				String key = (String) e1.nextElement();
				messageCodeTable.put(key, p.getProperty(key));
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取某个错误码的返回值
	 * 
	 * @param messagecode
	 * @param otherInfo
	 *            其他需要返回的错误信息
	 * @return 错误码对应的返回值
	 */
	public static String getMessageInfo(int messagecode, String otherInfo) {
		if (CommonUtil.validateString(otherInfo))
			return messageCodeTable.get(messagecode + "") + "," + otherInfo;
		else
			return messageCodeTable.get(messagecode + "");
	}

}
