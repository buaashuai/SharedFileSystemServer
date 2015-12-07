package pers.sharedFileSystem.communicationObject;

import java.io.Serializable;

/**
 * 客户端和冗余验证服务端之间通讯的消息类型
 * @author buaashuai
 */
public enum MessageType implements Serializable {
    /**
     * 请求冗余验证服务器对文件进行冗余验证
     */
    CHECK_REDUNDANCY,
    /**
     * 冗余验证服务器返回冗余验证结果
     */
    REPLY_CHECK_REDUNDANCY,
    /**
     * 请求冗余验证服务器对布隆过滤器置位
     */
    ADD_FINGERPRINT,
    /**
     * 冗余验证服务器返回布隆过滤器置位结果
     */
    REPLY_ADD_FINGERPRINT,
    /**
     * 向存储服务器发送添加映射信息指令
     */
    ADD_REDUNDANCY_INFO,
    /**
     * 存储服务器返回发送添加映射信息指令结果
     */
    REPLY_ADD_REDUNDANCY_INFO,
    /**
     * 长连接保持
     */
    KEEP_ALIVE,
}
