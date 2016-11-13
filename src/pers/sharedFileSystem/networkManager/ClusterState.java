package pers.sharedFileSystem.networkManager;

import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.Node;
import pers.sharedFileSystem.entity.ServerNode;
import pers.sharedFileSystem.logManager.LogRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 集群状态
 */
public class ClusterState {

    private static ClusterState instance;

    private ClusterState (){
        storesList = new ConcurrentHashMap<>();
        clientsList = new ConcurrentHashMap<>();
        serverNodeList = new Hashtable<>();
    }

    /**
     * 已连接的存储服务器的配置集合
     */
    private Hashtable<String, ServerNode> serverNodeList;

    /**
     * 已连接的存储端集合，ipPort——》SocketAction
     * 存储管理子系统的集合
     */
    private ConcurrentHashMap<String, SocketAction> storesList;

    /**
     * 已连接的客户端集合，ipPort——》SocketAction
     * 存储接口管理子系统的集合
     */
    private ConcurrentHashMap<String, SocketAction> clientsList;

    /**
     * 集群中的指纹信息总数
     */
    private double fingerprintNum;

    public static synchronized ClusterState getInstance(){
        if(instance ==null){
            instance = new ClusterState();
        }
        return instance;
    }

    /**
     * 某个存储服务器连接中断
     * @param serverNode 停机的结点
     */
    public void shutDownServerNode(ServerNode serverNode){
        String ipPort = serverNode.Ip+":"+serverNode.ServerPort;
        serverNodeList.remove(serverNode.Id);
        storesList.remove(ipPort);
    }
    /**
     * 新增加一个存储服务器结点
     * @param serverNode
     */
    public void addServerNode(ServerNode serverNode){
        LogRecord.FileHandleInfoLogger.info("add new serverNode "+ serverNode.Ip+":"+serverNode.ServerPort+" ["+serverNode.Id+"]");
        serverNodeList.put(serverNode.Id, serverNode);
    }

    /**
     * 获取所有的存储服务器结点
     * @return
     */
    public List<ServerNode> getAllServerNode(){
        return new ArrayList<>(serverNodeList.values());
    }

    /**
     * 获取一个存储服务器结点
     * @param id 存储服务器id
     * @return
     */
    public ServerNode getServerNode(String id){
        return serverNodeList.get(id);
    }

    /**
     * 增加一个存储端（存储管理子系统）
     * @param ipPort 10.2.8.181:9998
     * @param sa
     */
    public void addStore(String ipPort, SocketAction sa){
        storesList.put(ipPort, sa);
    }

    /**
     * 根据ipPort获取存储端的连接信息
     * @param ipPort 10.2.8.181:9998
     * @return
     */
    public SocketAction getStore(String ipPort){
        return storesList.get(ipPort);
    }

    /**
     * 增加一个客户端（存储接口管理子系统）
     * @param ipPort 10.2.8.181:9998
     * @param sa
     */
    public void addClient(String ipPort, SocketAction sa){
        clientsList.put(ipPort, sa);
    }

    /**
     * 根据ipPort获取客户端的连接信息
     * @param ipPort 10.2.8.181:9998
     * @return
     */
    public SocketAction getClient(String ipPort){
        return clientsList.get(ipPort);
    }

    /**
     * 增加指纹信息个数
     * @param num
     */
    public void addFingerprintNum(double num){
        fingerprintNum += num;
    }

    /**
     * 获取指纹信息总数
     * @return
     */
    public double getFingerprintNum(){
        return fingerprintNum;
    }

    /**
     * 根据节点id获取它所属的Node对象
     *
     * @param nodeId
     *            目录节点Id
     */
    public Node getNodeByNodeId(String nodeId) {
        try {
            if (serverNodeList.keySet().contains(nodeId)) {
                return serverNodeList.get(nodeId);
            }
            Collection<ServerNode> serverNodes = serverNodeList.values();
            for (ServerNode r : serverNodes) {
                if (r.DirectoryNodeTable.containsKey(nodeId))
                    return r.DirectoryNodeTable.get(nodeId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
