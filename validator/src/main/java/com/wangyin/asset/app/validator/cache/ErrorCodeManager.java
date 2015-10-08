package com.wangyin.asset.app.validator.cache;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

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
	public static ErrorCode getErrorCode(Object val,String fiedPath){
		ErrorCode result = ErrorCodeMetaCache.instance().getErrorAnnotation(toFullPath(val.getClass(), fiedPath));
		if(result == null){
			result = resolveAndCache(val,fiedPath);
		}
		return result;
	}
	
	/**
	 * 解析默认的注解，如果没有，则认为默认的注解
	 * @param type
	 * @param fiedPath
	 * @return
	 */
	private static ErrorCode resolveAndCache(final Object val,String fiedPath){
		String[] fieds = fiedPath.split("\\.");
		Class<?> lastType = null;
		Object tempVal = val;
		PropertyDescriptor descriptor = null;
		for (String field : fieds) {
			try {
				final String realName = getRealFieldName(field);//获取可用的属性名
				descriptor = new PropertyDescriptor(realName, tempVal.getClass());
				Method method = descriptor.getReadMethod();
				tempVal = method.invoke(tempVal);//获取到属性对应的值
				tempVal = getVal(tempVal,field,realName!=field);//如果是属性值为数组或者集合需要特殊处理,解析出真实值，进行下次迭代
				lastType = tempVal.getClass();
			} catch (IntrospectionException e) {
				throw new RuntimeException(tempVal.getClass().getCanonicalName()+"字段可能缺少get或set方法,fieldName="+field);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("无法解析出errorCode注解");
			} catch (IllegalAccessException e) {
				throw new RuntimeException("无法解析出errorCode注解");
			} catch (InvocationTargetException e) {
				throw new RuntimeException("无法解析出errorCode注解");
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
		ErrorCodeMetaCache.instance().putIfAbsent(toFullPath(val.getClass(), fiedPath), errorCode);
		return errorCode;
	}
	//如果类型为数组或者集合，需要特殊处理
	private static Object getVal(Object arrayOrCollect,String field,boolean nameChanged){
		if(!nameChanged){
			return arrayOrCollect;
		}
		int index = Integer.parseInt(field.substring(field.indexOf('[')+1, field.indexOf(']')));
		if(arrayOrCollect.getClass().isArray()){
			Object[] x = (Object[])arrayOrCollect;
			return x[index];
		}
		
		if(arrayOrCollect instanceof Collection){
			Iterator<Object> it = ((Collection) arrayOrCollect).iterator();
			Object temp = null;
			for (int i = 0; i <= index; i++) {
				temp = it.next();
			}
			return temp;
		}
		return arrayOrCollect;
	}
	//数组或者集合的名字比较吊~需要特殊进行处理
	private static String getRealFieldName(String fieldName){
		if(fieldName == null){
			return null;
		}
		if(fieldName.endsWith("]")){//处理数组
			return fieldName.substring(0, fieldName.indexOf('['));
		}
		return fieldName;
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
