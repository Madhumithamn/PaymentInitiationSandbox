package com.payment.sandbox.controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.payment.sandbox.exception.PaymentInitiationValidationException;
import com.payment.sandbox.model.PaymentInitiationResponse;
import com.payment.sandbox.model.Utility;
import com.payment.sandbox.model.Utility.VALIDATIONSTATUS;

@ControllerAdvice
public class PaymentInitiationControllerAdvice {

	private static final Logger logger = LoggerFactory.getLogger(PaymentInitiationControllerAdvice.class);

	@ExceptionHandler(value = Throwable.class)
	public ResponseEntity<PaymentInitiationResponse> handleException(Throwable th) {

		PaymentInitiationResponse response = new PaymentInitiationResponse();
		response.setResponseStatusCode(VALIDATIONSTATUS.GENERAL_ERROR.getErrorCode());
		response.setResponseStatusMessage(VALIDATIONSTATUS.GENERAL_ERROR.toString());
		logger.error("General error occurred in system ", th);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	@ExceptionHandler(value = PaymentInitiationValidationException.class)
	public ResponseEntity<PaymentInitiationResponse> handlePaymentInitiationValidatorException(
			PaymentInitiationValidationException e,WebRequest request) throws InvalidKeyException, CertificateException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {//PaymentInitiationRequest initationRequest
		PaymentInitiationResponse paymentIntiationResponse = new PaymentInitiationResponse();
		paymentIntiationResponse.setResponseStatusCode(e.getErrorCode());
		paymentIntiationResponse.setResponseStatusMessage(e.getValidationStatus().toString());
		HttpHeaders responseHeaders = new HttpHeaders();
		Map<String, String> headerMap = Utility.getSignatureAndCertificate(request.getHeader(Utility.HEADER_XREQUEST_ID),
				paymentIntiationResponse);
		responseHeaders.add(Utility.HEADER_SIGNATURE, headerMap.get(Utility.HEADER_SIGNATURE));
		responseHeaders.add(Utility.HEADER_SIGNATURE_CERTIFICATE, headerMap.get(Utility.HEADER_SIGNATURE_CERTIFICATE));
		return ResponseEntity.accepted().headers(responseHeaders).body(paymentIntiationResponse);
	}
}
