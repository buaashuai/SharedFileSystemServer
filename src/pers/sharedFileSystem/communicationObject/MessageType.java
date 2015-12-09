package pers.sharedFileSystem.communicationObject;

import java.io.Serializable;

/**
 * 客户端和冗余验证服务端之间通讯的消息类型
 * @author buaashuai
 */
public enum MessageType implements Serializable {
    /**
     * 客户端 ——> 冗余验证服务器
     * <p>对文件进行冗余验证</p>
     */
    CHECK_REDUNDANCY,
    /**
     * 冗余验证服务器 ——> 客户端
     * <p>返回冗余验证结果</p>
     */
    REPLY_CHECK_REDUNDANCY,
    /**
     * 客户端 ——> 冗余验证服务器
     * <p>对布隆过滤器置位</p>
     */
    ADD_FINGERPRINT,
    /**
     * 冗余验证服务器 ——> 客户端
     * <p>返回布隆过滤器置位结果</p>
     */
    REPLY_ADD_FINGERPRINT,
    /**
     * 冗余验证服务器 ——> 存储服务器
     * <p>根据文件指纹查找冗余信息文件</p>
     */
    FIND_REDUNDANCY,
    /**
     * 存储服务器 ——> 冗余验证服务器
     * <p>返回冗余信息文件查找结果</p>
     */
    REPLY_FIND_REDUNDANCY,
    /**
     * 冗余验证服务器 ——> 存储服务器
     * <p>停止查找冗余信息文件</p>
     */
    STOP_FIND_REDUNDANCY,
    /**
     * 客户端 ——> 存储服务器
     * <p>添加冗余文件映射信息指令</p>
     */
    ADD_REDUNDANCY_INFO,
    /**
     * 存储服务器 ——> 客户端
     * <p>返回添加冗余文件映射信息结果</p>
     */
    REPLY_ADD_REDUNDANCY_INFO,
    /**
     * 客户端 ——> 存储服务器
     * <p>添加指纹详细信息</p>
     */
    ADD_FINGERPRINTINFO,
    /**
     * 存储服务器 ——> 客户端
     * <p>添加指纹详细信息结果</p>
     */
    REPLY_ADD_FINGERPRINTINFO,
    /**
     * 客户端 ——> 冗余验证服务器
     * <p>长连接保持</p>
     */
    KEEP_ALIVE,
}