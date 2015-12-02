package pers.sharedFileSystem.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

public class JsonXmlTest {
	public static String xmlToString(String fileName) {
		String dirPath = System.getProperty("user.dir");// user.dir指定了当前的路径
		String filePath = dirPath + "\\resource\\" + fileName;
		Reader reader = null;
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			reader = new FileReader(filePath);
			br = new BufferedReader(reader);
			String data = null;
			while ((data = br.readLine()) != null) {
				sb.append(data);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return sb.toString();
		} finally {
			try {
				reader.close();
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String xmlToJSON(String xml) {
		XMLSerializer xmlSerializer = new XMLSerializer();
		JSON json = xmlSerializer.read(xml);
		System.out.println("xml--->json \n" + json.toString());
		return json.toString();
	}

	public static String jsonToXML(String jsonStr) {
		JSONObject json = JSONObject.fromObject(jsonStr);
		XMLSerializer xmlSerializer = new XMLSerializer();
		xmlSerializer.setRootName("user_info");
		xmlSerializer.setTypeHintsEnabled(false);
		String xml = xmlSerializer.write(json);
		System.out.println("json--->xml \n" + xml);
		return xml;
	}

	public static class Item {
		String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getScreenshotPath() {
			return screenshotPath;
		}

		public void setScreenshotPath(String screenshotPath) {
			this.screenshotPath = screenshotPath;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		String screenshotPath;
		String content;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<Item> items = new ArrayList<JsonXmlTest.Item>();
		Map<String, Object> item1 = new HashMap<String, Object>();
		Map<String, Object> item2 = new HashMap<String, Object>();
		Item i1 = new Item(), i2 = new Item();
		i1.name = "模板1";
		i1.content = "/OlineSacrifice/static /RenderConfig/OfficialConfig/a.xml";
		i1.screenshotPath = "/OlineSacrifice/static /RenderConfig/OfficialConfigImg/a.png";
		i2.name = "模板2";
		i2.content = "/OlineSacrifice/static /RenderConfig/OfficialConfig/b.xml";
		i2.screenshotPath = "/OlineSacrifice/static /RenderConfig/OfficialConfigImg/b.png";
		item1.put("name", "模板1");
		item1.put("screenshotPath",
				"/OlineSacrifice/static /RenderConfig/OfficialConfigImg/a.png");
		item1.put("content",
				"/OlineSacrifice/static /RenderConfig/OfficialConfig/a.xml");
		items.add(i1);
		items.add(i2);

		item2.put("name", "模板2");
		item2.put("screenshotPath",
				"/OlineSacrifice/static /RenderConfig/OfficialConfigImg/b.png");
		item2.put("content",
				"/OlineSacrifice/static /RenderConfig/OfficialConfig/b.xml");
		Map<String, Object> root = new HashMap<String, Object>();
		map.put("total", items.size());
		map.put("items", items);
		// map.put("root", root);
		JSONObject jsonStr0 = JSONObject.fromObject(map);
		System.out.println(jsonStr0.toString());
		jsonToXML(jsonStr0.toString());
		System.out.println("***********");
		String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><user_info><password>123456</password><username>张三</username></user_info>";
		String jsonStr1 = "{\"password\":\"123456\",\"username\":\"张三\"}";
		xmlToJSON(xml1);
		jsonToXML(jsonStr1);
		String xml2 = xmlToString("myScene.xml");
		xmlToJSON(xml2);
		String xml3 = xmlToString("Units.xml");
		String jsonStr3 = xmlToJSON(xml2);
		jsonStr3 = jsonStr3.replaceAll("\"", "\\\"");
		jsonToXML("{" + jsonStr3 + "}");
		// System.out.println(jsonStr3);
	}
}
