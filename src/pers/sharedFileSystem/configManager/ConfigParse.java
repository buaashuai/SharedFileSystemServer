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
        systemConfig.Port = Integer.parseInt(element.getChildText("port"));
        systemConfig.MaxElement = Double.parseDouble(element.getChildText("maxElement"));
        systemConfig.FalsePositiveRate = Double.parseDouble(element.getChildText("falsePositiveRate"));
        Config.SYSTEMCONFIG = systemConfig;
    }
}
