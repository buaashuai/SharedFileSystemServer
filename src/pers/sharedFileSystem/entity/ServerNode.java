package pers.sharedFileSystem.entity;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * 应用系统所对应某个的文件系统的根节点
 *
 * @author buaashuai
 *
 */
public class ServerNode extends Node implements Serializable {
	/**
	 * 存储服务器的Ip地址
	 */
	public String Ip;
	/**
	 * 存储服务器上FTP的端口号
	 */
	public Integer Port;
	/**
	 * 存储服务器上的文件系统服务端口
	 */
	public Integer ServerPort;
	/**
	 * ServerNode包含的节点id和目录节点对象的映射
	 */
	public Hashtable<String, DirectoryNode> DirectoryNodeTable;
	/**
	 * ServerNode服务器的用户名
	 */
	public String UserName;
	/**
	 * ServerNode服务器的密码
	 */
	public String Password;
	/**
	 * 对该服务器的子节点进行统计之后，得到的该服务器的文件删冗信息
	 */
	public RedundancyInfo ServerRedundancy;
	/**
	 * 该存储服务器运行是否正常
	 */
	public boolean isRunning;
	/**
	 * 该服务器的http访问路径
	 */
	public String URL;
	/**
	 * 打印节点信息
	 *
	 * @param tabs
	 *            缩进tab
	 */
	public void print(String tabs) {
		System.out.println(tabs + "id: " + Id);
		System.out.println(tabs + "Ip: " + Ip);
		System.out.println(tabs + "Port: " + Port);
		System.out.println(tabs + "ServerPort: " + ServerPort);
		System.out.println(tabs + "UserName: " + UserName);
		System.out.println(tabs + "Password: " + Password);
		System.out.println(tabs + "URL: " + URL);
		System.out.println(tabs + "ServerRedundancy: ");
		this.ServerRedundancy.print(tabs + "\t");
		System.out.println(tabs + "ChildNodes: "+ChildNodes.size());
		int num=0;
		for (DirectoryNode directoryNode : ChildNodes) {
			if(num>0)
				System.out.println("");
			num++;
			directoryNode.print(tabs + "\t");
		}
	}
}
