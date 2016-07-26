package pers.sharedFileSystem.communicationObject;

import java.io.Serializable;

/**
 * 查找冗余信息文件通信对象
 */
public class FindRedundancyObject implements Serializable {
    /**
     * 文件指纹信息
     */
    public FingerprintInfo fingerprintInfo;
    /**
     * 本次查找的序列号
     */
    public double sequenceNum;
}