package com.wangyin.asset.app.validator;

import java.util.HashMap;
import java.util.Map;

import com.wangyin.asset.app.framework.common.code.ResultConstant;
import com.wangyin.asset.app.framework.common.validator.annotation.ErrorCode;

public abstract class Constants {
	
	@ErrorCode(ResultConstant.REQUEST_PARAMETER_ERROR.code)
	public static final String DEFAULT_ERROR_ANNOTATION = "";
	
	//约束默认消息模板
	public static final String AssertFalse = "{javax.validation.constraints.AssertFalse.message}";
	public static final String AssertTrue = "{javax.validation.constraints.AssertTrue.message}";
	public static final String DecimalMax = "{javax.validation.constraints.DecimalMax.message}";
	public static final String DecimalMin = "{javax.validation.constraints.DecimalMin.message}";
	public static final String Digits = "{javax.validation.constraints.Digits.message}";
	public static final String Future = "{javax.validation.constraints.Future.message}";
	public static final String Max = "{javax.validation.constraints.Max.message}";
	public static final String Min = "{javax.validation.constraints.Min.message}";
	public static final String NotNull = "{javax.validation.constraints.NotNull.message}";
	public static final String Null = "{javax.validation.constraints.Null.message}";
	public static final String Past = "{javax.validation.constraints.Past.message}";
	public static final String Pattern = "{javax.validation.constraints.Pattern.message}";
	public static final String Size = "{javax.validation.constraints.Size.message}";
	
	public static final Map<String, String> DEFAULT_MSG_MAP = new HashMap<String, String>(); 
	static{
		DEFAULT_MSG_MAP.put(AssertFalse, "只能为true");
		DEFAULT_MSG_MAP.put(AssertTrue, "只能为false");
		DEFAULT_MSG_MAP.put(DecimalMax, "不能超过最大值");
		DEFAULT_MSG_MAP.put(DecimalMin, "不能小于最小值");
		DEFAULT_MSG_MAP.put(Digits, "只能为数字");
		DEFAULT_MSG_MAP.put(Future, "只能为未来某个时间");
		DEFAULT_MSG_MAP.put(Max, "不能超过最大值");
		DEFAULT_MSG_MAP.put(Min, "不能小于");
		DEFAULT_MSG_MAP.put(NotNull, "不能为空");
		DEFAULT_MSG_MAP.put(Null, "只能为空");
		DEFAULT_MSG_MAP.put(Past, "只能为过去某个时间");
		DEFAULT_MSG_MAP.put(Pattern, "不符合正则表达式");
		DEFAULT_MSG_MAP.put(Size, "大小不符合范围");
	}
}
