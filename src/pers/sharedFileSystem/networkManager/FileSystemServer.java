package pers.sharedFileSystem.networkManager;

import pers.sharedFileSystem.bloomFilterManager.BloomFilter;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.FingerprintInfo;
import pers.sharedFileSystem.entity.MessageProtocol;
import pers.sharedFileSystem.entity.MessageType;
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
    private ServerSocket serverSocket;
    private  Socket socket = null;

    /**
     * 关闭文件系统服务
     */
    public void closeServer() {
        try {
            serverSocket.close();
            LogRecord.RunningInfoLogger.info("file system server shut down");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogRecord.RunningErrorLogger.error(e.toString());
        }
    }

    /**
     * 初始化文件系统
     */
    private void initServerSocket(){
        try {
            serverSocket = new ServerSocket(Config.SYSTEMCONFIG.FileSystemPort);
            LogRecord.RunningInfoLogger.info("file system server start at port "+Config.SYSTEMCONFIG.FileSystemPort);
            while (true) {
                Thread.sleep(1000);
                // 阻塞，等待连接
                socket = serverSocket.accept();
                // 接受客户端发来的信息
                ObjectInputStream ois = new ObjectInputStream(
                        socket.getInputStream());
                MessageProtocol messageProtocol=(MessageProtocol)ois.readObject();
                //返回验证结果
                ObjectOutputStream oos = new ObjectOutputStream(
                        socket.getOutputStream());
                if(messageProtocol.messageType== MessageType.CHECK_REDUNDANCY){
                    String figurePrint=messageProtocol.content.get("figurePrint");
                    String desNodeId=messageProtocol.content.get("desNodeId");
                    MessageProtocol reMessage=new MessageProtocol();
                    if(!CommonUtil.validateString(figurePrint)){
                        reMessage.messageType=MessageType.REPLY_CHECK_REDUNDANCY;
                        reMessage.content.put("messageCode","4001");
                        oos.writeObject(reMessage);
                        LogRecord.FileHandleInfoLogger.info(MessageCodeHandler.getMessageInfo(4001, ""));
                        return;
                    }
                    String reMes="";
                    //验证指纹
                    if(BloomFilter.getInstance().isFingerPrintExist(figurePrint)) {
                        //此处应该返回指纹信息对应的文件的绝对路径
                        FingerprintInfo fingerprintInfo=new FingerprintAdapter().getFingerprintInfoByMD5(desNodeId,figurePrint);
                        if(fingerprintInfo==null){
                            reMes="false";
                            reMessage.content.put("messageCode","4003");
                        }else {
                            reMessage.content.put("messageCode","4000");
                            reMessage.content.put("filePath", fingerprintInfo.FilePath+fingerprintInfo.FileName);
                            reMes = "true";
                            LogRecord.RunningInfoLogger.info(figurePrint+" upload rapidly.");
                        }
                    }
                    else {
                        reMes="false";
                        reMessage.content.put("messageCode","4002");
                    }
                    reMessage.messageType=MessageType.REPLY_CHECK_REDUNDANCY;
                    oos.writeObject(reMessage);
                    LogRecord.FileHandleInfoLogger.info("BloomFilter check redundancy ["+figurePrint+"] "+reMes);
                }else if(messageProtocol.messageType==MessageType.ADD_FINGERPRINT){
                    String figurePrint=messageProtocol.content.get("figurePrint");
                    String filePath=messageProtocol.content.get("FilePath");
                    String fileName=messageProtocol.content.get("FileName");
                    String desNodeId=messageProtocol.content.get("desNodeId");
                    FingerprintInfo fInfo=new FingerprintInfo(figurePrint,filePath,fileName);
                    if(CommonUtil.validateString(figurePrint)) {
                        new FingerprintAdapter().saveFingerprint(desNodeId,fInfo);
                        BloomFilter.getInstance().addFingerPrint(figurePrint);
                        LogRecord.FileHandleInfoLogger.info("BloomFilter add a new fingerPrint ["+figurePrint+"]");
                    }
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
            LogRecord.RunningErrorLogger.error(e.toString());
        } finally {

        }
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
