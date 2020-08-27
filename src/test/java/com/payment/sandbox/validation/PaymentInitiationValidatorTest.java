package com.payment.sandbox.validation;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.payment.sandbox.exception.PaymentInitiationValidationException;
import com.payment.sandbox.model.PaymentInitiationModel;
import com.payment.sandbox.model.PaymentInitiationResponse;
import com.payment.sandbox.model.Utility.VALIDATIONSTATUS;
import com.payment.sandbox.validation.impl.ConsumerValidator;
import com.payment.sandbox.validation.impl.RequestValidator;

@RunWith(MockitoJUnitRunner.class)
public class PaymentInitiationValidatorTest {

	@InjectMocks
	PaymentInitiationValidator paymentIntiationValidator;

	private static final Logger logger = LoggerFactory.getLogger(PaymentInitiationValidator.class);

	@Mock
	private ConsumerValidator consumerValidator;

	@Mock
	private RequestValidator requestValidator;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void unKnownCertificationValidation() throws PaymentInitiationValidationException {

		Mockito.when(consumerValidator.validate(Mockito.any()))
				.thenThrow(new PaymentInitiationValidationException(VALIDATIONSTATUS.UNKNOWN_CERTIFICATE.getErrorCode(),
						"UnKnown certificate", VALIDATIONSTATUS.UNKNOWN_CERTIFICATE));
		
		PaymentInitiationResponse res = paymentIntiationValidator.validation(Mockito.any());
		assertNotNull(res);
		assertEquals(400, res.getResponseStatusCode());
	}
	
	@Test
	public void unSignatureValidation() throws PaymentInitiationValidationException {

		Mockito.when(consumerValidator.validate(Mockito.any()))
				.thenThrow(new PaymentInitiationValidationException(VALIDATIONSTATUS.INVALID_SIGNATURE.getErrorCode(),
						"Invalid Signature certificate", VALIDATIONSTATUS.INVALID_SIGNATURE));
		
		PaymentInitiationResponse res = paymentIntiationValidator.validation(Mockito.any());
		assertNotNull(res);
		assertEquals(400, res.getResponseStatusCode());
	}

	@Test
	public void amountExceedValidation() throws PaymentInitiationValidationException {

		Mockito.when(requestValidator.validate(Mockito.any()))
				.thenThrow(new PaymentInitiationValidationException(VALIDATIONSTATUS.LIMIT_EXCEEDED.getErrorCode(),
						"Amount Limit Exceeded", VALIDATIONSTATUS.LIMIT_EXCEEDED));
		
		PaymentInitiationResponse res = paymentIntiationValidator.validation(Mockito.any());
		assertNotNull(res);
		assertEquals(422, res.getResponseStatusCode());
	}

	@Test
	public void requestValidation() throws PaymentInitiationValidationException {

		Mockito.when(requestValidator.validate(Mockito.any()))
				.thenThrow(new PaymentInitiationValidationException(VALIDATIONSTATUS.INVALID_REQUEST.getErrorCode(),
						"Invalid Request", VALIDATIONSTATUS.INVALID_REQUEST));
		
		PaymentInitiationResponse res = paymentIntiationValidator.validation(Mockito.any());
		assertNotNull(res);
		assertEquals(400, res.getResponseStatusCode());
	}

	
}
