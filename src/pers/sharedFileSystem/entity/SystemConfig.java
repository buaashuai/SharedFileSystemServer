package pers.sharedFileSystem.entity;

/**
 * 文件系统运行配置类
 */
public class SystemConfig {
    /**
     * 冗余验证服务器上的监听端口
     */
    public Integer Port;

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
    }
}