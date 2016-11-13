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
     * <p>删除冗余文件映射信息指令</p>
     */
    DELETE_REDUNDANCY_INFO,
    /**
     * 存储服务器 ——> 客户端
     * <p>返回删除冗余文件映射信息结果</p>
     */
    REPLY_DELETE_REDUNDANCY_INFO,
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
     * 客户端 ——> 存储服务器
     * <p>删除指纹详细信息</p>
     */
    DELETE_FINGERPRINTINFO,
    /**
     * 存储服务器 ——> 客户端
     * <p>删除指纹详细信息结果</p>
     */
    REPLY_DELETE_FINGERPRINTINFO,
    /**
     * 客户端 ——> 冗余验证服务器
     * 客户端 ——> 存储服务器
     * <p>长连接保持</p>
     */
    KEEP_ALIVE,
    /**
     * 客户端 ——> 存储服务器
     * <p>获取某个目录下的冗余文件信息</p>
     */
    GET_REDUNDANCY_INFO,
    /**
     * 存储服务器 ——> 客户端
     * <p>返回某个目录下的冗余文件信息</p>
     */
    REPLY_GET_REDUNDANCY_INFO,
    /**
     * 冗余验证服务器 ——> 存储服务器
     * <p>连接是否正常</p>
     */
    SOCKET_MONITOR,
    /**
     * 客户端 ——> 存储服务器
     * <p>删除文件引用</p>
     */
    DELETE_FREQUENCY,
    /**
     * 存储服务器 ——> 客户端
     * <p>返回删除文件引用</p>
     */
    REPLY_DELETE_FREQUENCY,
    /**
     * 客户端 ——> 存储服务器
     * <p>添加文件引用</p>
     */
    ADD_FREQUENCY,
    /**
     * 存储服务器 ——> 客户端
     * <p>返回添加文件引用</p>
     */
    REPLY_ADD_FREQUENCY,
    /**
     * 冗余验证服务器 ——> 存储服务器
     * <p>获取指纹信息列表</p>
     */
    REPLY_SEND_FINGERPRINT_LIST,
    /**
     * 存储服务器 ——> 冗余验证服务器
     * <p>返回指纹信息列表</p>
     */
    SEND_FINGERPRINT_LIST,
    /**
     * 客户端 ——> 存储服务器
     * <p>验证文件名集合是否有效</p>
     */
    VALIDATE_FILENAMES,
    /**
     * 存储服务器 ——> 客户端
     * <p>返回验证文件名集合是否有效</p>
     */
    REPLY_VALIDATE_FILENAMES,
    /**
     * 存储服务器 ——> 冗余验证服务器
     * <p>发送结点配置信息 FileConfig.xml 的内容对象</p>
     */
    SEND_CONFIG,
    /**
     * 冗余验证服务器 或 客户端——> 存储服务器
     * <p>获取存储服务器的运行状态</p>
     */
    GET_SERVER_STATE,
    /**
     * 存储服务器——> 冗余验证服务器 或 客户端
     * <p>返回存储服务器的运行状态</p>
     */
    REPLY_GET_SERVER_STATE,
    /**
     * 客户端 ——> 存储服务器
     * <p>某个存储目录是否需要扩容</p>
     */
    IF_DIRECTORY_NEED_EXPAND,
    /**
     * 存储服务器 ——> 客户端
     * <p>返回某个存储目录是否需要扩容的结论</p>
     */
    REPLY_IF_DIRECTORY_NEED_EXPAND,
    /**
     * 客户端 ——> 冗余验证服务器
     * <p>给某个存储目录结点扩容</p>
     */
    GET_EXPAND_DIRECTORY,
    /**
     * 冗余验证服务器 ——> 客户端
     * <p>返回给某个存储目录结点扩容的结果</p>
     */
    REPLY_GET_EXPAND_DIRECTORY,
    /**
     * 冗余验证服务器 ——> 存储服务器
     * <p>获取某个存储目录的扩容文件存储信息</p>
     */
    GET_EXPAND_FILE_STORE_INFO,
    /**
     * 存储服务器 ——> 冗余验证服务器
     * <p>返回某个存储目录的扩容文件存储信息</p>
     */
    REPLY_GET_EXPAND_FILE_STORE_INFO,
    /**
     * 冗余验证服务器 或 客户端——> 存储服务器
     * <p>获取空闲的存储服务器的运行状态</p>
     */
    GET_FREE_SERVER_STATE,
    /**
     * 存储服务器——> 冗余验证服务器 或 客户端
     * <p>返回空闲的存储服务器的运行状态</p>
     */
    REPLY_FREE_GET_SERVER_STATE,
    /**
     * 客户端 ——> 存储服务器
     * <p>添加扩容信息</p>
     */
    ADD_EXPAND_INFO,
    /**
     * 存储服务器 ——> 客户端
     * <p>添加扩容信息结果</p>
     */
    REPLY_ADD_EXPAND_INFO,
}