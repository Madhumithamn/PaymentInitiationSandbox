package com.payment.sandbox.validation.impl;

import org.springframework.stereotype.Component;

import com.payment.sandbox.exception.PaymentInitiationValidationException;
import com.payment.sandbox.model.PaymentInitiationModel;
import com.payment.sandbox.model.Utility;
import com.payment.sandbox.model.Utility.VALIDATIONSTATUS;
import com.payment.sandbox.validation.Validator;

@Component("requestValidator")
public class RequestValidator implements Validator<PaymentInitiationModel> {
	

	@Override
	public boolean validate(PaymentInitiationModel payInitiationModel) throws PaymentInitiationValidationException {
		requestValidation(payInitiationModel);
		amountLimitExeeded(payInitiationModel);
		return false;
	}

	public void requestValidation(PaymentInitiationModel paymentModel) throws PaymentInitiationValidationException {
		if ((iBANValidation(paymentModel.getInitiationRequest().getCreditorIBAN())) || (iBANValidation(paymentModel.getInitiationRequest().getDebtorIBAN()))) {
			throw new PaymentInitiationValidationException(VALIDATIONSTATUS.INVALID_REQUEST.getErrorCode(),
					"Invalid request", VALIDATIONSTATUS.INVALID_REQUEST);

		}
	}

	public void amountLimitExeeded(PaymentInitiationModel paymentModel) throws PaymentInitiationValidationException {
		 if(!(Utility.isNumeric(paymentModel.getInitiationRequest().getAmount()))){
				throw new PaymentInitiationValidationException(VALIDATIONSTATUS.GENERAL_ERROR.getErrorCode(),
						"General Error", VALIDATIONSTATUS.GENERAL_ERROR);
			}

		if ((Double.valueOf(paymentModel.getInitiationRequest().getAmount()) > 0)
				&& ((Utility.sum(paymentModel.getInitiationRequest().getDebtorIBAN()))
						% paymentModel.getInitiationRequest().getDebtorIBAN().length() == 0)) {
			throw new PaymentInitiationValidationException(VALIDATIONSTATUS.LIMIT_EXCEEDED.getErrorCode(),
					"Amount Limit Exceeded", VALIDATIONSTATUS.LIMIT_EXCEEDED);
		} 
	}

	

	private boolean iBANValidation(String iban) {
		String iBanPattern = "[A-Z]{2}[0-9]{2}[a-zA-Z0-9]{1,30}";
		if (!(iban.matches(iBanPattern))) {
			return true;
		}

		return false;
	}

}
