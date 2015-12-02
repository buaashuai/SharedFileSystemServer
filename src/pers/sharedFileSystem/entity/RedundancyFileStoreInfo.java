package pers.sharedFileSystem.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 冗余文件存储信息
 */
public class RedundancyFileStoreInfo implements Serializable{
    /**
     * 节目录节点相对路径（相对该节点的根存储根路径）
     */
    public String essentialStorePath;
    /**
     * 该目录节点下的存储在其他文件夹里面的冗余文件的相对路径
     */
    public ArrayList<String> otherFilePath;

    public RedundancyFileStoreInfo(){
        otherFilePath=new ArrayList<String>();
    }
}
