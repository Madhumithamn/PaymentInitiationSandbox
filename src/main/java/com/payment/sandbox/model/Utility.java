package com.payment.sandbox.model;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.stream.Stream;

import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class Utility {
	private Utility() {
	}

	public static final String HEADER_XREQUEST_ID = "x-request-id";
	public static final String HEADER_SIGNATURE = "signature";
	public static final String HEADER_SIGNATURE_CERTIFICATE = "signature-certificate";

	public static enum VALIDATIONSTATUS {
		VALIDATIONPASS(201), UNKNOWN_CERTIFICATE(400), INVALID_SIGNATURE(400), INVALID_REQUEST(400), LIMIT_EXCEEDED(422), GENERAL_ERROR(500);

		private final int errorCode;

		VALIDATIONSTATUS(int code) {
			errorCode = code;
		}

		public int getErrorCode() {
			return errorCode;
		}

	}

	public static final int sum(String debtorIBAN) {
		
		int sum =debtorIBAN.chars().filter(x->Character.isDigit(x)).map(x->Character.getNumericValue(x)).sum();
		return sum;
	}

	public static final boolean isNumeric(String str) {
		return str.matches("-?[0-9]+(\\.[0-9]{1,3})?");
	}

	public static Signature getSignature(PaymentInitiationModel payInitiationModel) throws NoSuchAlgorithmException,
			SignatureException, InvalidKeySpecException, IOException, InvalidKeyException {
		ObjectMapper obj = new ObjectMapper();
		String xRequestId = payInitiationModel.getHeaders().get(Utility.HEADER_XREQUEST_ID);
		String requestBody = obj.writeValueAsString(payInitiationModel.getInitiationRequest());

		Signature rsaSha256Signature = Signature.getInstance("SHA256withRSA");
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		Path path = Paths.get("src/main/resources/privateKey");
		PEMParser pemParser = new PEMParser(
				new BufferedReader(new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(path)))));
		PEMKeyPair pemCertObj = (PEMKeyPair) pemParser.readObject();
		PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(pemCertObj.getPrivateKeyInfo().getEncoded());
		PrivateKey privKey = keyFactory.generatePrivate(privKeySpec);
		messageDigest.digest(requestBody.getBytes());
		rsaSha256Signature.initSign(privKey);
		rsaSha256Signature.update(xRequestId.getBytes());
		rsaSha256Signature.update(messageDigest.digest(requestBody.getBytes()));
		pemParser.close();
		return rsaSha256Signature;
	}

}
