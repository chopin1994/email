package com.nexus.website.impl.utils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * @ClassName: ApiParamCheckUtil
 * @Description: 参数校验工具类
 */
public class ApiParamCheckUtil {

	private static Validator validator = null;

	public static <T> String checkParam(T obj){
		if(validator == null){
			ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
	        validator = validatorFactory.getValidator();
		}
		Set<ConstraintViolation<T>> valideSert = validator.validate(obj);
		if(valideSert != null && valideSert.size() > 0){
			StringBuilder strBuff = new StringBuilder();
	        for(ConstraintViolation<T> cv : validator.validate(obj)){
	        	strBuff.append(cv.getMessage()).append(";");
	        }
	        return strBuff.toString();
		}else{
			return null;
		}
	}

	/**
	* @Description: 用于一个bean有些类型不需要验证全部的属性
	* @param obj
	* @param args 需要验证的属性名
	* @return String    返回类型
	* @throws
	 */
	public static <T> String checkParam(T obj,String... args){
		StringBuilder strBuff = new StringBuilder("");
		if(validator == null){
			ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
	        validator = validatorFactory.getValidator();
		}
		if(args!=null){
			for(String param:args){
				Set<ConstraintViolation<T>> valideSert = validator.validateProperty(obj,param);
				if(valideSert != null && valideSert.size() > 0){
			        for(ConstraintViolation<T> cv : validator.validateProperty(obj, param)){
			        	strBuff.append(cv.getPropertyPath()).append(":").append(cv.getMessage()).append(";");
			        }
				}
			}
		}
		System.out.println(strBuff.toString().equals("")?null:strBuff.toString());
		return strBuff.toString().equals("")?null:strBuff.toString();
	}
}
