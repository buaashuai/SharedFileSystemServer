package pers.sharedFileSystem.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pers.sharedFileSystem.shareInterface.DirectoryAdapter;

public class ConfigTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// Pattern pattern = Pattern.compile("([\\d]+\\.){3}[\\d]+");
		// Matcher matcher = pattern.matcher("127.0.0.1:8080\\d:\\hello");
		// if (matcher.find()) {
		// for (int i = 0; i <= matcher.groupCount(); i++) {
		// String ip = matcher.group(0);
		// System.out.println(ip);
		// }
		// }
		// String string = "dgf/@dfg/@ddffg/dfgds@f";
		// String patternStr = Config.getPREFIX() + "[\\w]+";
		// Pattern pattern = Pattern.compile(patternStr);
		// Matcher matcher = pattern.matcher(string);
		// while (matcher.find()) {
		// System.out.println(matcher.group(0));
		// }
		// String str = string.replaceAll("@dfg", "3");
		// System.out.println(str);
		// System.out.println(matcher.matches());
		// Pattern pattern = Pattern.compile("[, |]+");
		// String[] strs = pattern
		// .split("Java Hello World  Java,Hello,,World|Sun");
		// for (int i = 0; i < strs.length; i++) {
		// System.out.println(strs[i]);
		// }
		// FileInputStream fileInputStream = new FileInputStream(new File(
		// "C:/Users/dell/Desktop/本科毕设/本科毕设.rar"));
		// FileAdapter baseMethod = new FileAdapter(fileInputStream);
		Map<String, String> map = new HashMap<String, String>();
		map.put("activityId", "2");
		map.put("eventId", "1");
		map.put("categoryId", "3");
		// FileAdapter fileAdapter = new FileAdapter("eventActivityAlbum",
		// "a.xml", map);
		// JSONObject re = fileAdapter.getFileContent();
		// JSONObject re = baseMethod
		// .saveFileTo("eventActivityAlbum", "本科毕设", map);
		// System.out.println(re);
		// System.out.println(re.getJSONArray("Info").getString(0));
		// FileAdapter fileAdapter = new FileAdapter("eventActivityAlbum",
		// "本科毕设 - 副本.RAR", map);
		// JSONObject re = fileAdapter.saveFileTo("temp", "本科毕设 - 副本",
		// new HashMap<String, String>());
		// System.out.println(re);

		// FileUtil.delete("D:\\EclipseProject\\deploy\\webapps\\static\\OlineSacrificeFiles");

		DirectoryAdapter directoryAdapter = new DirectoryAdapter("temp",
				JSONObject.fromObject(map));
		JSONArray re2 = directoryAdapter.getAllFilePaths();
		JSONArray re3 = JSONArray
				.fromObject(directoryAdapter.getAllFileNames());
		List<String> names = new ArrayList<String>();
		names.add("本科毕设 - 副本.RAR");
		directoryAdapter.deleteSelective(names);
		System.out.println(re2);
		System.out.println(re3);
		// Feedback f1 = new Feedback();
		// f1.ErrorInfo = "dsfgsdgf";
		// f1.Info.addFingerPrint("dsfg");
		// System.out.println(f1.toJsonArray());
		// RootNode rootNode = Config.getRootNodeByNodeId("user");
		// System.out.println(rootNode.NodeTable.get("user").Redundancy.Switch);

		// for (RootNode rootNode : nodes) {
		// System.out.println(rootNode.Ip);
		// }
		// XMLOutputter out = new
		// XMLOutputter(Format.getPrettyFormat().setIndent(
		// "    "));
		//
		// out.output(doc, new FileOutputStream("jdom2.xml"));
	}
}
