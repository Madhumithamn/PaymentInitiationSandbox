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

	private static final Logger LOG = LoggerFactory.getLogger(PaymentInitiationValidator.class);
	
	@Autowired
	@Qualifier("consumerValidator")
	private Validator<PaymentInitiationModel> consumerValidator;
	
	@Autowired
	@Qualifier("requestValidator")
	private Validator<PaymentInitiationModel> requestValidator;

	public PaymentInitiationResponse validation(PaymentInitiationModel paymentInitiationModel) throws PaymentInitiationValidationException {
		LOG.debug("Payment initiation validation..");
		consumerValidator.validate(paymentInitiationModel);
		requestValidator.validate(paymentInitiationModel);
		PaymentInitiationResponse paymentIntiationResponse = new PaymentInitiationResponse();
		paymentIntiationResponse.setResponseStatusCode(VALIDATIONSTATUS.VALIDATIONPASS.getErrorCode());
		paymentIntiationResponse.setResponseStatusMessage(VALIDATIONSTATUS.VALIDATIONPASS.toString());
		return paymentIntiationResponse;
	}

}