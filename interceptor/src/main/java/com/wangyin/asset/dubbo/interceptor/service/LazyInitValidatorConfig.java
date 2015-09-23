package com.wangyin.asset.dubbo.interceptor.service;

import javax.validation.Validator;

import com.wangyin.asset.app.framework.common.code.ResultConstant;
import com.wangyin.asset.app.validator.ErrorCodeResolver;
import com.wangyin.asset.app.validator.ValidatorFactory;

public class LazyInitValidatorConfig {
	
	private Validator validator;
	
	private String validatorProvider;
	
	private String overrideErrorCode = ResultConstant.REQUEST_PARAMETER_ERROR.code;
	
	private boolean needValidate = true;

	
	private ErrorCodeResolver errorCodeResolver = new ErrorCodeResolver();
	
    private static LazyInitValidatorConfig lazyInitConfig = new LazyInitValidatorConfig();
    
    
    private synchronized Validator init(){
    	if(validator != null){
    		return validator;
    	}
    	errorCodeResolver.setOverrideDefaultErrorCode(overrideErrorCode);
    	ValidatorFactory factory = new ValidatorFactory();
    	factory.setProvider(lazyInitConfig.validatorProvider);
    	validator = factory.getValidator();
    	return validator;
    }
    
    public static void configValidatorProvider(String provider){
    	lazyInitConfig.validatorProvider = provider;
    }
    
    public static Validator getValidator(){
    	if(lazyInitConfig.validator == null){
    		lazyInitConfig.init();
    	}
    	return lazyInitConfig.validator;
    }
    
    public static void configDefaultErrorCode(String errorCode){
    	lazyInitConfig.overrideErrorCode = errorCode;
    }
    
    public static String errorCode(){
    	return lazyInitConfig.overrideErrorCode;
    }
    
    public static ErrorCodeResolver getCodeReSolver(){
    	return lazyInitConfig.errorCodeResolver;
    }
    
    public static boolean needValidation(){
    	return lazyInitConfig.needValidate;
    }
    public static void turnOn(){
    	lazyInitConfig.needValidate = true;
    }
}
