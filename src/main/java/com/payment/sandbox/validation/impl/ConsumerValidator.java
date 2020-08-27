package com.payment.sandbox.validation.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.sandbox.exception.PaymentInitiationValidationException;
import com.payment.sandbox.model.PaymentInitiationModel;
import com.payment.sandbox.model.Utility;
import com.payment.sandbox.model.Utility.VALIDATIONSTATUS;
import com.payment.sandbox.validation.Validator;

@Component("consumerValidator")
public class ConsumerValidator implements Validator<PaymentInitiationModel> {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerValidator.class);
	private ObjectMapper obj = new ObjectMapper();

	@Value("${subject.commonname}")
	private String commonName;

	@Override
	public boolean validate(PaymentInitiationModel payInitiationModel) throws PaymentInitiationValidationException {
		whiteListCertValidation(payInitiationModel.getHeaders().get(Utility.HEADER_SIGNATURE_CERTIFICATE));
		signatureValidation(payInitiationModel);
		
		return false;
	}

	public void whiteListCertValidation(String base64EncodedSignature) throws PaymentInitiationValidationException {
		try {
			
			CertificateFactory fact = CertificateFactory.getInstance("X.509");
			byte[] decodedSignature = Base64.getDecoder().decode(base64EncodedSignature);
			X509Certificate cer = (X509Certificate) fact
					.generateCertificate(new ByteArrayInputStream(decodedSignature));
			String certificateName = cer.getSubjectDN().getName();
			if (null == certificateName || certificateName.isEmpty()) {
				throw new PaymentInitiationValidationException(VALIDATIONSTATUS.UNKNOWN_CERTIFICATE.getErrorCode(),
						"Invalid Signature certificate", VALIDATIONSTATUS.UNKNOWN_CERTIFICATE);
			}
			String[] splitted = Arrays.stream(certificateName.split(",")).map(String::trim).toArray(String[]::new);
			Map<String, String> subjectNameMap = Arrays.asList(splitted).stream().map(str -> str.split("="))
					.collect(Collectors.toMap(str -> str[0], str -> str[1]));
			if (null == subjectNameMap || null == subjectNameMap.get("CN") || subjectNameMap.get("CN").isEmpty()
					|| !subjectNameMap.get("CN").startsWith(commonName)) {
				throw new PaymentInitiationValidationException(VALIDATIONSTATUS.UNKNOWN_CERTIFICATE.getErrorCode(),
						"Invalid Signature certificate", VALIDATIONSTATUS.UNKNOWN_CERTIFICATE);
			}
		} catch (CertificateException ex) {
			logger.error("Error occurred in whiteListCertValidation {}", ex);
			throw new PaymentInitiationValidationException(VALIDATIONSTATUS.UNKNOWN_CERTIFICATE.getErrorCode(),
					"Invalid Signature certificate", VALIDATIONSTATUS.UNKNOWN_CERTIFICATE);

		}

	}

	public void signatureValidation(PaymentInitiationModel payInitiationModel)
			throws PaymentInitiationValidationException {
		try {
			String xRequestId = payInitiationModel.getHeaders().get(Utility.HEADER_XREQUEST_ID);
			String requestBody = obj.writeValueAsString(payInitiationModel.getInitiationRequest());
			String base64EncodedSignature = payInitiationModel.getHeaders().get(Utility.HEADER_SIGNATURE_CERTIFICATE);
			Signature rsaSha256Signature = Utility.getSignature(payInitiationModel);
			// Verify
			CertificateFactory fact = CertificateFactory.getInstance("X.509");
			byte[] decodedSignature = Base64.getDecoder().decode(base64EncodedSignature);
			X509Certificate cer = (X509Certificate) fact
					.generateCertificate(new ByteArrayInputStream(decodedSignature));
			PublicKey key = cer.getPublicKey();
			Signature verifyRsaSha256Signature = Signature.getInstance("SHA256withRSA");
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			verifyRsaSha256Signature.initVerify(key);
			verifyRsaSha256Signature.update(xRequestId.getBytes());
			verifyRsaSha256Signature.update(messageDigest.digest(requestBody.getBytes()));

			if (!(verifyRsaSha256Signature.verify(rsaSha256Signature.sign()))) {								
				throw new PaymentInitiationValidationException(VALIDATIONSTATUS.UNKNOWN_CERTIFICATE.getErrorCode(),
						"Invalid Signature certificate", VALIDATIONSTATUS.UNKNOWN_CERTIFICATE);
			}
		} catch (IOException | NoSuchAlgorithmException jex) {
			throw new PaymentInitiationValidationException(VALIDATIONSTATUS.GENERAL_ERROR.getErrorCode(),
					"General Error", VALIDATIONSTATUS.GENERAL_ERROR);
			
		} catch (CertificateException | InvalidKeyException | SignatureException | InvalidKeySpecException e) {
			throw new PaymentInitiationValidationException(VALIDATIONSTATUS.INVALID_SIGNATURE.getErrorCode(),
					"Invalid Signature certificate", VALIDATIONSTATUS.INVALID_SIGNATURE);
		}
	}

}
