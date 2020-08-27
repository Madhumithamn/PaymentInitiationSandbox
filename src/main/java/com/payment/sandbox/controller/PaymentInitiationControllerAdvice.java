package com.payment.sandbox.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.payment.sandbox.model.PaymentInitiationResponse;
import com.payment.sandbox.model.Utility.VALIDATIONSTATUS;

@ControllerAdvice
public class PaymentInitiationControllerAdvice {
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentInitiationController.class);

@ExceptionHandler(value=Throwable.class)
 public PaymentInitiationResponse handleException(Throwable th) {
	
	PaymentInitiationResponse response = new PaymentInitiationResponse();
	response.setResponseStatusCode(VALIDATIONSTATUS.GENERAL_ERROR.getErrorCode());
	response.setResponseStatusMessage(VALIDATIONSTATUS.GENERAL_ERROR.toString());
	logger.error("error occurred in system {} ",th);
	 return response;
 }
}
