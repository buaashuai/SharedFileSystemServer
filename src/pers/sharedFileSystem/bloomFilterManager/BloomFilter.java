package pers.sharedFileSystem.bloomFilterManager;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import pers.sharedFileSystem.bloomFilterManager.hashFunctions.*;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.*;
import pers.sharedFileSystem.logManager.LogRecord;
import pers.sharedFileSystem.systemFileManager.Common;

/**
 * 布隆过滤器
 * 
 * @author buaashuai
 * 
 */
public class BloomFilter {
	/**
	 * 布隆过滤器槽数
	 */
	private  int Slot_SIZE;// 布隆每个 bitSets 包含 20亿 位 2000000000
	/**
	 * 布隆过滤器实例采用的hash函数个数
	 */
	private  int validHashFunctionNum;
	/**
	 * bit 数组（槽）
	 */
	private BitSet bitset;//最大1425601750
	/**
	 * 系统内置的全部hash函数
	 */
	private LinkedHashMap<String, HashFunction> hashFuncations;
	/**
	 * 布隆过滤器实例
	 */
	private static final BloomFilter bloomFilter = new BloomFilter();// ”饿汉式“单例模式可以保证线程安全

	/**
	 * 根据配置文件计算系统需要的hash函数个数（validHashFunctionNum），和布隆过滤器需要的槽数（Slot_SIZE）
	 */
	private void calculateHashFunctionNum(){
		String serverNodeName=Config.SYSTEMCONFIG.ServerNodeName;
		Node n=Config.getNodeByNodeId(serverNodeName);
		if(n==null||n instanceof DirectoryNode) {
			LogRecord.FileHandleErrorLogger.error("["+serverNodeName+"] is not a ServerNode id");
			return;
		}
		ServerNode node=(ServerNode)n;
		RedundancyInfo  serverRedundancy=node.ServerRedundancy;
		double m=serverRedundancy.MaxElementNum*(Math.log(serverRedundancy.FalsePositiveRate)/Math.log(0.6185));
		Slot_SIZE =(int)m;
		double k=0.7*m/serverRedundancy.MaxElementNum;
		validHashFunctionNum=(int)(k+1);
		LogRecord.RunningInfoLogger.info("BloomFilter need "+m+"( approximate to "+Slot_SIZE+" ) slots.");
		LogRecord.RunningInfoLogger.info("BloomFilter need "+k+"( approximate to "+validHashFunctionNum+" ) hash functions.");
		LogRecord.RunningInfoLogger.info("BloomFilter need "+m/(8*1024*1024)+" MB memory.");
	}
	/**
	 * 初始化hash函数阵列
	 */
	private void initHashFunction() {
		hashFuncations = new LinkedHashMap<String, HashFunction>();
		APHash apHash = new APHash();
		BKDRHash bkdrHash = new BKDRHash();
		DJBHash djbHash = new DJBHash();
		JSHash jsHash = new JSHash();
		MurmurHash murmurHash = new MurmurHash();
		Rabin64Hash rabin64Hash = new Rabin64Hash();
		RSHash rsHash = new RSHash();
		SDBMHash sdbmHash = new SDBMHash();
		TianlHash tianlHash = new TianlHash();
		ZendInlineHash zendInlineHash = new ZendInlineHash();
		ELFHash elfHash=new ELFHash();
		DEKHash dekHash=new DEKHash();
		ADDHash addHash=new ADDHash();
		RotatingHash rotatingHash=new RotatingHash();
		OneByOneHash oneByOneHash=new OneByOneHash();
		BernsteinHash bernsteinHash=new BernsteinHash();
		FNVHash fnvHash=new FNVHash();
		PJWHash pjwHash=new PJWHash();
		hashFuncations.put("MurmurHash", murmurHash);//1
		hashFuncations.put("Rabin64Hash", rabin64Hash);//2
		hashFuncations.put("ZendInlineHash", zendInlineHash);//3
		hashFuncations.put("TianlHash", tianlHash);//4
		hashFuncations.put("BKDRHash", bkdrHash);//5
		hashFuncations.put("APHash", apHash);//6
		hashFuncations.put("DJBHash", djbHash);//7
		hashFuncations.put("JSHash", jsHash);//8
		hashFuncations.put("RSHash", rsHash);//9
		hashFuncations.put("SDBMHash", sdbmHash);//10
		hashFuncations.put("ELFHash", elfHash);//11
		hashFuncations.put("DEKHash", dekHash);//12
		hashFuncations.put("ADDHash", addHash);//13
		hashFuncations.put("RotatingHash", rotatingHash);//14
		hashFuncations.put("OneByOneHash", oneByOneHash);//15
		hashFuncations.put("BernsteinHash", bernsteinHash);//16
		hashFuncations.put("FNVHash", fnvHash);//17
		hashFuncations.put("PJWHash", pjwHash);//18
	}

	/**
	 * 把指纹信息加载到内存
	 */
	private double loadFigurePrint(){
		double count=0;
		String serverNodeName=Config.SYSTEMCONFIG.ServerNodeName;
		Node n=Config.getNodeByNodeId(serverNodeName);
		if(n==null||n instanceof DirectoryNode) {
			LogRecord.FileHandleErrorLogger.error("["+serverNodeName+"] is not a ServerNode id");
			return -1;
		}
		ServerNode node=(ServerNode)n;

		FileInputStream fin = null;
		BufferedInputStream bis =null;
		ObjectInputStream oip=null;
		String filePath=node.ChildNodes.get(0).StorePath;//指纹信息的保存路径
		String fileName=Common.FINGERPRINT_NAME;
		if(!CommonUtil.validateString(filePath)){
			LogRecord.FileHandleErrorLogger.error("get Fingerprint error, filePath is null.");
			return -1;
		}
		filePath+="/"+ Common.SYSTEM_FILE_FOLDER_Name;
		File file = new File(filePath);
		if(!file.isDirectory()||!new File(filePath+"/"+fileName).exists()){
			LogRecord.FileHandleErrorLogger.error("Fingerprint.sys not exist. ["+filePath+"/"+fileName+"]");
			return -1;
		}
		try{
			fin = new FileInputStream(filePath+"/"+fileName);
			bis = new BufferedInputStream(fin);
			while (true) {
				try {
					oip = new ObjectInputStream(bis); // 每次重新构造对象输入流
				}catch (EOFException e) {
					// e.printStackTrace();
//                    System.out.println("已达文件末尾");// 如果到达文件末尾，则退出循环
					return count;
				}
				Object object = new Object();
				object = oip.readObject();
				if (object instanceof FingerprintInfo) { // 判断对象类型
					FingerprintInfo fInfo=(FingerprintInfo) object;
					addFingerPrint(fInfo.Md5);
					count++;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				if(oip!=null)
					oip.close();
				if(bis!=null)
					bis.close();
				if(fin!=null)
					fin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	/**
	 *  私有的默认构造函数
	 */
	private BloomFilter() {
		LogRecord.RunningInfoLogger.info("start calculate hashFunction num.");
		calculateHashFunctionNum();
		LogRecord.RunningInfoLogger.info("calculate hashFunction num successful.");
		LogRecord.RunningInfoLogger.info("start init hashFunction.");
		initHashFunction();
		LogRecord.RunningInfoLogger.info("init hashFunction successful.");
		bitset= new BitSet(Slot_SIZE);
		LogRecord.RunningInfoLogger.info("start load figurePrint.");
		double num=loadFigurePrint();
		LogRecord.RunningInfoLogger.info("load figurePrint successful.  total count: "+num);
	}

	/**
	 * 获取布隆过滤器实例
	 * @return
	 */
	public static BloomFilter getInstance() {
		return bloomFilter;
	}

	/**
	 * 计算hash值映射到BitSet中的具体位置
	 * 
	 * @param hash
	 *            哈希值
	 * @return 映射到BitSet中的位置索引
	 */
	private int mapToBitSet(long hash){
		BigDecimal m = BigDecimal.valueOf(Slot_SIZE);
		BigDecimal v = CommonUtil.readUnsignedLong(hash).divideAndRemainder(m)[1];// 两个BigDecimal相除,求余数
		return v.intValue();
	}

	/**
	 * 判断指纹是否存在
	 * 
	 * @param fingerprint
	 *            文件指纹
	 * @return 存在返回true，否则返回false
	 */
	public boolean isFingerPrintExist(String fingerprint){
		if (fingerprint == null || fingerprint == "") {
			return false;
		}
		boolean ret = true;
		Set<String> keys = hashFuncations.keySet();
		int i = 1;
		long hash = -1;
		int index = -1;
		for (String key : keys) {
			if (i > validHashFunctionNum)
				break;
			hash = hashFuncations.get(key).getHashCode(fingerprint);
			index = mapToBitSet(hash);
			// System.out.println(key + " ， " + hash + " ， " + index);
			ret = ret && bitset.get(index);
			i++;
		}
		return ret;
	}

	/**
	 * 向布隆过滤器中插入新的指纹信息
	 * 
	 * @param fingerprint
	 *            文件指纹
	 */
	public void addFingerPrint(String fingerprint) {
		if (fingerprint == null || fingerprint == "") {
			return;
		}
		Set<String> keys = hashFuncations.keySet();
		int i = 1;
		int index = -1;
		long hash = -1;
		for (String key : keys) {
			if (i > validHashFunctionNum)
				break;
			hash = hashFuncations.get(key).getHashCode(fingerprint);
			index = mapToBitSet(hash);
			bitset.set(index);
			i++;
		}
	}
}
