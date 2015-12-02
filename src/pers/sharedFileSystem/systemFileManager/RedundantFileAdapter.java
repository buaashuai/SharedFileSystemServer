package pers.sharedFileSystem.systemFileManager;

import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.DirectoryNode;
import pers.sharedFileSystem.entity.FingerprintInfo;
import pers.sharedFileSystem.entity.RedundancyFileStoreInfo;
import pers.sharedFileSystem.logManager.LogRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件冗余映射信息操作类
 */
public class RedundantFileAdapter {
    /**
     * 按照序列化的方式将冗余文件存储信息保存到磁盘
     * @param  directoryNodeId 客户端上传文件的节点编号（客户端将文件上传到该节点下）
     * @param redundancyFileStoreInfo 待保存的冗余文件映射信息
     */
    public void saveRedundancyFileStoreInfo(String directoryNodeId,RedundancyFileStoreInfo redundancyFileStoreInfo){
        FileOutputStream fout=null;
        ObjectOutputStream sout =null;
        String filePath= ((DirectoryNode) Config.getNodeByNodeId(directoryNodeId)).StorePath;//指纹信息的保存路径
        String fileName=Common.REDUNDANCY_NAME;
        if(!CommonUtil.validateString(filePath)){
            LogRecord.FileHandleErrorLogger.error("save Redundant error, filePath is null.");
            return;
        }
        filePath+="/"+Common.SYSTEM_FILE_FOLDER_Name;
        File file = new File(filePath);
        if (!file.exists() && !file.isDirectory())
            file.mkdir();//如果系统文件夹不存在，就建立系统文件夹
        try{
            fout= new FileOutputStream(filePath+"/"+fileName, true);
            sout= new ObjectOutputStream(fout);
            sout.writeObject(redundancyFileStoreInfo);
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
     * 根据被保存文件的目的节点到根节点的相对路径查找该文件夹对应的冗余文件存储信息
     * @param desNodeId 被保存文件的目的节点编号
     * @param relativeFilePath 被保存文件的目的节点到根节点的相对路径
     * @return 该文件夹里面存储的冗余文件信息
     */
    public RedundancyFileStoreInfo getRedundancyFileStoreInfoByPath(String desNodeId,String relativeFilePath){
        String filePath= ((DirectoryNode)Config.getNodeByNodeId(desNodeId)).StorePath;//指纹信息的保存路径
        String fileName=Common.REDUNDANCY_NAME;
        FileInputStream fin = null;
        BufferedInputStream bis =null;
        ObjectInputStream oip=null;
        if(!CommonUtil.validateString(filePath)){
            LogRecord.FileHandleErrorLogger.error("get Redundant error, filePath is null.");
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
                if (object instanceof RedundancyFileStoreInfo) { // 安全起见，这里需要判断对象类型
                    RedundancyFileStoreInfo tmp=(RedundancyFileStoreInfo)object;
                    if(tmp.essentialStorePath.equals(filePath))
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
     * 按照序列化的方式获取冗余文件存储信息
     * @param  directoryNodeId 获取某个的节点编号的全部冗余文件信息
     * @return
     */
    public List<RedundancyFileStoreInfo> getAllFingerprintInfo(String directoryNodeId){
        List<RedundancyFileStoreInfo>redundancyFileStoreInfos=new ArrayList<RedundancyFileStoreInfo>();
        FileInputStream fin = null;
        BufferedInputStream bis =null;
        ObjectInputStream oip=null;
        String filePath= ((DirectoryNode)Config.getNodeByNodeId(directoryNodeId)).StorePath;//指纹信息的保存路径
        String fileName=Common.REDUNDANCY_NAME;
        if(!CommonUtil.validateString(filePath)){
            LogRecord.FileHandleErrorLogger.error("get Redundant error, filePath is null.");
            return redundancyFileStoreInfos;
        }
        filePath+="/"+Common.SYSTEM_FILE_FOLDER_Name;
        File file = new File(filePath);
        if (!file.isDirectory())
            return redundancyFileStoreInfos;//如果系统文件夹不存在
        try{
            fin = new FileInputStream(filePath+"/"+fileName);
            bis = new BufferedInputStream(fin);
            while (true) {
                try {
                    oip = new ObjectInputStream(bis); // 每次重新构造对象输入流
                }catch (EOFException e) {
                    // e.printStackTrace();
//                    System.out.println("已达文件末尾");// 如果到达文件末尾，则退出循环
                    return redundancyFileStoreInfos;
                }
                Object object = new Object();
                object = oip.readObject();
                if (object instanceof RedundancyFileStoreInfo) { // 判断对象类型
                    redundancyFileStoreInfos.add((RedundancyFileStoreInfo) object);
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
        return redundancyFileStoreInfos;
    }
}
