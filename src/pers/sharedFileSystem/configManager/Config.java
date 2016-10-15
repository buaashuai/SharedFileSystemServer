package pers.sharedFileSystem.configManager;

import java.util.Collection;
import java.util.Hashtable;

import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.Node;
import pers.sharedFileSystem.entity.RuntimeType;
import pers.sharedFileSystem.entity.ServerNode;
import pers.sharedFileSystem.entity.SystemConfig;
import pers.sharedFileSystem.logManager.LogRecord;

/**
 * 配置文件类
 * <p>
 * 配置文件被解析之后的对象
 * </p>
 * 
 * @author buaashuai
 *
 */
public class Config {
	/**
	 * 标记此文件系统是处于调试阶段还是部署阶段
	 * <p>
	 * true 表示调试阶段<br/>
	 * false 表示部署阶段
	 * </p>
	 */
	public static final RuntimeType runtimeType = RuntimeType.DEBUG;

	/**
	 * 解析之后的文件系统配置文件对象
	 */
	public static SystemConfig SYSTEMCONFIG;

	/**
	 * 初始化加载配置文件
	 */
	static {
		ConfigParse configParse = new ConfigParse();
		try {
			LogRecord.RunningInfoLogger.info("start parse SystemConfig.xml.");
			configParse.parseSystemConfig();
			LogRecord.RunningInfoLogger.info("parse SystemConfig.xml successful.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}