package pers.sharedFileSystem.communicationObject;

import java.io.Serializable;

/**
 * 存储服务器运行状态
 */
public class ServerState  implements Serializable {
    /**
     * 总内存GB
     */
    public double TotalMemory;
    /**
     * 空闲内存GB
     */
    public double FreeMemory;
    /**
     * 内存占用率(%)
     */
    public double MemoryState;
    /**
     * 总磁盘GB
     */
    public double TotalDisk;
    /**
     * 空闲磁盘GB
     */
    public double FreeDisk;
    /**
     * 磁盘占用率（%）
     */
    public double DiskState;
    /**
     * CPU占用率（%）
     */
    public double CpuState;
}
