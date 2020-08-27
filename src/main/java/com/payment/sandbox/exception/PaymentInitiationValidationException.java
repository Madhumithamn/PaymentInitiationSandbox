package com.payment.sandbox.exception;

import com.payment.sandbox.model.Utility.VALIDATIONSTATUS;

public class PaymentInitiationValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8934724450665989223L;
	private int errorCode;
	private String msg;
	private VALIDATIONSTATUS validationStatus;
	
	

	public PaymentInitiationValidationException() {
		super();
	}

	public PaymentInitiationValidationException(int errorCode, String msg,VALIDATIONSTATUS validationStatus) {
		super(msg);
		this.errorCode = errorCode;
		this.msg = msg;
		this.validationStatus=validationStatus;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public VALIDATIONSTATUS getValidationStatus() {
		return validationStatus;
	}

	public void setValidationStatus(VALIDATIONSTATUS validationStatus) {
		this.validationStatus = validationStatus;
	}
	
	

}
