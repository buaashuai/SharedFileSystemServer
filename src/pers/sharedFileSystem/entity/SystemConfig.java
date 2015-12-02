package pers.sharedFileSystem.entity;

/**
 * 文件系统运行配置类
 */
public class SystemConfig {
    /**
     * 存储服务器上的文件系统监听端口
     */
    public Integer FileSystemPort;

    /**
     * 当前存储服务器的服务器节点编号
     */
    public String ServerNodeName;

    public SystemConfig(){

    }
    /**
     * 打印系统配置信息
     *
     * @param tabs
     *            缩进tab
     */
    public void print(String tabs) {
        System.out.println(tabs + "FileSystemPort: " + FileSystemPort);
        System.out.println(tabs + "ServerNodeName: " + ServerNodeName);
    }
}
