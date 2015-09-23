package com.wangyin.asset.app.validator;

import javax.validation.ConstraintViolation;

import com.wangyin.asset.app.framework.common.validator.annotation.ErrorCode;
import com.wangyin.asset.app.validator.cache.ErrorCodeManager;
/**
 * 解析ErrorCode注解，获取错误码
 * @author YangTao
 *
 */
public class ErrorCodeResolver{
	
	private volatile String  overrideDefaultErrorCode = null;

	public String resolveErrorCode(ConstraintViolation<?> violation){
        ErrorCode error = ErrorCodeManager.getErrorCode(violation.getRootBeanClass(), violation.getPropertyPath().toString());
        String result = null;
        if(error == ErrorCodeManager.defualtError()){
        	result = overrideDefaultErrorCode==null?error.value():overrideDefaultErrorCode;
        }else{
        	result = error.value();
        }
        return result;
	}
	
	public String getOverrideDefaultErrorCode() {
		return overrideDefaultErrorCode;
	}

	public void setOverrideDefaultErrorCode(String overrideDefaultErrorCode) {
		this.overrideDefaultErrorCode = overrideDefaultErrorCode;
	}
}
