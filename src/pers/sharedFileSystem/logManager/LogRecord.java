package pers.sharedFileSystem.logManager;

import org.apache.log4j.Logger;

public class LogRecord {
	/**
	 * 【文件操作】普通消息日志
	 */
	public static Logger FileHandleInfoLogger = Logger.getLogger("FileHandleInfoLogger");
	/**
	 * 【文件操作】错误消息日志
	 */
	public static Logger FileHandleErrorLogger = Logger.getLogger("FileHandleErrorLogger");
	/**
	 * 【文件系统运行时】普通消息日志
	 */
	public static Logger RunningInfoLogger = Logger.getLogger("RunningInfoLogger");
	/**
	 * 【文件系统运行时】错误消息日志
	 */
	public static Logger RunningErrorLogger = Logger.getLogger("RunningErrorLogger");
}
