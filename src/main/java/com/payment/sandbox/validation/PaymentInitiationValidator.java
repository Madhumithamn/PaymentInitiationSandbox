package com.payment.sandbox.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.payment.sandbox.exception.PaymentInitiationValidationException;
import com.payment.sandbox.model.PaymentInitiationModel;
import com.payment.sandbox.model.PaymentInitiationResponse;
import com.payment.sandbox.model.Utility.VALIDATIONSTATUS;

@Component
public class PaymentInitiationValidator {

	private static final Logger logger = LoggerFactory.getLogger(PaymentInitiationValidator.class);
	
	@Autowired
	@Qualifier("consumerValidator")
	private Validator<PaymentInitiationModel> consumerValidator;
	
	@Autowired
	@Qualifier("requestValidator")
	private Validator<PaymentInitiationModel> requestValidator;

	public PaymentInitiationResponse validation(PaymentInitiationModel paymentInitiationModel) {
		try {
		consumerValidator.validate(paymentInitiationModel);
		requestValidator.validate(paymentInitiationModel);
		}catch(PaymentInitiationValidationException e) {
			logger.error("Error "+e);
			return buildRespone(e);
		}
		PaymentInitiationResponse paymentIntiationResponse = new PaymentInitiationResponse();
		paymentIntiationResponse.setResponseStatusCode(VALIDATIONSTATUS.VALIDATIONPASS.getErrorCode());
		paymentIntiationResponse.setResponseStatusMessage(VALIDATIONSTATUS.VALIDATIONPASS.toString());
		return paymentIntiationResponse;
	}

	private PaymentInitiationResponse buildRespone(PaymentInitiationValidationException e) {
		PaymentInitiationResponse paymentIntiationResponse = new PaymentInitiationResponse();
		paymentIntiationResponse.setResponseStatusCode(e.getErrorCode());
		paymentIntiationResponse.setResponseStatusMessage(e.getValidationStatus().toString());
	 return paymentIntiationResponse;
	}


}