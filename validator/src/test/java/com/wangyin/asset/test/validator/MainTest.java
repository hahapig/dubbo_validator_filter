package com.wangyin.asset.test.validator;

import java.beans.IntrospectionException;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.wangyin.asset.app.framework.common.validator.annotation.ErrorCode;
import com.wangyin.asset.app.validator.ErrorCodeResolver;
import com.wangyin.asset.app.validator.MsgTemplateResolverUtils;
import com.wangyin.asset.app.validator.ValidatorFactory;

public class MainTest {
	private static Validator validator;
	/**
	 * @param args
	 * @throws IntrospectionException 
	 */
	public static void main(String[] args) throws IntrospectionException {
		ValidatorFactory validatorFactory = new ValidatorFactory();
        validator = validatorFactory.getValidator();
        Car car = new Car("Morris", "asdf",3);
        Set<ConstraintViolation<Car>> constraintViolations = validator.validate(car);
        ConstraintViolation<Car> violation = constraintViolations.iterator().next();
//        System.out.println(violation.getConstraintDescriptor().getAttributes());
        ErrorCodeResolver errorCodeResolver = new ErrorCodeResolver();
        errorCodeResolver.setOverrideDefaultErrorCode("覆盖后的错误码");
        String resovledMsg = MsgTemplateResolverUtils.getMsgFromViolation(violation);
        System.out.println(resovledMsg);
        System.out.println(violation.getMessage()+"errorCode="+errorCodeResolver.resolveErrorCode(violation));
	}
	
	static class Father{
		
		@ErrorCode("类型独有错误码")
		@Min(value=2)
		private int xxxxx=3;

		public int getXxxxx() {
			return xxxxx;
		}

		public void setXxxxx(int xxxxx) {
			this.xxxxx = xxxxx;
		}
		
	    @Valid
		private Son son = new Son();
		
		public Son getSon() {
			return son;
		}

		public void setSon(Son son) {
			this.son = son;
		}

		public void changeValue(){
			son.setSonVal("");
		}
		
	}
	
	static class Car extends Father{
		@NotNull
	    private String manufacturer;
		
	    @NotNull
	    @Size(message="xxxx",min = 2, max = 14)
	    private String licensePlate;
		
	    @NotNull
		@Min(value=2)
	    private int seatCount;
	    
		
		
		public Car(String manufacturer, String licencePlate, int seatCount) {
	        this.manufacturer = manufacturer;

	        this.licensePlate = licencePlate;

	        this.setSeatCount(seatCount);
	    }

		public String getManufacturer() {
			return manufacturer;
		}

		public void setManufacturer(String manufacturer) {
			this.manufacturer = manufacturer;
		}

		public String getLicensePlate() {
			return licensePlate;
		}

		public void setLicensePlate(String licensePlate) {
			this.licensePlate = licensePlate;
		}

		public int getSeatCount() {
			return seatCount;
		}

		public void setSeatCount(int seatCount) {
			this.seatCount = seatCount;
		}
	}
	
	static class Son{
		
		@ErrorCode("错误代码再次")
		@NotNull
	    @Size(min = 4, max = 14,message="asdfaf")
		private String sonVal = "123";

		public String getSonVal() {
			return sonVal;
		}

		public void setSonVal(String sonVal) {
			this.sonVal = sonVal;
		}
	}
}
