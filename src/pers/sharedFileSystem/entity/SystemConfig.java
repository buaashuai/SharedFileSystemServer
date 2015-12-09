package pers.sharedFileSystem.entity;

import java.util.List;

/**
 * 文件系统运行配置类
 */
public class SystemConfig {
    /**
     * 冗余验证服务器上的监听端口
     */
    public Integer Port;

    /**
     * 指纹信息存储路径
     */
    public String StorePath;
    /**
     * 需要发送冗余验证消息的存储服务器编号列表
     */
    public List<String>redundancyServerIds;

    public SystemConfig(){

    }
    /**
     * 打印系统配置信息
     *
     * @param tabs
     *            缩进tab
     */
    public void print(String tabs) {
        System.out.println(tabs + "Port: " + Port);
        System.out.println(tabs + "StorePath: " + StorePath);
        System.out.print(tabs + "redundancyServerIds: ");
        for(String str:redundancyServerIds){
            System.out.print(str + ",");
        }
        System.out.println("");
    }
}