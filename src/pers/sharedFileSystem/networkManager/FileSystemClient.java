package pers.sharedFileSystem.networkManager;

import java.net.Socket;
import java.util.Hashtable;

import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.entity.ServerNode;
import pers.sharedFileSystem.logManager.LogRecord;

/**
 * 应用程序端的文件系统与服务端的文件系统之间的socket长连接池
 *
 * @author buaashuai
 */
public class FileSystemClient {
    private static Hashtable<String, Socket> SOCKETPOOL;
    static {
        SOCKETPOOL = new Hashtable<String, Socket>();
        try {
            Hashtable<String, ServerNode> serverNodes = Config.getConfig();
            for (ServerNode sNode : serverNodes.values()) {
                // if(CommonUtil.isRemoteServer(sNode.Ip)){
                Socket socket = new Socket(sNode.Ip, sNode.ServerPort);
                if(socket==null){
                    LogRecord.RunningErrorLogger.error("socket initial failed. "+sNode.Ip+" : "+sNode.ServerPort);
                }
                SOCKETPOOL.put(sNode.Id, socket);
                // }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogRecord.RunningErrorLogger.error(e.toString());
        }
    }

    /**
     * 根据根节点id获取对应的Socket对象
     *
     * @param serverNodeId
     *            根节点ID
     * @return
     */
    public static Socket getSocketByServerNodeId(String serverNodeId) {
        return SOCKETPOOL.get(serverNodeId);
    }
}
