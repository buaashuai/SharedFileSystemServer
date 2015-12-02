package pers.sharedFileSystem.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源目录树的目录节点
 * <p>
 * 本类是对资源目录树中文件夹节点的抽象
 * </p>
 * 
 * @author buaashuai
 *
 */
public class DirectoryNode extends Node implements Serializable {
	/**
	 * 该文件删冗信息
	 */
	public RedundancyInfo Redundancy;
	/**
	 * 该节点接收文件的白名单列表
	 */
	public List<FileType> WhiteList;
	/**
	 * 节点命名方式
	 */
	public NodeNameType NameType;
	/**
	 * 节点的命名方式是静态命名时，节点名
	 */
	public String Name;
	/**
	 * 节点的命名方式是动态命名时，节点名映射的属性名
	 */
	public String Property;

	/**
	 * 该节点所属的根节点
	 */
	public ServerNode ParentServerNode;

	/**
	 * 该节点的存储根路径
	 */
	public String StorePath;
	/**
	 * Path是从该节点所属的根节点到该节点的路径，相对路径
	 */
	public String Path;

	public DirectoryNode(){
		WhiteList = new ArrayList<FileType>();
		WhiteList.add(FileType.ANY);
		Redundancy = new RedundancyInfo();
	}
	/**
	 * 打印节点信息
	 * 
	 * @param tabs
	 *            缩进tab
	 */
	public void print(String tabs) {
		System.out.println(tabs + "id: " + Id);
		System.out.println(tabs + "NameType: " + NameType.toString());
		System.out.println(tabs + "Name: " + Name);
		System.out.println(tabs + "Property: " + Property);
		System.out.println(tabs + "ParentServerNode: " + ParentServerNode.Ip);
		System.out.println(tabs + "StorePath: " + StorePath);
		System.out.println(tabs + "path: " + Path);
		System.out.println(tabs + "Redundancy: ");
		this.Redundancy.print(tabs + "\t");
		System.out.println(tabs + "WhiteList: " + WhiteList.toString());
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
