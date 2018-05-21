package com.nexus.website.impl.utils;

import com.nexus.website.impl.constants.Constants;
import com.nexus.website.impl.constants.ResultCodeConstant;
import com.nexus.website.impl.dto.request.BaseRequest;
import com.nexus.website.impl.dto.response.BaseResponse;
import com.nexus.website.impl.exceptions.NexusException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName: RespPackUtil
 * @Description: 响应包装类
 */
public class RespPackUtil {
	private final static Logger logger = LoggerFactory.getLogger(RespPackUtil.class);

	/**
	* @Title: execInvokeService
	* @Description: 统一包装调用后端服务
	* @return BaseResponse    返回类型
	 */
	public static BaseResponse execInvokeService(BaseRequest coreReq,
												 BaseResponse response, ServiceWrapper wrapper) {
		long startTime = System.currentTimeMillis();
		StringBuilder strBuff = new StringBuilder();
		StackTraceElement ste = new Exception().getStackTrace()[1];
		//服务方法名
		String methodName = ste.getMethodName();
		//服务类名
		String serviceName = StringUtils.substringBefore(getSimpleClassName(ste.getClassName()), "Impl");
		String facadeName = serviceName.concat(".").concat(methodName);
		String reqStr = JacksonMapper.beanToJson(coreReq);
		String reqLogStr = StringUtils.isBlank(wrapper.getTraceId()) ? reqStr : wrapper.getTraceId();
		strBuff.append("调用服务==").append(facadeName).append(";请求数据==").append(reqLogStr)
			   .append(";日志包装耗时==").append(System.currentTimeMillis()-startTime).append(";");
		long checkStartTime = System.currentTimeMillis();
		//数据检查
		String checkResult = coreReq.check();
		strBuff.append("检查数据合法性耗时==").append(System.currentTimeMillis()-checkStartTime).append(";");
		String resultCode = null;
		String exceptionMsg = null;
		Exception ex = null;
		if (StringUtils.isBlank(checkResult)) {
			try{
				//调用服务
				resultCode = wrapper.invokeService();
			}catch(Exception e){//服务异常
				ex = e;
				if(isTimeoutThrowable(ex.getCause())){
					resultCode = ResultCodeConstant.TIMEOUT_CODE;
					exceptionMsg = ResultCodeConstant.getCodeByLabel(ResultCodeConstant.TIMEOUT_CODE);
				}else if(ex instanceof NexusException){
					resultCode = ((NexusException)ex).getDefineCode();
					exceptionMsg = ((NexusException)ex).getDefineMsg();
				}else{
					resultCode = ResultCodeConstant.SYSTEM_FAIL_CODE;
					exceptionMsg = ex.getCause().getMessage();
				}
				//异常预警
				execInvokeFailToNotice(serviceName, methodName, ex, reqStr);
			}
		} else {//参数缺失
			resultCode = ResultCodeConstant.PARAMETER_ILLEGAL_CODE;
			strBuff.append("检测到数据错误描述==").append(checkResult).append(";");
		}
		//包装response
		packCoreResponse(response, resultCode,exceptionMsg, checkResult);
		String respStr = JacksonMapper.beanToJson(response);
		if((wrapper.isNoticeInvokeFail() && StringUtils.isNotBlank(response.getCode()))
			  || (ResultCodeConstant.SYSTEM_FAIL_CODE.equals(resultCode) && ex == null)){//调用失败是否通知
			//故障编码
			StringBuilder faultCodeBuff = new StringBuilder(facadeName);
			faultCodeBuff.append("||").append(response.getMessage());
		}
		strBuff.append("resultCode==").append(resultCode).append(";共耗时==")
			   .append(System.currentTimeMillis()-startTime).append(";response==").append(respStr).append(";");
		if(ResultCodeConstant.SUCCESS_CODE.equals(resultCode)){
			Boolean sendResult = wrapper.sendChangeNotify();
			if(sendResult != null){
				strBuff.append("单据内容变更通知消息发送完成,notifyMqSendResult==").append(sendResult).append(";");
			}
		}
		if(StringUtils.equals(ResultCodeConstant.SUCCESS_CODE, resultCode)){//调用成功
			//服务成功但调用时间过长
			if(wrapper.isMonitorSuccess() || System.currentTimeMillis() - startTime > wrapper.timeout()){
				logger.info(strBuff.toString());
			}else{
				logger.debug(strBuff.toString());
			}
		}else{//是否监控+是否忽略失败的操作
			if(ex != null){//服务异常
				strBuff.append(";error_msg==");
				Throwable cause = ex.getCause();
				if(isTimeoutThrowable(cause)){//超时异常
					strBuff.append(cause.getMessage());
					logger.error(strBuff.toString());
				}else{
					strBuff.append(ex.getMessage());
					logger.error(strBuff.toString(), ex);
				}
			}else{
				logger.info(strBuff.toString());
			}
		}
		return response;
	}
	//包装Response
	private static void packCoreResponse(BaseResponse response,
			String resultCode, String exceptionMsg,String errorDetail) {
		//封装基础返回
		response.setCode(resultCode);
		if (response.getMessage() != null && response.getMessage() != "") {
            response.setMessage(response.getMessage());
        } else {
            response.setMessage(ResultCodeConstant.getLableByCode(resultCode));
        }
		//系统异常检测封装
		if(StringUtils.isNotBlank(exceptionMsg) && StringUtils.isNotBlank(resultCode)){
			response.setCode(resultCode);
			response.setMessage(ResultCodeConstant.getLableByCode(resultCode));
			response.setDetailMessage(exceptionMsg);
		}
		//参数检测错误封装
		if (StringUtils.isNotBlank(errorDetail)) {
			response.setCode(ResultCodeConstant.PARAMETER_ILLEGAL_CODE);
			response.setMessage(ResultCodeConstant.getLableByCode(ResultCodeConstant.PARAMETER_ILLEGAL_CODE));
			response.setDetailMessage(errorDetail);
		}
	}
	//判断是否超时或者网络方面的异常
	public static boolean isTimeoutThrowable(Throwable cause){
		if(cause != null && (cause instanceof TimeoutException
				|| cause instanceof SocketTimeoutException
				|| cause instanceof ConnectException)){
			return true;
		}else{
			return false;
		}
	}

	/**
	* @Title: execInvokeFailToNotice
	* @Description: 服务失败通知策略
	* @param @param serviceName
	* @param @param methodName
	* @param @param ex
	* @param @param reqStr
	* @param @param errorStr
	 */
	public static void execInvokeFailToNotice(String serviceName,String methodName,
			Exception ex,String reqStr){
		//获取当前时间的月日时MMddHH
		String mdh = DateTool.getDateToMDH(DateTool.getCurrentDate());
		//异常名 如:NullPointerException
		String exName = ex.getClass().getSimpleName();
		//异常通知cache key(时间+服务名+异常名)
		String noticeKey = mdh+methodName+exName;
		//故障编码
		String faultCode = serviceName+"."+methodName;
		//计算同一服务同一类型异常次数
		Long incrNum = JedisUtil.getJedisInstance().execIncrToCache(noticeKey);
		if(incrNum == Constants.CONSTANT_ONE_INT){//首次异常设置cache作废时间
			JedisUtil.getJedisInstance().execExpireToCache(noticeKey, Constants.REDIS_ONEDAY);
		}
		StringBuilder errorBuff = new StringBuilder("调用服务:");
		errorBuff.append(faultCode).append("异常,错误信息:")
				 .append(ex.getClass().getName()).append(":");
		Throwable cause = ex.getCause();
		if(isTimeoutThrowable(cause)){//超时异常
			errorBuff.append(cause.getMessage());
		}else{//后期可以给异常分类
			errorBuff.append(ex.getMessage());
		}
		if(incrNum % 100 == Constants.CONSTANT_ONE_INT){//每累计一百次发送通知
		}
	}

	//获取服务类名
	private static String getSimpleClassName(String className){
		return className.substring(className.lastIndexOf(".")+1);
	}

}
