package com.wangyin.asset.app.validator.cache;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import com.wangyin.asset.app.framework.common.validator.annotation.ErrorCode;
import com.wangyin.asset.app.validator.Constants;
/**
 * 提供错误码管理功能
 * @author yangtao
 *
 */
public class ErrorCodeManager {
	private static ErrorCode DEFAULT_ANNOTATION = null;
	private static final String TYPE_SEPRATOR = "_";
	
	
	/**
	 * 获取默认的错误注解
	 * @return
	 */
	public static ErrorCode defualtError(){
		if(DEFAULT_ANNOTATION == null){
			synchronized (TYPE_SEPRATOR) {
				if(DEFAULT_ANNOTATION!=null){
					return DEFAULT_ANNOTATION;
				}
				try {
					DEFAULT_ANNOTATION = Constants.class.getField("DEFAULT_ERROR_ANNOTATION").getAnnotation(ErrorCode.class);
				} catch (SecurityException e) {
					throw new RuntimeException("严重异常");
				} catch (NoSuchFieldException e) {
					throw new RuntimeException("严重异常");
				}
			}
			
		}
		return DEFAULT_ANNOTATION;
	}
	/**
	 * 获取注解信息,如果解析注解信息不存在,则指定一个默认的注解信息
	 * @param type
	 * @param fiedPath
	 * @return
	 */
	public static ErrorCode getErrorCode(Class<?> type,String fiedPath){
		ErrorCode result = ErrorCodeMetaCache.instance().getErrorAnnotation(toFullPath(type, fiedPath));
		if(result == null){
			result = resolveAndCache(type,fiedPath);
		}
		return result;
	}
	
	/**
	 * 解析默认的注解，如果没有，则认为默认的注解
	 * @param type
	 * @param fiedPath
	 * @return
	 */
	private static ErrorCode resolveAndCache(Class<?> type,String fiedPath){
		String[] fieds = fiedPath.split("\\.");
		Class<?> tempType = type;
		Class<?> lastType = null;
		PropertyDescriptor descriptor = null;
		for (String field : fieds) {
			try {
				descriptor = new PropertyDescriptor(field, tempType);
				lastType = tempType;
				tempType = descriptor.getPropertyType();
			} catch (IntrospectionException e) {
				throw new RuntimeException(tempType.getCanonicalName()+"字段可能缺少get或set方法,fieldName="+field);
			}
		}
		ErrorCode errorCode = null;
		try {
			errorCode = getFieldAnnotion(lastType, descriptor.getName());
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
		if(errorCode == null){
			errorCode = defualtError();
		}
		ErrorCodeMetaCache.instance().putIfAbsent(toFullPath(type, fiedPath), errorCode);
		return errorCode;
	}
	
	private static String toFullPath(Class<?> type,String fiedPath){
		return type.getCanonicalName()+TYPE_SEPRATOR+fiedPath;
	}
	
	private static ErrorCode getFieldAnnotion(final Class<?> cls,String fieldName){
        for (Class<?> acls = cls; acls != null; acls = acls.getSuperclass()) {
            try {
                Field field = acls.getDeclaredField(fieldName);
                return field.getAnnotation(ErrorCode.class);
            } catch (NoSuchFieldException ex) { // NOPMD
                // ignore
            }
        }
        return null;
	}
}
