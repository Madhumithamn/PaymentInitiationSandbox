package com.payment.sandbox.exception;

public class PaymentInitiationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8934724450665989223L;
	private String errorCode;
	private String msg;

	public PaymentInitiationException(String errorCode, String msg) {
		super(msg);
		this.errorCode = errorCode;
		this.msg = msg;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
