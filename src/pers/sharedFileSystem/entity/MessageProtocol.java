package pers.sharedFileSystem.entity;

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
	 * 消息内容
	 */
	public Hashtable<String,String>content;

	public MessageProtocol(){
		content=new Hashtable<String,String>();
	}
}
