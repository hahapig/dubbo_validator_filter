/**   
 * @Title: Resultcode.java 
 * @Package: com.wangyin.asset.app.framework.common.code 
 * @Description: TODO 
 * @author: zhouhang   
 * @date: 2015年9月21日 上午10:34:24 
 * @version: 1.0.0   
 */
package com.wangyin.asset.app.framework.common.code;

/**    
 * <b>项目名称</b>： asset-framework-common <br>
 * <b>包名称</b>： com.wangyin.asset.app.framework.common.code <br>
 * <b>类名称</b>： Resultcode.java <br>
 * <b>类描述</b>：返回结果常量  <br>
 * <b>创建人</b>：zhouhang <br>
 * <b>创建时间</b>：2015年9月21日 上午10:34:24 <br>
 * <b>修改人</b>：  <br>
 * <b>修改时间</b>：  <br>
 * <b>修改备注</b>：  <br>
 * @version 1.0.0  <br> 
 */
public interface ResultConstant {
	
	interface SUCCESS {
		String code = "000000";
		String message = "成功";
	}
	
	interface UNKOWN_ERROR {
		String code = "999999";
		String message = "未知错误";
	}
	
	interface DATABASE_CONSISTENCE_ERROR {
		String code = "01010001";
		String message = "违反数据库唯一性约束";
	}
	
	interface DATABASE_ERROR {
		String code = "01010000";
		String message = "数据库错误";
	}
	
	interface REQUEST_PARAMETER_ERROR {
		String code = "10010001";
		String message = "请求参数错误";
	}
	
	interface NO_SUCH_DATA {
		String code = "10010002";
		String message = "无此记录";
	}
	
	interface CALL_SERVICE_FAIL {
		String code = "05010001";
		String message = "服务调用失败";
	}
	
}
