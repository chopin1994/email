package com.nexus.website.impl.utils;

import com.nexus.website.impl.constants.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static boolean isMobileNO(String mobiles) {
		if (!NumberUtils.isDigits(mobiles)) {
			return false;
		}
		Pattern p = Pattern
				.compile("^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static String getObjToStr(Object obj) {
		if (obj == null) {
			return Constants.CONSTANT_EMPTY_STR;
		} else {
			return obj.toString();
		}
	}

	public static String getStrValueOrDefault(String str, String defaultStr) {
		if (StringUtils.isBlank(str)) {
			return defaultStr;
		} else {
			return str;
		}
	}

	public static boolean isEmailFormat(String email) {
		if(StringUtils.isBlank(email)){
			return false;
		}
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}
	
	public static String getCacheKeyByCallMethodName(String keyStr,int tierIndex){
		String preMethodName = new Exception().getStackTrace()[tierIndex].getMethodName();
		return preMethodName.concat(keyStr==null?Constants.CONSTANT_EMPTY_STR:keyStr);
	}
	
//	public static String getCustomSubString(String sourceStr,int maxLength,String splitStr){
//		int posit = StringUtils.indexOf(sourceStr, splitStr, maxLength);
//		if(posit > Constants.CONSTANT_INVALID_INT){
//			sourceStr = StringUtils.substring(sourceStr, 0, posit);
//		}
//		return sourceStr;
//	}
	
	public static String getCustomSubstr(String sourceStr,int maxLength,int maxSize,String splitStr){
		if(StringUtils.length(sourceStr) > maxLength){
			String[] urlArray = StringUtils.split(sourceStr,splitStr);
			StringBuilder strBuff = new StringBuilder();
			for(int index = 0; index < urlArray.length; index++){
				if(index > maxSize){
					break;
				}
				strBuff.append(urlArray[index]).append(splitStr);
			}
			return strBuff.toString();
		}else{
			return sourceStr;
		}
	}
	
	public static String concatInt(Integer i1,Integer i2){
		return getObjToStr(i1).concat(getObjToStr(i2));
	}
	
	public static List<Integer> splitStrToList(String str,String splitTag){
		String[] strArray = StringUtils.split(str, splitTag);
		if(strArray == null){
			return null;
		}
		List<Integer> list = new ArrayList<Integer>(strArray.length);
		for(String element : strArray){
			list.add(Integer.parseInt(element));
		}
		return list;
	}
	
	public static String splitJoinStr(String sourceStr,String joinStr,String splitStr){
		return StringUtils.isBlank(sourceStr) ? joinStr : StringUtils.join(new String[]{sourceStr, joinStr}, splitStr);
	}
}
