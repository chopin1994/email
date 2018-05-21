package com.nexus.website.impl.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 服务调用结果码
 * @author chopin
 */
public class ResultCodeConstant {

	private ResultCodeConstant(){
		super();
	}

	public static final Map<String,String> msgMap = new HashMap<String,String>(300);

	/**
	 * 操作成功编码
	 */
	public static final String SUCCESS_CODE = "200";

	/**
	 * 操作失败编码
	 */
	public static final String FAIL_CODE = "99";

	/**
	 * 操作超时编码
	 */
	public static final String TIMEOUT_CODE = "98";

	/**
	 * 系统异常
	 */
	public static final String SYSTEM_FAIL_CODE = "97";

	/**
	 * 参数非法
	 */
	public static final String PARAMETER_ILLEGAL_CODE = "96";

	/**
	 * 异常体为空
	 */
	public static final String EXCEPTION_NULL_CODE = "95";

	/**
	 * 查询结果为空
	 */
	public static final String GET_EMPTY_CODE = "94";

	/**
	 * 导入异常
	 */
	public static final String IMPORT_OUTSIDE = "93";

	/**
	 * RPC异常
	 */
	public static final String RPC_EXCEPTION_CODE = "92";

	/**
	 * token过期或不存在
	 */
	public static final String SESSION_EXPIRE = "-1";

	static{
		msgMap.put(SUCCESS_CODE, "操作成功");
		msgMap.put(FAIL_CODE, "服务器开了小差，请稍后再试");
		msgMap.put(TIMEOUT_CODE, "操作超时");
		msgMap.put(SYSTEM_FAIL_CODE, "系统异常");
		msgMap.put(PARAMETER_ILLEGAL_CODE, "参数非法");
		msgMap.put(EXCEPTION_NULL_CODE, "异常体为空");
		msgMap.put(GET_EMPTY_CODE, "查询结果为空");
		msgMap.put(IMPORT_OUTSIDE, "导入异常");
		msgMap.put(RPC_EXCEPTION_CODE, "RPC异常");
		msgMap.put(SESSION_EXPIRE, "会话超时");
	}

	public static String getCodeByLabel(String label){
		for (Entry<String, String> entry : msgMap.entrySet()) {
			if(entry.getValue().equals(label)){
				return entry.getKey();
			}
		}
		return null;
	}

	public static String getLableByCode(String code){
		for (Entry<String, String> entry : msgMap.entrySet()) {
			if(entry.getKey().equals(code)){
				return entry.getValue();
			}
		}
		return null;
	}
}
