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
	 * 资源目录名的前导符
	 * <p>
	 * 对于动态命名的资源目录，解析完资源目录之后，会在映射的资源目录名前面加上这个前导符 <br/>
	 * 例如：某个动态命名的资源目录名别映射到 activityId，则解析完之后映射之后的名为 *activityId <br/>
	 * 前导符不能出现在目录名之中，最后会被过滤掉
	 * </p>
	 */
	protected static String PREFIX = "@";

	public static String getPREFIX() {
		return PREFIX;
	}

	/**
	 * 解析之后的文件系统配置文件对象
	 */
	public static SystemConfig SYSTEMCONFIG;

	/**
	 * 解析之后的配置文件对象
	 */
	protected static Hashtable<String, ServerNode> SERVERNODES;
	/**
	 * SERVERNODES的副本，应用程序获取的是配置文件的副本，可以防止应用程序恶意更改文件系统的配置文件
	 */
	private static Hashtable<String, ServerNode> SERVERNODES_BACK;

	/**
	 * 根据节点id获取它所属的Node对象
	 * 
	 * @param nodeId
	 *            目录节点Id
	 * @return <p>
	 *         如果是serverNode就返回ServerNode对象副本<br/>
	 *         否则返回DirectoryNode对象副本
	 *         </p>
	 *         <p>
	 *             之所以返回副本，是因为如果一个方法中既包括FileAdapter对象，又包括DirectoryAdapter，则后面的对象会覆盖前面的对象
	 *         </p>
	 */
	public static Node getNodeByNodeId(String nodeId) {
		try {
			if (SERVERNODES.keySet().contains(nodeId)) {
				return CommonUtil.copyImplSerializable(SERVERNODES.get(nodeId));
			}
			Collection<ServerNode> serverNodes = SERVERNODES.values();
			for (ServerNode r : serverNodes) {
				if (r.DirectoryNodeTable.containsKey(nodeId))
					return CommonUtil.copyImplSerializable(r.DirectoryNodeTable.get(nodeId));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取配置文件
	 * 
	 * @return 返回配置文件的副本（防止外部代码对配置进行修改）
	 */
	public static Hashtable<String, ServerNode> getConfig() {
		if (SERVERNODES_BACK == null) {
			SERVERNODES_BACK = CommonUtil.copyImplSerializable(SERVERNODES);
		}
		return SERVERNODES_BACK;
	}

	/**
	 * 初始化加载配置文件
	 */
	static {
		ConfigParse configParse = new ConfigParse();
		try {
			LogRecord.RunningInfoLogger.info("start parse FileConfig.xml.");
			configParse.parseFileConfig();
			LogRecord.RunningInfoLogger.info("parse FileConfig.xml successful.");
			LogRecord.RunningInfoLogger.info("start parse SystemConfig.xml.");
			configParse.parseSystemConfig();
			LogRecord.RunningInfoLogger.info("parse SystemConfig.xml successful.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}