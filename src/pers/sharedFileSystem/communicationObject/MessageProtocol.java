package pers.sharedFileSystem.communicationObject;

import pers.sharedFileSystem.entity.SenderType;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * 客户端和服务端之间的通讯协议
 * @author buaashuai
 */
public class MessageProtocol implements Serializable {
	/**
	 * 消息类型
	 */
	public MessageType messageType;
	/**
	 *  发送方类型，1：存储端，2：客户端
	 */
	public int senderType;
	/**
	 * 消息内容类型(状态码)
	 */
	public int messageCode;
	/**
	 * 消息内容
	 */
	public Object content;
}