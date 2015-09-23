package com.wangyin.asset.dubbo.interceptor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcResult;
import com.wangyin.asset.app.framework.common.code.ResultConstant;
import com.wangyin.asset.app.validator.MsgTemplateResolverUtils;
import com.wangyin.asset.dubbo.interceptor.service.LazyInitValidatorConfig;
@Activate(group = {Constants.PROVIDER}, order=10000)
public class AssetInterceptor implements Filter{
	
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetInterceptor.class);
    
    private static final String ERRORCODE_PROPERTY_NAME = "code";
    private static final String ERRORMSG_PROPERTY_NAME = "message";
    private static final String INVOKE_ERROR = ResultConstant.UNKOWN_ERROR.code;
    private static final String INVOKE_ERROR_MSG = ResultConstant.UNKOWN_ERROR.message;

    
	public Result invoke(Invoker<?> invoker, Invocation invocation)
			throws RpcException {
		if(LazyInitValidatorConfig.needValidation()){
			//校验参数开始---
			try {
				Validator validator = LazyInitValidatorConfig.getValidator();
				Object[] args = invocation.getArguments();
				@SuppressWarnings("rawtypes")
				ConstraintViolation constraintViolation = null;
				if(args!=null && args.length>0){
					for (Object object : args) {
						Set<ConstraintViolation<Object>> set = validator.validate(object);
						if(set!=null && !set.isEmpty()){
							constraintViolation = set.iterator().next();
							break;//快速失败的校验
						}
					}
				}
				if(constraintViolation!=null){
					String errorCode = resolveErrorCode(constraintViolation);
					String errorMsg = MsgTemplateResolverUtils.getMsgFromViolation(constraintViolation);
					Result result = toRpcResult(invoker, invocation, errorCode, errorMsg);
					if(result != null){
						LOGGER.warn("参数错误,接口：{},错误原因：{},自动返回结果：{}",
								invoker.getInterface().getCanonicalName()+"."+invocation.getMethodName(),errorMsg,result);
						return result;
					}
					LOGGER.warn("请求中参数有误，但是无法自动处理返回结果，将进入调用链{}",errorCode);
				}
			} catch (Throwable e) {
				LOGGER.warn("验证请求中参数有误，自动处理返回结果出现异常，将进入调用链",e);
			}
		//校验参数结束
		}
		Result result = null;
		try {
            result = invoker.invoke(invocation);
            result = handleExp(result, invoker, invocation);
        } catch (RpcException e) {
        	throw e;
        }
		return result;
	}
	
	/**
	 * 自动处理一些异常为错误码的形式
	 * @param srcResult原始结果
	 * @param invoker
	 * @param invocation
	 * @return 返回数据为新的结果，并不是传入的srcResult
	 */
	private Result handleExp(final Result srcResult,Invoker<?> invoker, Invocation invocation){
		if(!srcResult.hasException()){
			return srcResult;//无异常直接返回结果
		}
		Throwable exception = srcResult.getException();
		// 如果是checked异常，不自动处理
        if (!(exception instanceof RuntimeException) && (exception instanceof Exception)) {
            return srcResult;
        }
        // 在方法签名上有声明，不自动处理
        try {
            Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            Class<?>[] exceptionClassses = method.getExceptionTypes();
            for (Class<?> exceptionClass : exceptionClassses) {
                if (exception.getClass().equals(exceptionClass)) {
                    return srcResult;
                }
            }
        } catch (NoSuchMethodException e) {
            return srcResult;
        }
        //其他情况需要重新处理,统一包装成错误码的形式
        Result wapperdResult = null;
		try {
			wapperdResult = toRpcResult(invoker, invocation, INVOKE_ERROR, INVOKE_ERROR_MSG);
    		if(wapperdResult == null){
    			LOGGER.warn("异常封装出错,将异常转为统一错误码出错");
    		}else{
    			LOGGER.warn("调用出现异常，已自动转为错误码,返回结果{}异常堆栈>>>",wapperdResult.toString(),srcResult.getException());
    			return wapperdResult;
    		}
		} catch (Exception e) {
			LOGGER.warn("调用目标接口出错,自动处理返回结果失败，客户端将收到异常",e);
			return srcResult;
		}
		return srcResult;
	}
	
	/**
	 * 尝试建立错误码的调用结果
	 * @param invoker
	 * @param invocation
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 */
	private Result toRpcResult(Invoker<?> invoker, Invocation invocation,String errorCode,String errorMsg) throws SecurityException, NoSuchMethodException, IllegalAccessException{
        Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
        if(method.getReturnType().isPrimitive()){
        	LOGGER.warn("方法返回类型为基本类型，不能进行错误码的设置{}",invoker.getInterface().getCanonicalName()+"."+method.getName());
        	return null;
        }
        if(method.getReturnType().isInterface()){
        	LOGGER.warn("方法返回类型为接口，不能进行错误码的设置{}",invoker.getInterface().getCanonicalName()+"."+method.getName());
        	return null;
        }
        Object value = null;
        try {
			value = method.getReturnType().newInstance();
		} catch (InstantiationException e) {
			LOGGER.warn("不能进行实例化，必须包含无参数的构造方法",e);
			return null;
		}
        PropertyDescriptor codeDescriptor = null;
        PropertyDescriptor msgDescriptor = null;
        try {
        	codeDescriptor = new PropertyDescriptor(ERRORCODE_PROPERTY_NAME, method.getReturnType());
		} catch (IntrospectionException e) {
			LOGGER.warn("无法设置错误码，必须有"+ERRORCODE_PROPERTY_NAME+"的get和set方法",e);
			return null;
		}
        try {
 			msgDescriptor = new PropertyDescriptor(ERRORMSG_PROPERTY_NAME, method.getReturnType());
 		} catch (IntrospectionException e) {
 			LOGGER.warn("无法设置错误消息提示，必须有"+ERRORMSG_PROPERTY_NAME+"的get和set方法",e);
 			return null;
 		}
        try {
            codeDescriptor.getWriteMethod().invoke(value, errorCode);
			msgDescriptor.getWriteMethod().invoke(value, errorMsg);
		} catch (IllegalArgumentException e) {
			LOGGER.warn("无法设置错误信息，code或message数据类型非String,类:"+value.getClass().getCanonicalName(),e);
 			return null;
		} catch (InvocationTargetException e) {
			LOGGER.warn("无法设置错误错误信息，code或message可访问的get和set方法，类："+value.getClass().getCanonicalName(),e);
 			return null;
		}
		return new RpcResult(value);
	}
	
	/**
	 * 获取某个字段上的ErrorCode注解信息
	 * @param constraintViolation
	 * @return
	 */
	private String resolveErrorCode(@SuppressWarnings("rawtypes") ConstraintViolation constraintViolation){
		try {
			return LazyInitValidatorConfig.getCodeReSolver().resolveErrorCode(constraintViolation);
		} catch (Exception e) {
			LOGGER.error("解析ErrorCode注解失败",e);
		}
		LOGGER.warn("解析ErrorCode注解失败,将返回默认错误码：{}",LazyInitValidatorConfig.errorCode());
		return LazyInitValidatorConfig.errorCode();
	}
	

}
