package com.wangyin.asset.app.validator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.validation.Configuration;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.spi.ValidationProvider;

import com.wangyin.asset.app.validator.interceptor.LocalMsgInterceptor;

/**
 * 获取校验实现类
 * @author yangtao
 *
 */
public class ValidatorFactory {
	
	private String provider = null;

	private Map<String,String> configProperties = new HashMap<String,String>();
	
	private MessageInterpolator messageInterpolator;
	
	private static final String DEFAULT_IMPLEMENTION = "org.hibernate.validator.HibernateValidator";
	
	private static final String HIBERNATE_FAIL_FAST_FLAG = "hibernate.validator.fail_fast";
	
	private static final MessageInterpolator DEFAULT_MSG_INTERCEPTOR = new LocalMsgInterceptor();
	
	public Validator getValidator(){
		@SuppressWarnings("unchecked")
		Configuration<?> config = Validation.byProvider(loadImplmentionClazz()).configure();
		setConfig(config);
		javax.validation.ValidatorFactory factory = config.buildValidatorFactory();
		Validator validator = factory.getValidator();
		return validator;
	}
	
	public MessageInterpolator getMessageInterpolator() {
		if(messageInterpolator == null){
			messageInterpolator = DEFAULT_MSG_INTERCEPTOR;
		}
		return messageInterpolator;
	}

	public void setMessageInterpolator(MessageInterpolator messageInterpolator) {
		this.messageInterpolator = messageInterpolator;
	}
	
	public void addConfigProperties(String p,String v){
		configProperties.put(p, v);
		
	}
	
	public Map<String, String> getConfigProperties() {
		return configProperties;
	}

	public void setConfigProperties(Map<String, String> configProperties) {
		this.configProperties = configProperties;
	}
	
	public String getProvider() {
		if(provider == null || provider.isEmpty()){
			setProvider(DEFAULT_IMPLEMENTION);
		}
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	private void setConfig(Configuration<?> config ){
		config.ignoreXmlConfiguration();//忽略任何XML配置文件
		config.messageInterpolator(getMessageInterpolator());
		if(DEFAULT_IMPLEMENTION.equals(getProvider())){
			config.addProperty( HIBERNATE_FAIL_FAST_FLAG, "true" );//默认快速失败
		}
		if(!configProperties.isEmpty()){
			for (Iterator<String> iterator = configProperties.keySet().iterator(); iterator.hasNext();) {
				String key = iterator.next();
				config.addProperty(key, configProperties.get(key));
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Class<ValidationProvider> loadImplmentionClazz(){
		try {
			return (Class<ValidationProvider>) Class.forName(getProvider());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("classpath下是不否存在类"+getProvider());
		}catch (Exception e) {
			throw new RuntimeException(getProvider()+"不是ValidationProvider的实现类",e);
		}
	}
}
