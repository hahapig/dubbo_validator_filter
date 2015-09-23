package com.wangyin.asset.app.validator.cache;


import com.wangyin.asset.app.framework.common.validator.annotation.ErrorCode;

/**
 * 提供元数据的缓存
 * @author yangtao
 *
 */
class ErrorCodeMetaCache {
	private final ConcurrentReferenceHashMap<String,ErrorCode> beanMetaDataCache = 
			new ConcurrentReferenceHashMap<String,ErrorCode>();
	private static final ErrorCodeMetaCache instance = new ErrorCodeMetaCache();
	private ErrorCodeMetaCache(){}
	public static ErrorCodeMetaCache instance(){
		return instance;
	}
	
	public ErrorCode getErrorAnnotation(String fullPath){
		return beanMetaDataCache.get(fullPath);
	}
	
	public void putIfAbsent(String fullPath,ErrorCode code){
		beanMetaDataCache.putIfAbsent(fullPath, code);
	}
}
