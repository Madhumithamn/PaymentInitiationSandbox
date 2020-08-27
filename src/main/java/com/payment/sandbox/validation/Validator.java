package com.payment.sandbox.validation;

import com.payment.sandbox.exception.PaymentInitiationValidationException;

@FunctionalInterface
public interface Validator<T> {

	public boolean validate(T object) throws PaymentInitiationValidationException;
	
}
