package pers.sharedFileSystem.shareInterface;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.sun.corba.se.spi.activation.Server;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.convenientUtil.AdvancedFileUtil;
import pers.sharedFileSystem.convenientUtil.CommonFileUtil;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.DirectoryNode;
import pers.sharedFileSystem.entity.Feedback;
import pers.sharedFileSystem.entity.Node;
import pers.sharedFileSystem.entity.ServerNode;
import pers.sharedFileSystem.exceptionManager.ErrorHandler;
import pers.sharedFileSystem.ftpManager.FTPUtil;
import pers.sharedFileSystem.logManager.LogRecord;

/**
 * 文件目录适配器提供文件目录操作的基本功能
 *
 * @author buaashuai
 */
public class DirectoryAdapter extends Adapter {
    /**
     * 删除本目录
     */
    public JSONObject delete() {
        Feedback feedback = null;
        if (AdvancedFileUtil.delete(this.NODE,
                this.FILEPATH))
            feedback = new Feedback(3000, "");
        else
            feedback = new Feedback(3001, "");
        return feedback.toJsonObject();
    }

    /**
     * @param nodeId 目录节点名称
     * @param parms  节点路径参数
     */
    public DirectoryAdapter(String nodeId, Map<String, String> parms) {
        Node n=Config.getNodeByNodeId(nodeId);
        if(n==null||n instanceof ServerNode) {
            LogRecord.FileHandleErrorLogger.error("["+nodeId+"] is not a DirectoryNode id");
            return;
        }
        DirectoryNode node = (DirectoryNode)n;
        ServerNode rootNode = node.getServerNode();
        // DirectoryAdapter.rootNode = rootNode;
        this.NODEID = nodeId;
        // Node node = rootNode.NodeTable.get(nodeId);
        this.NODE = node;
        // 初始化节点的相对路径
        JSONObject nodePathFeed = CommonFileUtil.initFilePath(nodeId, parms,
                false);
        if (nodePathFeed.getInt("Errorcode") != 3000) {
            System.out.println(ErrorHandler.getErrorInfo(3005, ""));
        }
        JSONArray jsonArray = nodePathFeed.getJSONArray("Info");
        if (jsonArray.size() > 0)
            this.FILEPATH =node.StorePath + jsonArray.getString(0);
        else
            this.FILEPATH = node.StorePath;
    }

    /**
     * 获取该目录下所有的文件相对路径（不包括目录文件）
     */
    public JSONArray getAllFilePaths() {
        ArrayList<String> files = new ArrayList<String>();
        ServerNode serverNode = this.NODE.getServerNode();
        String relativePath = this.FILEPATH.substring( this.NODE.StorePath.length());
        if (!CommonUtil.isRemoteServer(serverNode.Ip)) {
            File desFolderPath = new File(this.FILEPATH);
            String[] filelist = null;
            if (desFolderPath.isDirectory()) {
                filelist = desFolderPath.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(this.FILEPATH + "/"
                            + filelist[i]);
                    if (readfile.isFile()) {
                        files.add(relativePath + "/" + readfile.getName());
                    }
                }
            }
        } else {
            FTPClient ftpClient = FTPUtil.getFTPClientByServerNode(serverNode,
                    false);
            boolean flag = false;
            try {
                flag = ftpClient.changeWorkingDirectory(new String(relativePath.getBytes(), "ISO-8859-1"));
                if (flag) {//是文件夹
                    ftpClient.changeToParentDirectory();
                    FTPFile[] ftpFiles = ftpClient.listFiles(relativePath);
                    for (FTPFile f : ftpFiles) {
                        files.add(relativePath + "/" + f.getName());
                    }
                }
            } catch (Exception e) {
                LogRecord.FileHandleErrorLogger.error(e.toString());
            }
        }
        return JSONArray.fromObject(files);
    }

    /**
     * 获取该目录下所有的文件名称
     */
    public ArrayList<String> getAllFileNames() {
        ArrayList<String> files = new ArrayList<String>();
        ServerNode serverNode = this.NODE.getServerNode();
        if (!CommonUtil.isRemoteServer(serverNode.Ip)) {
            File desFolderPath = new File(this.FILEPATH);
            String[] filelist = null;
            if (desFolderPath.isDirectory()) {
                filelist = desFolderPath.list();
                Collections.addAll(files, filelist);
            }
        } else {
            FTPClient ftpClient = FTPUtil.getFTPClientByServerNode(serverNode,
                    false);
            String relativePath = this.FILEPATH.substring(this.NODE.StorePath.length());
            boolean flag = false;
            try {
                flag = ftpClient.changeWorkingDirectory(new String(relativePath.getBytes(), "ISO-8859-1"));

                if (flag) {//是文件夹
                    ftpClient.changeToParentDirectory();
                    FTPFile[] ftpFiles = ftpClient.listFiles(relativePath);
                    for (FTPFile f : ftpFiles) {
                        files.add(f.getName());
                    }
                }
            } catch (Exception e) {
                LogRecord.FileHandleErrorLogger.error(e.toString());
            }
        }
        return files;
    }

    /**
     * 删除本目录下指定的文件
     *
     * @param fileNames 待删除的文件名
     */
    public JSONObject deleteSelective(List<String> fileNames) {
        Feedback feedback = null;
        boolean flag = true;
        for (String name : fileNames) {
            if (name.equals("Fingerprint.sys")&&!AdvancedFileUtil.delete(this.NODE,
                    this.FILEPATH + "/" + name))
                flag = false;
        }
        if (flag) {
            feedback = new Feedback(3000, "");
        } else
            feedback = new Feedback(3001, "");
        return feedback.toJsonObject();
    }
}
