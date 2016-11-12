package pers.sharedFileSystem.communicationObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 扩容文件存储信息
 */
public class ExpandFileStoreInfo implements Serializable{
    /**
     * 被扩容的结点编号
     * 可以根据directoryNodeId找到它的全部扩容结点的编号
     * directoryNodeId ——> expandNodeList
     */
    public String directoryNodeId;
    /**
     *  directoryNodeId的扩容结点的编号集合
     */
    public ArrayList<String> expandNodeList;

    public ExpandFileStoreInfo(){
        expandNodeList=new ArrayList<String>();
    }

    /**
     * 添加一条信息
     * @param directoryNodeId
     */
    public void addExpandNodeId(String directoryNodeId){
        expandNodeList.add(directoryNodeId);
    }
}
