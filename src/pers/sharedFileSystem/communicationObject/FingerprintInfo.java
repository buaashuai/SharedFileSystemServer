package pers.sharedFileSystem.communicationObject;

import pers.sharedFileSystem.entity.FileType;

import java.io.Serializable;

/**
 * 指纹信息类
 */
public class FingerprintInfo  implements Serializable {
    /**
     * 文件指纹
     */
    public String Md5;
    /**
     *文件存储的节点编号
     */
    public String NodeId;
    /**
     * 文件绝对路径
     */
    public String FilePath;
    /**
     * 文件名（带后缀）
     */
    public String FileName;
    /**
     * 文件类型
     */
    public FileType fileType;

    public FingerprintInfo(){}

    public  FingerprintInfo(String md5,String nodeId,String filePath,String fileName,FileType type){
        this.Md5=md5;
        this.NodeId=nodeId;
        this.FilePath=filePath;
        this.FileName=fileName;
        this.fileType=type;
    }
    public  FingerprintInfo(String md5,FileType type){
        this.Md5=md5;
        this.fileType=type;
    }
}