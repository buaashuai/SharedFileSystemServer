package pers.sharedFileSystem.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.exceptionManager.ErrorHandler;

/**
 * 接口的反馈实体类
 * 
 * @author buaashuai
 *
 */
public class Feedback {
	/**
	 * 错误码
	 */
	private int Errorcode;
	/**
	 * 错误码对应的错误信息
	 */
	private String ErrorInfo;
	/**
	 * 其他反馈信息
	 */
	private List<String> Info;
	/**
	 * 反馈信息对象
	 */
	private  Map<String, Object> map;

	/**
	 * 添加反馈信息
	 * 
	 * @param otherErrorInfo
	 */
	public void addFeedbackInfo(String otherErrorInfo) {
		if (CommonUtil.validateString(otherErrorInfo))
			Info.add(otherErrorInfo);
	}

	public void addFeedbackInfo(String key,Object info) {
		map.put(key, info);
	}

	/**
	 * 获取错误信息
	 * 
	 * @return
	 */
	public String getErrorInfo() {
		return ErrorInfo;
	}

	/**
	 * 通过错误代码构造反馈信息
	 * 
	 * @param errorcode
	 *            错误码
	 * @param otherErrorInfo
	 *            除了错误码信息之外的其他错误信息
	 */
	public Feedback(int errorcode, String otherErrorInfo) {
		Info = new ArrayList<String>();
		map = new HashMap<String, Object>();
		if (CommonUtil.validateString(otherErrorInfo))
			Info.add(otherErrorInfo);
		ErrorInfo = ErrorHandler.getErrorInfo(errorcode, "");
		this.Errorcode = errorcode;
	}

	/**
	 * 将Feedback信息转换成json数组
	 * 
	 * @return 转换之后的json数组
	 */
	public JSONObject toJsonObject() {
		map.put("Errorcode", Errorcode);
		map.put("ErrorInfo", ErrorInfo);
		map.put("Info", Info);
		return JSONObject.fromObject(map);
	}
}
