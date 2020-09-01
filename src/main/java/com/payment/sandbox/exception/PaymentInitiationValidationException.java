package com.payment.sandbox.exception;

import com.payment.sandbox.model.Utility.VALIDATIONSTATUS;

public class PaymentInitiationValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8934724450665989223L;
	private final int errorCode;
	private final String msg;
	private final VALIDATIONSTATUS validationStatus;
	
	public PaymentInitiationValidationException(int errorCode, String msg,VALIDATIONSTATUS validationStatus) {
		super(msg);
		this.errorCode = errorCode;
		this.msg = msg;
		this.validationStatus=validationStatus;
	}

	public int getErrorCode() {
		return errorCode;
	}

	
	public String getMsg() {
		return msg;
	}

	
	public VALIDATIONSTATUS getValidationStatus() {
		return validationStatus;
	}

	
	

}
