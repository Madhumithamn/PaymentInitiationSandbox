package com.payment.sandbox.model;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class Utility {
	private Utility() {
	}

	public static final ObjectMapper OBJMAPPER = new ObjectMapper();
	public static final String HEADER_XREQUEST_ID = "x-request-id";
	public static final String HEADER_SIGNATURE = "signature";
	public static final String HEADER_SIGNATURE_CERTIFICATE = "signature-certificate";
	public static final String INVALID_SIGNATURE_MESSAGE = "Invalid Signature certificate";

	public enum VALIDATIONSTATUS {
		VALIDATIONPASS(201), UNKNOWN_CERTIFICATE(400), INVALID_SIGNATURE(400), INVALID_REQUEST(400),
		LIMIT_EXCEEDED(422), GENERAL_ERROR(500);

		private final int errorCode;

		VALIDATIONSTATUS(int code) {
			errorCode = code;
		}

		public int getErrorCode() {
			return errorCode;
		}

	}

	public static final int sum(String debtorIBAN) {
		return debtorIBAN.chars().filter(Character::isDigit).map(Character::getNumericValue).sum();
	}

	public static final boolean isNumeric(String str) {
		return str.matches("-?[0-9]+(\\.[0-9]{1,3})?");
	}

	public static final Signature getRequestSignature(PaymentInitiationModel payInitiationModel)
			throws NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, IOException,
			InvalidKeyException {
		String xRequestId = payInitiationModel.getHeaders().get(Utility.HEADER_XREQUEST_ID);
		String requestBody = OBJMAPPER.writeValueAsString(payInitiationModel.getInitiationRequest());
		return getSignatureValue(xRequestId, requestBody);
	}

	public static final Signature getResponseSignature(String xRequestId, PaymentInitiationResponse body)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException,
			IOException {
		String requestBody = OBJMAPPER.writeValueAsString(body);
		return getSignatureValue(xRequestId, requestBody);
	}

	private static final Signature getSignatureValue(String xRequestId, String body) throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {
		Signature rsaSha256Signature = Signature.getInstance("SHA256withRSA");
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		Path path = Paths.get("src/main/resources/privateKey");
		PEMParser pemParser = new PEMParser(
				new BufferedReader(new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(path)))));
		PEMKeyPair pemCertObj = (PEMKeyPair) pemParser.readObject();
		PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(pemCertObj.getPrivateKeyInfo().getEncoded());
		PrivateKey privKey = keyFactory.generatePrivate(privKeySpec);
		messageDigest.digest(body.getBytes());
		rsaSha256Signature.initSign(privKey);
		rsaSha256Signature.update(xRequestId.getBytes());
		rsaSha256Signature.update(messageDigest.digest(body.getBytes()));
		pemParser.close();
		return rsaSha256Signature;
	}

	public static Map<String, String> getSignatureAndCertificate(String xRequestId, PaymentInitiationResponse body)
			throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException,
			InvalidKeySpecException, IOException {
		String signature = Base64.getEncoder().encodeToString(getResponseSignature(xRequestId, body).sign());
		CertificateFactory fact = CertificateFactory.getInstance("X.509");
		FileInputStream is = new FileInputStream("src/main/resources/publicKey");
		X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
		PublicKey key = cer.getPublicKey();
		String signatureCertificate = Base64.getEncoder().encodeToString(key.getEncoded());
		Map<String, String> headerString = new HashMap<>();
		headerString.put(HEADER_SIGNATURE, signature);
		headerString.put(HEADER_SIGNATURE_CERTIFICATE, signatureCertificate);
		return headerString;

	}

}
