package com.payment.sandbox.model;

public class PaymentInitiationResponse {
	
	private int responseStatusCode;
	private String responseStatusMessage;	
	
	
	public PaymentInitiationResponse() {
		super();
	}

	public PaymentInitiationResponse(int responseStatusCode, String responseStatusMessage) {
		super();
		this.responseStatusCode = responseStatusCode;
		this.responseStatusMessage = responseStatusMessage;
	}
	
	public int getResponseStatusCode() {
		return responseStatusCode;
	}
	public void setResponseStatusCode(int responseStatusCode) {
		this.responseStatusCode = responseStatusCode;
	}
	public String getResponseStatusMessage() {
		return responseStatusMessage;
	}
	public void setResponseStatusMessage(String responseStatusMessage) {
		this.responseStatusMessage = responseStatusMessage;
	}

	@Override
	public String toString() {
		return "PaymentInitiationResponse [responseStatusCode=" + responseStatusCode + ", responseStatusMessage="
				+ responseStatusMessage + "]";
	}
	

}
