package pers.sharedFileSystem.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 节点
 * 
 * @author buaashuai
 *
 */
public class Node implements Serializable {
	/**
	 * 节点编号
	 */
	public String Id;

	/**
	 * 该节点的子节点
	 */
	public List<DirectoryNode> ChildNodes;
	/**
	 * <p>
	 * 对directoryNode来说，Path是从该节点所属的ServerNode到该节点的路径
	 * </p>
	 * <p>
	 * 对serverNode来说，Path是资源目录树根节点在该存储服务器上存储的根路径
	 * </p>
	 */
//	public String Path;

	public Node() {
		// 默认的节点白名单是全部文件类型

	}

	/**
	 * 获取该节点所属的根节点对象
	 * 
	 * @return 根节点对象
	 */
	public ServerNode getServerNode() {
		if (this instanceof ServerNode)
			return (ServerNode) this;
		if (this instanceof DirectoryNode)
			return ((DirectoryNode) this).ParentServerNode;
		return null;
	}
}
