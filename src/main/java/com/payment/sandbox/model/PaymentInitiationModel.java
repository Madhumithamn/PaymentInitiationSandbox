package com.payment.sandbox.model;

import java.util.Map;

public class PaymentInitiationModel {

	private PaymentInitiationRequest initiationRequest;
	
	private  Map<String, String> headers;

	public PaymentInitiationRequest getInitiationRequest() {
		return initiationRequest;
	}

	public void setInitiationRequest(PaymentInitiationRequest initiationRequest) {
		this.initiationRequest = initiationRequest;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	
}
