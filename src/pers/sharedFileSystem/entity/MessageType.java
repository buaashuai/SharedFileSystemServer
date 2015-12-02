package pers.sharedFileSystem.entity;

import java.io.Serializable;

/**
 * 客户端和服务端之间通讯的消息类型
 * @author buaashuai
 */
public enum MessageType implements Serializable {
    /**
     * 请求对文件进行冗余验证
     */
    CHECK_REDUNDANCY,
    /**
     * 返回冗余验证结果
     */
    REPLY_CHECK_REDUNDANCY,
    /**
     * 请求存储端的文件系统对布隆过滤器置位
     */
    ADD_FINGERPRINT,
    /**
     * 向存储服务器发送添加映射信息指令
     */
    ADD_REDUNDANCY_INFO,
    /**
     * 返回向存储服务器发送添加映射信息指令结果
     */
    REPLY_ADD_REDUNDANCY_INFO,
}
