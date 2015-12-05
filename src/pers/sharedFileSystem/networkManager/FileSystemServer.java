package pers.sharedFileSystem.networkManager;

import pers.sharedFileSystem.bloomFilterManager.BloomFilter;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.FingerprintInfo;
import pers.sharedFileSystem.communicationObject.MessageProtocol;
import pers.sharedFileSystem.communicationObject.MessageType;
import pers.sharedFileSystem.systemFileManager.FingerprintAdapter;
import pers.sharedFileSystem.systemFileManager.MessageCodeHandler;
import pers.sharedFileSystem.logManager.LogRecord;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 文件系统服务端，运行在每个存储服务器上面
 */
public class FileSystemServer {

    /**
     * 初始化文件系统
     */
    private void initServerSocket(){
        ConnWatchDog connWatchDog = new ConnWatchDog();
        Thread connWatchDogThread = new Thread(connWatchDog);
        connWatchDogThread.start();
    }

    public FileSystemServer() {
        BloomFilter.getInstance();
        initServerSocket();
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new FileSystemServer();
    }
}
