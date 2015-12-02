package pers.sharedFileSystem.entity;

import java.io.Serializable;

/**
 * 信息指纹的产生方式
 * 
 * @author buaashuai
 *
 */
public enum FingerGenerateType implements Serializable {
	/**
	 * 信息指纹由客户端产生
	 */
	CLIENT,
	/**
	 * 信息指纹由服务器产生
	 */
	SERVER
}
