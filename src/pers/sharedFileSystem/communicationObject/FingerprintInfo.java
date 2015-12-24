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
        return Md5;
    }

    public String getNodeId() {
        return NodeId;
    }

    public String getFilePath() {
        return FilePath;
    }

    public String getFileName() {
        return FileName;
    }

    public FileType getFileType() {
        return fileType;
    }

    public String getUpdateTime() {
        SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化当前系统日期
        String time = dateFm.format(new Date());
        return time;
    }

    public void setMd5(String md5) {
        Md5 = md5;
    }

    public void setNodeId(String nodeId) {
        NodeId = nodeId;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public void setUpdateTime(Date updateTime) {
        UpdateTime = updateTime;
    }

    /**
     * 文件指纹
     */
    private String Md5;
    /**
     *文件存储的节点编号
     */
    private String NodeId;
    /**
     * 文件相对路径
     */
    private String FilePath;
    /**
     * 文件名（带后缀）
     */
    private String FileName;
    /**
     * 文件类型
     */
    private FileType fileType;

    /**
     * 添加日期
     */
    private Date UpdateTime;

    public int getFrequency() {
        return Frequency;
    }

    public void setFrequency(int frequency) {
        Frequency = frequency;
    }


    /**
     * 文件被引用的频率
     */
    private int  Frequency;

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
        this.Md5=md5;
        this.NodeId=nodeId;
        this.FilePath=filePath;
        this.FileName=fileName;
        this.fileType=type;
        this.UpdateTime=new Date();
        this.Frequency=0;
        this.physicalDeletedByTrueUserFlag =false;
    }

    public  FingerprintInfo(String md5,FileType type){
        this.Md5=md5;
        this.fileType=type;
    }

    public String toString(){
        String str="";
        SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化当前系统日期
        String time = dateFm.format(new Date());
        str+="UpdateTime: "+time+" , ";
        str+="Md5: "+Md5+" , ";
        str+="NodeId: "+NodeId+" , ";
        str+="FilePath: "+FilePath+" , ";
        str+="FileName: "+FileName+" , ";
        str+="fileType: "+fileType+" , ";
        str+="Frequency: "+Frequency+" , ";
        str+="physicalDeletedByTrueUserFlag: "+ physicalDeletedByTrueUserFlag;
        return str;
    }
}