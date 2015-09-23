package com.wangyin.asset.dubbo.interceptor.service;
/**
 * 预留的接口，目前没什么卵用
 * @author YangTao
 *
 */
public interface Converter {
	
	/**
	 * 调用出错或者参数校验失败时，会调用此接口
	 * @param targetInterface
	 * @param methodName
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 */
	public Object convertTo(Class<?> targetInterface,String methodName,String errorCode,String errorMsg);
	
	
}
