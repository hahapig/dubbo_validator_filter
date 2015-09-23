package com.wangyin.asset.app.framework.common.domain;

/**
 * 通用的dubbo返回結果
 * @author YangTao
 *
 * @param <T>
 */
public class Result<T>{
	private String code;//状态码
	private String message;//状态消息提示
	private T data;//接口业务数据
	
	public Result(){
		//默认构造方法
	}
	public Result(String code, String message) {
		this.code = code;
		this.message = message;
	}
	public Result(String code, String message, T data) {
		this(code, message);
		this.data = data;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "Result [code=" + code + ", message=" + message + ", data=" + data + "]";
	}
	
}
