package pers.sharedFileSystem.exceptionManager;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.RuntimeType;

/**
 * 文件系统错误处理器
 * 
 * @author buaashuai
 */
public class ErrorHandler {

	private static Hashtable<String, String> errorCodeTable = new Hashtable<String, String>();
	private static final ErrorHandler errorHandler = new ErrorHandler();

	private ErrorHandler() {
		try {
			InputStream in = null;
			if (Config.runtimeType== RuntimeType.DEBUG) {
				in = this
						.getClass()
						.getResourceAsStream(
								"/pers/sharedFileSystem/exceptionManager/errorcode.properties");
				// System.out
				// .println(this
				// .getClass()
				// .getResource(
				// "/pers/sharedFileSystem/exception/errorcode.properties"));
			} else if (Config.runtimeType==RuntimeType.CLIENT){
				in = this
						.getClass()
						.getResourceAsStream(
								"/pers/sharedFileSystem/exceptionManager/errorcode.properties");
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
								"/pers/sharedFileSystem/exceptionManager/errorcode.properties");
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
				errorCodeTable.put(key, p.getProperty(key));
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取某个错误码的返回值
	 * 
	 * @param errorcode
	 * @param otherInfo
	 *            其他需要返回的错误信息
	 * @return 错误码对应的返回值
	 */
	public static String getErrorInfo(int errorcode, String otherInfo) {
		if (CommonUtil.validateString(otherInfo))
			return errorCodeTable.get(errorcode + "") + "," + otherInfo;
		else
			return errorCodeTable.get(errorcode + "");
	}

}
