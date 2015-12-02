package pers.sharedFileSystem.entity;

import java.io.Serializable;

/**
 * 资源目录树节点的命名方式
 * 
 * @author buaashuai
 *
 */
public enum NodeNameType implements Serializable {
	/**
	 * 静态命名
	 */
	STATIC,
	/**
	 * 动态命名
	 */
	DYNAMIC
}