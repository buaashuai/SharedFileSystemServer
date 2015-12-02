package pers.sharedFileSystem.convenientUtil;

import java.io.IOException;
import java.util.Map;

import net.sf.json.JSONObject;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.entity.DirectoryNode;
import pers.sharedFileSystem.entity.Feedback;
import pers.sharedFileSystem.entity.FileType;
import pers.sharedFileSystem.entity.Node;
import pers.sharedFileSystem.entity.ServerNode;
import pers.sharedFileSystem.logManager.LogRecord;

/**
 * 文件操作相关的工具包
 *
 * @author buaashuai
 */
public class CommonFileUtil {

    /**
     * 将文件头转换成16进制字符串
     *
     * @param src 原生byte
     * @return 16进制字符串
     */
    private static String bytesToHexString(byte[] src) {

        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 得到文件头
     *
     * @param bs 文件输入流
     * @return
     */
    private static String getFileContent(byte[] bs) {

        byte[] b = new byte[28];
        try {
            for (int i = 0; i < 28; i++)
                b[i] = bs[i];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytesToHexString(b);
    }

    /**
     * 获取文件类型
     *
     * @param bs 文件输入流
     * @return 文件类型
     */
    public static FileType getFileType(byte[] bs) {
        String fileHead = getFileContent(bs);
        if (fileHead == null || fileHead.length() == 0) {
            return null;
        }
        fileHead = fileHead.toUpperCase();
        FileType[] fileTypes = FileType.values();
        for (FileType type : fileTypes) {
            if (fileHead.startsWith(type.getValue().toUpperCase())) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断保存的文件是否符合节点白名单的规定
     *
     * @param bs   文件的部分输入流
     * @param node 待保存的节点
     * @param fileSuffix 文件后缀名
     * @return 是否符合规定
     */
    public static boolean isLegalFile(byte[] bs, Node node, FileType fileType,String fileSuffix) {
        if (node instanceof DirectoryNode) {
            DirectoryNode dNode = (DirectoryNode) node;
            if (fileType == null) {// 对于无法自动识别的文件类型，单独做处理
                for (FileType f : dNode.WhiteList) {
                    if (f.toString().equals(fileSuffix.toUpperCase())) {
                        return true;
                    }
                }
            }
            if (dNode.WhiteList.contains(FileType.ANY) || fileType != null
                    && dNode.WhiteList.contains(fileType))
                return true;
        }
        return false;
    }

    /**
     * 初始化节点的相对路径，生成路径上每个节点
     *
     * @param nodeId             DirectoryNode节点 ID
     * @param parms              节点路径中动态节点的名称参数 , JSON 类型
     * @param absoluteOrRelative true 返回绝对路径 false 返回相对路径
     * @return absoluteOrRelative = true 返回绝对路径，false 返回相对路径
     */
    public static JSONObject initFilePath(String nodeId,
                                          Map<String, String> parms, boolean absoluteOrRelative) {
        Node n = Config.getNodeByNodeId(nodeId);
        DirectoryNode node=(DirectoryNode)n;
        ServerNode serverNode = node.getServerNode();
        String orignPath = "";// 根节点到nodeId节点的相对路径
        orignPath = node.Path;
        String[] paths = orignPath.split("/");
        String key = "";
        String alt = "";
        for (int i = 0; i < paths.length; i++) {
            // 找到动态命名节点
            if (paths[i].contains(Config.getPREFIX())) {
                key = paths[i].substring(1);
                alt = parms.get(key);
                if (CommonUtil.validateString(alt))
                    orignPath = orignPath.replaceAll(paths[i], alt);
                else {
                    Feedback feedback = new Feedback(3003, "miss parm[" + key
                            + "]");
                    LogRecord.RunningErrorLogger.error("miss parm[" + key
                            + "]");
                    return feedback.toJsonObject();
                }
            }
        }
        generateFilePath(node, orignPath);
        if (absoluteOrRelative)
            orignPath = node.StorePath + orignPath;
        Feedback feedback = new Feedback(3000, "");
        feedback.addFeedbackInfo(orignPath);
        return feedback.toJsonObject();
    }

    /**
     * 在指定的节点下生成文件的路径
     *
     * @param rootNode 文件根节点
     * @param path     文件在该节点下的相对路径
     * @return 反馈信息
     */
    private static void generateFilePath(DirectoryNode rootNode, String path) {
        String[] paths = path.split("/");
        String root = rootNode.StorePath;
        for (int i = 0; i < paths.length; i++) {
            if (!CommonUtil.validateString(paths[i]))
                continue;
            root += "/" + paths[i];
            AdvancedFileUtil.validateDirectory(rootNode, root);
        }
    }

}
