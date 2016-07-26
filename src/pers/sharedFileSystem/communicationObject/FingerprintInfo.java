package pers.sharedFileSystem.communicationObject;

import pers.sharedFileSystem.entity.FileType;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 指纹信息类
 */
public class FingerprintInfo  implements Serializable {

    //get和set方法的目的是为了将该类转换成json对象
    public String getMd5() {
        return md5;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public FileType getFileType() {
        return fileType;
    }

    public String getUpdateTime() {
        SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化当前系统日期
        String time = dateFm.format(updateTime);
        return time;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 文件指纹
     */
    private String md5;
    /**
     *文件存储的节点编号
     */
    private String nodeId;
    /**
     * 文件相对路径
     */
    private String filePath;
    /**
     * 文件名（带后缀）
     */
    private String fileName;
    /**
     * 文件类型
     */
    private FileType fileType;

    /**
     * 添加日期
     */
    private Date updateTime;

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }


    /**
     * 文件被引用的频率
     */
    private int frequency;

    public boolean getPhysicalDeletedByTrueUserFlag() {
        return physicalDeletedByTrueUserFlag;
    }

    public void setPhysicalDeletedByTrueUserFlag(boolean physicalDeletedByTrueUserFlag) {
        this.physicalDeletedByTrueUserFlag = physicalDeletedByTrueUserFlag;
    }

    /**
     * 是否被上传这个文件的真实用户物理删除
     */
    private boolean physicalDeletedByTrueUserFlag;

    public FingerprintInfo(){}

    public  FingerprintInfo(String md5,String nodeId,String filePath,String fileName,FileType type){
        this.md5 =md5;
        this.nodeId =nodeId;
        this.filePath =filePath;
        this.fileName =fileName;
        this.fileType=type;
        this.updateTime =new Date();
        this.frequency =0;
        this.physicalDeletedByTrueUserFlag =false;
    }

    public  FingerprintInfo(String md5,FileType type){
        this.md5 =md5;
        this.fileType=type;
    }

    public String toString(){
        String str="";
        SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化当前系统日期
        String time = dateFm.format(new Date());
        str+="updateTime: "+time+" , ";
        str+="md5: "+ md5 +" , ";
        str+="nodeId: "+ nodeId +" , ";
        str+="filePath: "+ filePath +" , ";
        str+="fileName: "+ fileName +" , ";
        str+="fileType: "+fileType+" , ";
        str+="frequency: "+ frequency +" , ";
        str+="physicalDeletedByTrueUserFlag: "+ physicalDeletedByTrueUserFlag;
        return str;
    }
}