package pers.sharedFileSystem.entity;

import java.io.Serializable;

/**
 * 备份节点信息
 */
public class BackupNode  extends Node implements Serializable {

    /**
     * 资源目录树根节点的Ip地址
     */
    public String Ip;
    /**
     * 资源目录树根节点的端口号
     */
    public Integer Port;
    /**
     * 资源目录树根节点对应的存储服务器上的文件系统服务端口
     */
    public Integer ServerPort;
    /**
     * ServerNode服务器的用户名
     */
    public String UserName;
    /**
     * ServerNode服务器的密码
     */
    public String Password;
    /**
     * 打印节点信息
     *
     * @param tabs
     *            缩进tab
     */
    public void print(String tabs) {
        System.out.println(tabs + "id: " + Id);
        System.out.println(tabs + "Ip: " + Ip);
        System.out.println(tabs + "Port: " + Port);
        System.out.println(tabs + "ServerPort: " + ServerPort);
        System.out.println(tabs + "UserName: " + UserName);
        System.out.println(tabs + "Password: " + Password);
    }
}
