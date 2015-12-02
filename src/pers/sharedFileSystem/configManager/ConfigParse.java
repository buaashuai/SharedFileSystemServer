package pers.sharedFileSystem.configManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.*;
import pers.sharedFileSystem.logManager.LogRecord;

/**
 * 配置文件解析器
 * <p>
 * 该类主要对文件系统的配置文件进行解析
 * </p>
 *
 * @author buaashuai
 */
public class ConfigParse {
    /**
     * 解析文件白名单列表
     *
     * @param fileTypes 待解析的白名单字符串
     * @return 解析之后的白名单“文件类型”集合
     */
    private List<FileType> parseFileTypes(String fileTypes) {
        List<FileType> types = new ArrayList<FileType>();
        String[] file_types = fileTypes.split(",");
        for (int i = 0; i < file_types.length; i++) {
            types.add(FileType.valueOf(file_types[i].trim()));
        }
        return types;
    }

    /**
     * 对配置文件中的redundancy节点进行解析
     *
     * @param element redundancy节点的dom对象
     * @return 解析之后的redundancy对象
     */
    private RedundancyInfo parseRedundancyInfo(Element element) {
        RedundancyInfo redundancyInfo = new RedundancyInfo();
        redundancyInfo.Switch = element.getAttributeValue("switch")
                .equals("on") ? true : false;
        String maxElement = element.getChildText("maxElement");
        String falsePositiveRate = element.getChildText("falsePositiveRate");
        if (CommonUtil.validateString(maxElement)) {
            redundancyInfo.MaxElementNum = Double.parseDouble(maxElement);
        }
        if (CommonUtil.validateString(falsePositiveRate)) {
            redundancyInfo.FalsePositiveRate = Double
                    .parseDouble(falsePositiveRate);
        }
        Element figure = element.getChild("fingerGenType");
        if (figure != null) {
            if (figure.getAttributeValue("type").equals(
                    FingerGenerateType.CLIENT.toString().toLowerCase())) {
                redundancyInfo.FingerGenType = FingerGenerateType.CLIENT;
                redundancyInfo.Property = figure.getChildText("property");
            } else if (figure.getAttributeValue("type").equals(
                    FingerGenerateType.SERVER.toString().toLowerCase())) {
                redundancyInfo.FingerGenType = FingerGenerateType.SERVER;
            }
        }
        return redundancyInfo;
    }

    /**
     * 对配置文件中的directoryNode节点进行解析
     *
     * @param element            directoryNode节点的dom对象
     * @param path               从serverNode到该目录节点的路径
     * @param directoryNodeTable serverNode包含的目录节点id和目录节点对象的映射
     * @param whiteList          该节点的白名单
     * @param redundancy         该节点的删冗信息
     * @param serverNode         该节点所属的根节点
     * @return 解析之后的directoryNode对象
     */
    private DirectoryNode parseDirectoryNode(Element element, String path,
                                             Hashtable<String, DirectoryNode> directoryNodeTable,
                                             List<FileType> whiteList, RedundancyInfo redundancy,
                                             ServerNode serverNode,String storePath) {
        DirectoryNode directoryNode = new DirectoryNode();
        directoryNode.WhiteList = whiteList;
        directoryNode.Redundancy = redundancy;
        directoryNode.Id = element.getAttributeValue("id");
        directoryNode.ParentServerNode = serverNode;
        directoryNode.StorePath=storePath;
        String nameType ="static";
        if(element.getAttributeValue("storePath")!=null){
            directoryNode.StorePath=element.getAttributeValue("storePath");
        }
        if(element.getAttributeValue("nameType")!=null){
            nameType=element.getAttributeValue("nameType");
        }
        // 解析节点命名方式
        if (nameType.equals(NodeNameType.STATIC.toString().toLowerCase())) {
            directoryNode.NameType = NodeNameType.STATIC;
            if(element.getAttributeValue("name")!=null) {
                directoryNode.Name = element.getAttributeValue("name");
                if(CommonUtil.validateString(directoryNode.Name))
                    path += "/" + directoryNode.Name;
            }
        } else if (nameType.equals(NodeNameType.DYNAMIC.toString()
                .toLowerCase())) {
            directoryNode.NameType = NodeNameType.DYNAMIC;
            directoryNode.Property = element.getAttributeValue("property");
            path += "/" + Config.PREFIX + directoryNode.Property;
        }
        Element e_redundancy = element.getChild("redundancy");
        if (e_redundancy != null) {
            // 解析节点的删冗信息
            directoryNode.Redundancy = parseRedundancyInfo(e_redundancy);
        }
        String m_whiteList = element.getChildText("whiteList");
        if (CommonUtil.validateString(m_whiteList)) {
            directoryNode.WhiteList = parseFileTypes(m_whiteList);
        }
        List<Element> e_directoryNodes = element.getChildren("directoryNode");
        List<DirectoryNode> childNodes = new ArrayList<DirectoryNode>();
        DirectoryNode c_directoryNode;
        for (Element e : e_directoryNodes) {
            // 父节点的白名单就是子节点的白名单，父节点的删冗信息就是子节点的删冗信息
            c_directoryNode = parseDirectoryNode(e, path, directoryNodeTable,
                    directoryNode.WhiteList, directoryNode.Redundancy,
                    serverNode,directoryNode.StorePath);
            childNodes.add(c_directoryNode);
            directoryNodeTable.put(c_directoryNode.Id, c_directoryNode);// 每解析出一个子节点就将它加入directoryNodeTable
        }
        directoryNodeTable.put(directoryNode.Id, directoryNode);// 每解析出一个节点就将它加入directoryNodeTable
        directoryNode.Path = path;
        directoryNode.ChildNodes = childNodes;
        return directoryNode;
    }

    /**
     *
     * @param element backupNode节点的dom对象
     * @return 解析之后的BackupNode对象
     */
    private BackupNode parseBackupNode(Element element){
        BackupNode backupNode=new BackupNode();
        backupNode.Ip = element.getChildText("ip");
        backupNode.Port = Integer.parseInt(element.getChildText("port"));
        backupNode.ServerPort = Integer.parseInt(element.getChildText("serverPort"));
        backupNode.Id = element.getAttributeValue("id");
        backupNode.UserName = element.getChildText("userName");
        backupNode.Password = element.getChildText("password");
        return backupNode;
    }
    /**
     * 对配置文件中的ServerNode节点进行解析
     *
     * @param element serverNode 节点的dom对象
     * @return 解析之后的ServerNode对象
     */
    private ServerNode parseServerNode(Element element) {
        ServerNode serverNode = new ServerNode();
        Hashtable<String, DirectoryNode> directoryNodeTable = new Hashtable<String, DirectoryNode>();
        Hashtable<String, BackupNode>backupNodeTable = new Hashtable<String, BackupNode>();
        serverNode.Ip = element.getChildText("ip");
        serverNode.Port = Integer.parseInt(element.getChildText("port"));
        serverNode.ServerPort = Integer.parseInt(element.getChildText("serverPort"));
//        serverNode.Path = element.getChildText("path");
        serverNode.Id = element.getAttributeValue("id");
        serverNode.UserName = element.getChildText("userName");
        serverNode.Password = element.getChildText("password");
        List<Element> e_directoryNodes = element.getChildren("directoryNode");
        List<Element> e_backupNodes = element.getChildren("backupNode");
        List<DirectoryNode> childNodes = new ArrayList<DirectoryNode>();
        for (Element e : e_directoryNodes) {
            DirectoryNode directoryNode = new DirectoryNode();
            directoryNode = parseDirectoryNode(e, "", directoryNodeTable,
                    directoryNode.WhiteList, directoryNode.Redundancy,
                    serverNode,"");
            childNodes.add(directoryNode);
        }
        for(Element e:e_backupNodes){
            BackupNode backupNode=parseBackupNode(e);
            backupNodeTable.put(backupNode.Id,backupNode);
        }
        serverNode.DirectoryNodeTable = directoryNodeTable;
        serverNode.BackupNodeTable=backupNodeTable;
        serverNode.ChildNodes = childNodes;
        return serverNode;
    }

    /**
     * 对配置文件FileConfig.xml进行解析
     */
    public void parseFileConfig() {
        SAXBuilder builder = new SAXBuilder();
        String path = "", tpath = "", docPath = "";
        Document doc = null;
        try {
            if (Config.runtimeType == RuntimeType.DEBUG) {
                docPath = System.getProperty("user.dir") + "\\src\\FileConfig.xml";
                doc = builder.build(docPath);
            } else if (Config.runtimeType == RuntimeType.CLIENT) {
                path = this.getClass().getProtectionDomain().getCodeSource()
                        .getLocation().getPath();
//			LogRecord.RunningInfoLogger.info("path="+path);
                tpath = path.substring(0, path.indexOf("lib"));
//			LogRecord.RunningInfoLogger.info("tpath="+tpath);
                docPath = tpath + "classes/FileConfig.xml";
                doc = builder.build(docPath);
                // System.out.println(docPath);
//			LogRecord.RunningInfoLogger.info("FileConfig="+docPath);
            } else if (Config.runtimeType == RuntimeType.SERVER) {
                InputStream in = this
                        .getClass()
                        .getResourceAsStream(
                                "/FileConfig.xml");
                doc = builder.build(in);
            }
            // 单独放置在API系统里面是下面的代码，在文件系统中是上面的代码
            // InputStream in = this.getClass().getResourceAsStream(
            // "/com/shareAPI/config/FileConfig.xml");

        } catch (Exception e) {
            LogRecord.RunningErrorLogger.error(e.toString());
        }
        Element element = doc.getRootElement();
        List<Element> e_serverNodes = element.getChildren("serverNode");
        Hashtable<String, ServerNode> serverNodes = new Hashtable<String, ServerNode>();
        ServerNode serverNode;
        for (Element e : e_serverNodes) {
            //遍历得到ServerRedundancy
            RedundancyInfo serverRedundancy = new RedundancyInfo();
            serverNode = parseServerNode(e);
            summarizeServerRedundancy(serverNode, serverRedundancy);
            serverNode.ServerRedundancy = serverRedundancy;
            serverNodes.put(serverNode.Id, serverNode);
        }
        Config.SERVERNODES = serverNodes;
    }

    /**
     * 统计每个服务器的文件删冗信息
     *
     * @param node             节点
     * @param serverRedundancy 服务器的文件删冗信息
     */
    private void summarizeServerRedundancy(Node node, RedundancyInfo serverRedundancy) {
        if (node instanceof  DirectoryNode) {
            DirectoryNode dNode=(DirectoryNode)node;
            if(dNode.Redundancy.Switch) {
                serverRedundancy.Switch = true;
                //最大存储元素数累加
                serverRedundancy.MaxElementNum += dNode.Redundancy.MaxElementNum;
                //误报率取子节点中的最小值
                serverRedundancy.FalsePositiveRate = Math.min(serverRedundancy.FalsePositiveRate, dNode.Redundancy.FalsePositiveRate);
                return;
            }
        }
        for (Node n : node.ChildNodes) {
            summarizeServerRedundancy(n, serverRedundancy);
        }

    }

    /**
     * 对配置文件SystemConfig.xml进行解析
     */
    public void parseSystemConfig() {
        SAXBuilder builder = new SAXBuilder();
        String path = "", tpath = "", docPath = "";
        Document doc = null;
        try {
            if (Config.runtimeType == RuntimeType.DEBUG) {
                docPath = System.getProperty("user.dir") + "\\src\\SystemConfig.xml";
                doc = builder.build(docPath);
            } else if (Config.runtimeType == RuntimeType.CLIENT) {
                path = this.getClass().getProtectionDomain().getCodeSource()
                        .getLocation().getPath();
                tpath = path.substring(0, path.indexOf("lib"));
                docPath = tpath + "classes/SystemConfig.xml";
                doc = builder.build(docPath);
                // System.out.println(docPath);
                LogRecord.RunningInfoLogger.info("SystemConfig=" + docPath);
            } else if (Config.runtimeType == RuntimeType.SERVER) {
//			path = this.getClass().getProtectionDomain().getCodeSource()
//					.getLocation().getPath();//E:/WangShuai/FileSystem/SharedFileSystem.jar
//			LogRecord.RunningInfoLogger.info("path="+path);
//			String []str=path.split("//");
//			for(int i=0;i<str.length-1;i++){
//				docPath+=str[i];
//			}
//			docPath += "\\SystemConfig.xml";
//			LogRecord.RunningInfoLogger.info("SystemConfig="+docPath);
                InputStream in = this
                        .getClass()
                        .getResourceAsStream(
                                "/SystemConfig.xml");
                doc = builder.build(in);
            }
            // 单独放置在API系统里面是下面的代码，在文件系统中是上面的代码
            // InputStream in = this.getClass().getResourceAsStream(
            // "/com/shareAPI/config/FileConfig.xml");
        } catch (Exception e) {
            LogRecord.RunningErrorLogger.error(e.toString());
        }
        Element element = doc.getRootElement();
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.FileSystemPort = Integer.parseInt(element.getChildText("fileSystemPort"));
        systemConfig.ServerNodeName = element.getChildText("serverNodeName");
        Config.SYSTEMCONFIG = systemConfig;
    }
}
