package pers.sharedFileSystem.convenientUtil;

import pers.sharedFileSystem.logManager.LogRecord;

import java.io.*;
import java.math.BigDecimal;

/**
 * 文件系统基础工具包
 * 
 * @author buaashuai
 *
 */
public class CommonUtil {
	/**
	 * 验证字符串的有效性
	 * 
	 * @param string
	 *            待验证的字符串
	 * @return 字符串不为null 且 不为空字符串 则返回true
	 */
	public static boolean validateString(String string) {
		return string != null && !string.isEmpty();
	}

	/**
	 * 判断字符串是否是空
	 * @param string
	 * @return 字符串是null 或者空字符串 则返回true
	 */
	public static boolean isEmpty(String string){
		return string == null || string.isEmpty();
	}
	/**
	 * 深层拷贝 - 需要类继承序列化接口
	 * 
	 * @param <T>
	 *            待拷贝对象的类型
	 * @param obj
	 *            待拷贝的对象
	 * @return 拷贝的另一个对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copyImplSerializable(T obj){
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;

		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;

		Object o = null;
		// 如果子类没有继承该接口，这一步会报错
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			bais = new ByteArrayInputStream(baos.toByteArray());
			ois = new ObjectInputStream(bais);

			o = ois.readObject();
			return (T) o;
		} catch (Exception e) {
			LogRecord.RunningErrorLogger.error("object not contains object that  implements Serializable. ");
			return null;
		} finally {
			try {
				baos.close();
				oos.close();
				bais.close();
				ois.close();
			} catch (Exception e2) {
				// 这里报错不需要处理
			}
		}
	}

	/**
	 * 将long类型的数值转成无符号数
	 *
	 * @param value
	 *            需要转换的数
	 * @return 转换之后的值
	 */
	public static BigDecimal readUnsignedLong(long value){
		if (value >= 0)
			return new BigDecimal(value);
		long lowValue = value & 0x7fffffffffffffffL;
		return BigDecimal.valueOf(lowValue)
				.add(BigDecimal.valueOf(Long.MAX_VALUE))
				.add(BigDecimal.valueOf(1));
	}

	/**
	 * 判断ip地址是否是远程主机
	 * @param ip
	 * @return 是远程主机返回true，否则返回false
	 */
	public static  boolean isRemoteServer(String ip){
		return !ip.equals("127.0.0.1") && !ip.equals("localhost");
	}

	/**
	 * FIND_REDUNDANCY命令的编号
	 */
	private static  double seriesId=0;
	/**
	 * 生成FIND_REDUNDANCY命令的编号
	 * @return
	 */
	public static Double generateCheckId(){
		seriesId++;
		if(Double.MAX_VALUE==seriesId)
			seriesId=1;
		return seriesId;
	}
}
