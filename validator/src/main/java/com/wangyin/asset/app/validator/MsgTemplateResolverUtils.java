package com.wangyin.asset.app.validator;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolation;

public class MsgTemplateResolverUtils {
	
	private static final String MESSAGE_KEY = "message";
	private static final String PAYLOAD_KEY = "payload";
	private static final String GROUPS_KEY = "groups";
	private static final String VALUE_KEY = "value";
	private static final String FLAGS_KEY = "flags";

	
	public static String getConditonVal(Map<String, Object> attrs){
		if(attrs.get(VALUE_KEY)!=null){
			return attrs.get(VALUE_KEY).toString();
		}
		Map<String,Object> map = new HashMap<String, Object>(attrs);
		map.remove(MESSAGE_KEY);
		map.remove(PAYLOAD_KEY);
		map.remove(GROUPS_KEY);
		map.remove(FLAGS_KEY);
		if(map.isEmpty()){
			return "";
		}
		return map.toString();
	}
	
	public static String getMsgFromViolation(ConstraintViolation<?> violation ){
		String interceptedMsg = Constants.DEFAULT_MSG_MAP.get( violation.getMessage());
		if(interceptedMsg==null){
			return violation.getMessage();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(violation.getRootBeanClass().getCanonicalName()).append("字段").append(violation.getPropertyPath().toString());
		sb.append(interceptedMsg);
		sb.append(getConditonVal(violation.getConstraintDescriptor().getAttributes()));
		sb.append(",实际值为：");
		sb.append(violation.getInvalidValue());
		return sb.toString();
	}
}
