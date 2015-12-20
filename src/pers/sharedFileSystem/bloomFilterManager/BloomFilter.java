package pers.sharedFileSystem.bloomFilterManager;

import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.*;

import pers.sharedFileSystem.bloomFilterManager.hashFunctions.*;
import pers.sharedFileSystem.communicationObject.FingerprintInfo;
import pers.sharedFileSystem.communicationObject.MessageProtocol;
import pers.sharedFileSystem.communicationObject.MessageType;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.*;
import pers.sharedFileSystem.logManager.LogRecord;
import pers.sharedFileSystem.networkManager.ConnStoreServerSocketAction;

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
	 * 资源目录树配置文件
	 */
	private Hashtable<String, ServerNode> fileConfig= Config.getConfig();
	/**
	 * 服务端配置文件
	 */
	private SystemConfig systemConfig = Config.SYSTEMCONFIG;
	/**
	 * 初始化指纹信息产生的线程集合（必须使用线程安全的集合，否则不能得到每个线程的状态）
	 */
	private Hashtable<String,ConnStoreServerSocketAction> initFingerprintThreads=new Hashtable<String,ConnStoreServerSocketAction>();

	/**
	 * 根据配置文件计算系统需要的hash函数个数（validHashFunctionNum），和布隆过滤器需要的槽数（Slot_SIZE）
	 */
	private void calculateHashFunctionNum(){
		double maxElement=0, falsePositiveRate=Double.MAX_VALUE;
		for(ServerNode sNode:Config.getConfig().values()){
			RedundancyInfo  serverRedundancy=sNode.ServerRedundancy;
			maxElement+=serverRedundancy.MaxElementNum;
			falsePositiveRate=Math.min(falsePositiveRate,serverRedundancy.FalsePositiveRate);
		}
//		System.out.println("m="+maxElement+" p="+falsePositiveRate);
		double m=maxElement*(Math.log(falsePositiveRate)/Math.log(0.6185));
		Slot_SIZE =(int)m;
		double k=0.7*m/maxElement;
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
	 * 给需要发送冗余验证消息的存储服务器列表中的每个服务器都发送“获取指纹信息列表”命令
	 */
	private boolean sendGetFingerprintListMessageToStoreNode(){

		MessageProtocol mes=new MessageProtocol();
		mes.messageType= MessageType.GET_FINGERPRINT_LIST;
		for(String id:systemConfig.redundancyServerIds){
			ServerNode sn=fileConfig.get(id);
			try {
				Socket st=new Socket(sn.Ip, sn.ServerPort);
				ObjectOutputStream oos = new ObjectOutputStream(
						st.getOutputStream());
				oos.writeObject(mes);
				oos.flush();
				LogRecord.RunningInfoLogger.info("send GET_FINGERPRINT_LIST comand to"+sn.Ip+":"+sn.ServerPort);
				ConnStoreServerSocketAction socketAction = new ConnStoreServerSocketAction(st);
				Thread thread = new Thread(socketAction);
				initFingerprintThreads.put(sn.Ip,socketAction);
				thread.start();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	/**
	 * 把指纹信息加载到内存
	 */
	private double loadFigurePrint(){
		int count=0;
		boolean res=sendGetFingerprintListMessageToStoreNode();
		if(res){
			int num=initFingerprintThreads.size();
			//轮询检查是否指纹信息是否已经加载完毕
			boolean isRunning=true;//是否需要轮询
			while (isRunning){
				LogRecord.RunningInfoLogger.info("query if all storeNode finish GET_FINGERPRINT_LIST.");
				int n=0;
				for(ConnStoreServerSocketAction ac:initFingerprintThreads.values()){
					if(ac.isStop()){
						n++;
					}
					if(n==num){//全部查找都结束，并且都没有找到
						isRunning=false;
						break;
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
			//找到之后统计总数
			for(ConnStoreServerSocketAction ac:initFingerprintThreads.values()){
				count+=ac.getFingerprintNum();
			}
		}else{
			//发送失败通知其他获取指纹信息的线程停止继续获取
			for(ConnStoreServerSocketAction ac:initFingerprintThreads.values()){
				if(!ac.isStop()){
					ac.overThis();
				}
			}
		}
		//移除本次产生的所有线程对象，这样这些线程对象会被垃圾回收，从而释放占用的内存
		initFingerprintThreads.clear();
		return count;
	}
	/**
	 *  私有的默认构造函数
	 */
	private BloomFilter() {

	}

	/**
	 * 初始化布隆过滤器，（必须在外部进行初始化（不能将初始化放在BloomFilter()里面）否则查加载指纹信息里面的轮询函数永远停不下来）
	 */
	public void initBloomFilter(){
		LogRecord.RunningInfoLogger.info("start calculate hashFunction num.");
		calculateHashFunctionNum();
		LogRecord.RunningInfoLogger.info("calculate hashFunction num successful.");
		LogRecord.RunningInfoLogger.info("start init hashFunction.");
		initHashFunction();
		LogRecord.RunningInfoLogger.info("init hashFunction successful.");
		bitset= new BitSet(Slot_SIZE);
		LogRecord.RunningInfoLogger.info("start load figurePrint.");
		double num=loadFigurePrint();
		LogRecord.RunningInfoLogger.info("load figurePrint successful.  total count: " + num);
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