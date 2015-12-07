package pers.sharedFileSystem.networkManager;

import pers.sharedFileSystem.bloomFilterManager.BloomFilter;

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
