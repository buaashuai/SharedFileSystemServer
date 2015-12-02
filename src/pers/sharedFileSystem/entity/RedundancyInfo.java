package pers.sharedFileSystem.entity;

import java.io.Serializable;

/**
 * 文件删冗信息类
 * 
 * @author buaashuai
 *
 */
public class RedundancyInfo implements Serializable {
	/**
	 * 是否进行删冗
	 */
	public boolean Switch;
	/**
	 * 最大存储元素数
	 */
	public double MaxElementNum;
	/**
	 * 误报率
	 */
	public double FalsePositiveRate;
	/**
	 * 信息指纹产生方式
	 */
	public FingerGenerateType FingerGenType;
	/**
	 * 指纹产生方式为客户端时，指纹映射的属性名
	 */
	public String Property;

	public RedundancyInfo() {
		// 系统默认不对任何节点进行删冗
		Switch = false;
		MaxElementNum = 0;
		FalsePositiveRate = Integer.MAX_VALUE;
		FingerGenType = FingerGenerateType.SERVER;
		Property = "";
	}

	/**
	 * 打印
	 */
	public void print(String tabs) {
		System.out.println(tabs + "Switch: " + Switch);
		System.out.println(tabs + "MaxElementNum: " + MaxElementNum);
		System.out.println(tabs + "FalsePositiveRate: " + FalsePositiveRate);
		System.out.println(tabs + "FingerGenType: " + FingerGenType.toString());
		System.out.println(tabs + "Property: " + Property);
	}
}
