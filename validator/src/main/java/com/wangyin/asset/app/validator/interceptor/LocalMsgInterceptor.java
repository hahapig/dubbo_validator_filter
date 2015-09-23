package com.wangyin.asset.app.validator.interceptor;

import java.util.Locale;

import javax.validation.MessageInterpolator;

/**
 * 不支持国际化的消息解析器--通过自定义拦截器，避免引入el包
 * @author yangtao
 *
 */
public class LocalMsgInterceptor implements MessageInterpolator{
	
	public String interpolate(String messageTemplate, Context context) {
		return messageTemplate;
	}

	public String interpolate(String messageTemplate, Context context,
			Locale locale) {
		// TODO hibernate-validation的实现，从来不会被调用 never-never
		return messageTemplate;
	}

}
