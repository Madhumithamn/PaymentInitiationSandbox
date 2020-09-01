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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.sandbox.exception.PaymentInitiationValidationException;
import com.payment.sandbox.model.PaymentInitiationModel;
import com.payment.sandbox.model.PaymentInitiationRequest;
import com.payment.sandbox.model.PaymentInitiationResponse;
import com.payment.sandbox.model.Utility;
import com.payment.sandbox.validation.PaymentInitiationValidator;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/v1.0.0/")
public class PaymentInitiationController {

	private static final Logger logger = LoggerFactory.getLogger(PaymentInitiationController.class);

	@Autowired
	private PaymentInitiationValidator paymentIntiationValidator;

	@PostMapping(value = "initiate-payment", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<PaymentInitiationResponse> initiatePayment(
			@RequestBody PaymentInitiationRequest initationRequest, @RequestHeader Map<String, String> headers)
			throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException,
			IOException, CertificateException, PaymentInitiationValidationException {
		logger.info("Request in PaymentInitiationController {}", initationRequest);
		PaymentInitiationModel initiationModel = new PaymentInitiationModel();
		initiationModel.setHeaders(headers);
		initiationModel.setInitiationRequest(initationRequest);
		PaymentInitiationResponse paymentIntiationResponse = paymentIntiationValidator.validation(initiationModel);

		HttpHeaders responseHeaders = new HttpHeaders();
		Map<String, String> headerMap = Utility.getSignatureAndCertificate(headers.get(Utility.HEADER_XREQUEST_ID),
				paymentIntiationResponse);
		responseHeaders.add(Utility.HEADER_SIGNATURE, headerMap.get(Utility.HEADER_SIGNATURE));
		responseHeaders.add(Utility.HEADER_SIGNATURE_CERTIFICATE, headerMap.get(Utility.HEADER_SIGNATURE_CERTIFICATE));
		logger.info("Response in PaymentInitiationController {}", paymentIntiationResponse);
		return ResponseEntity.accepted().headers(responseHeaders).body(paymentIntiationResponse);
	}
}
