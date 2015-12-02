package pers.sharedFileSystem.bloomFilterManager.hashFunctions;

/**
 * bloomfilter用到的所有hash函数的基类
 * 
 * @author buaashuai
 *
 */
public abstract class HashFunction {

	/**
	 * 
	 * @param key
	 *            需要被hash的字符串
	 * @return key对应的散列值
	 */
	public abstract long getHashCode(String key);
}