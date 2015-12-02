package pers.sharedFileSystem.systemFileManager;

import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.DirectoryNode;
import pers.sharedFileSystem.entity.FingerprintInfo;
import pers.sharedFileSystem.logManager.LogRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 指纹信息文件操作类
 */
public class FingerprintAdapter {
    /**
     * 按照序列化的方式将指纹信息保存到磁盘
     * @param  directoryNodeId 客户端上传文件的节点编号（客户端将文件上传到该节点下）
     * @param fingerprintInfo 待保存的指纹信息
     */
    public void saveFingerprint(String directoryNodeId,FingerprintInfo fingerprintInfo){
        FileOutputStream fout=null;
        ObjectOutputStream sout =null;
        String filePath= ((DirectoryNode)Config.getNodeByNodeId(directoryNodeId)).StorePath;//指纹信息的保存路径
        String fileName=Common.FINGERPRINT_NAME;
        if(!CommonUtil.validateString(filePath)){
            LogRecord.FileHandleErrorLogger.error("save Fingerprint error, filePath is null.");
            return;
        }
        filePath+="/"+Common.SYSTEM_FILE_FOLDER_Name;
        File file = new File(filePath);
        if (!file.exists() && !file.isDirectory())
            file.mkdir();//如果系统文件夹不存在，就建立系统文件夹
        try{
            fout= new FileOutputStream(filePath+"/"+fileName, true);
            sout= new ObjectOutputStream(fout);
            sout.writeObject(fingerprintInfo);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
                try {
                    if(fout!=null)
                        fout.close();
                    if(sout!=null)
                        sout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 根据被保存文件的目的节点编号和指纹信息查找该指纹信息对应的文件存储信息
     * @param desNodeId 被保存文件的目的节点编号
     * @param md5 指纹信息
     * @return 该指纹信息对应的文件存储信息
     */
    public FingerprintInfo getFingerprintInfoByMD5(String desNodeId,String md5){
        String filePath= ((DirectoryNode)Config.getNodeByNodeId(desNodeId)).StorePath;//指纹信息的保存路径
        String fileName=Common.FINGERPRINT_NAME;
        FileInputStream fin = null;
        BufferedInputStream bis =null;
        ObjectInputStream oip=null;
        if(!CommonUtil.validateString(filePath)){
            LogRecord.FileHandleErrorLogger.error("get Fingerprint error, filePath is null.");
            return null;
        }
        filePath+="/"+Common.SYSTEM_FILE_FOLDER_Name;
        File file = new File(filePath);
        if (!file.isDirectory())
            return null;//如果系统文件夹不存在
        try{
            fin = new FileInputStream(filePath+"/"+fileName);
            bis = new BufferedInputStream(fin);
            while (true) {
                try {
                    oip = new ObjectInputStream(bis); // 每次重新构造对象输入流
                }catch (EOFException e) {
                    // e.printStackTrace();
//                    System.out.println("已达文件末尾");// 如果到达文件末尾，则退出循环
                    return null;
                }
                Object object = new Object();
                object = oip.readObject();
                if (object instanceof FingerprintInfo) { // 判断对象类型
                    FingerprintInfo tmp=(FingerprintInfo)object;
                    if(tmp.Md5.equals(md5))
                        return tmp;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                if(oip!=null)
                    oip.close();
                if(bis!=null)
                    bis.close();
                if(fin!=null)
                    fin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 按照序列化的方式获取指纹信息
     * @param  directoryNodeId 获取某个的节点编号的全部指纹信息
     * @return
     */
    public List<FingerprintInfo>getAllFingerprintInfo(String directoryNodeId){
        List<FingerprintInfo>fingerprintInfos=new ArrayList<FingerprintInfo>();
        FileInputStream fin = null;
        BufferedInputStream bis =null;
        ObjectInputStream oip=null;
        String filePath= ((DirectoryNode)Config.getNodeByNodeId(directoryNodeId)).StorePath;//指纹信息的保存路径
        String fileName=Common.FINGERPRINT_NAME;
        if(!CommonUtil.validateString(filePath)){
            LogRecord.FileHandleErrorLogger.error("get Fingerprint error, filePath is null.");
            return fingerprintInfos;
        }
        filePath+="/"+Common.SYSTEM_FILE_FOLDER_Name;
        File file = new File(filePath);
        if (!file.isDirectory())
            return fingerprintInfos;//如果系统文件夹不存在
        try{
            fin = new FileInputStream(filePath+"/"+fileName);
            bis = new BufferedInputStream(fin);
            while (true) {
                try {
                    oip = new ObjectInputStream(bis); // 每次重新构造对象输入流
                }catch (EOFException e) {
                    // e.printStackTrace();
//                    System.out.println("已达文件末尾");// 如果到达文件末尾，则退出循环
                    return fingerprintInfos;
                }
                Object object = new Object();
                object = oip.readObject();
                if (object instanceof FingerprintInfo) { // 判断对象类型
                    fingerprintInfos.add((FingerprintInfo) object);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                if(oip!=null)
                    oip.close();
                if(bis!=null)
                    bis.close();
                if(fin!=null)
                    fin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fingerprintInfos;
    }

}