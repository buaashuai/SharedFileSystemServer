package pers.sharedFileSystem.networkManager;

import pers.sharedFileSystem.bloomFilterManager.BloomFilter;

/**
 * 文件系统冗余验证服务端
 */
public class FileSystemServer {

    /**
     * 初始化
     */
    private void initServerSocket(){
        ConnWatchDog connWatchDog = new ConnWatchDog();
        Thread connWatchDogThread = new Thread(connWatchDog);
        connWatchDogThread.start();
    }

    public FileSystemServer() {
        BloomFilter b=BloomFilter.getInstance();
        b.initBloomFilter();
        initServerSocket();
    }

    public static void main(String[] args) {
        new FileSystemServer();
    }
}
