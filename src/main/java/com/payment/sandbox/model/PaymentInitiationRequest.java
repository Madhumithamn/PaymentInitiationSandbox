package com.payment.sandbox.model;

public class PaymentInitiationRequest {

	private String debtorIBAN;
	private String creditorIBAN;
	private String amount;
	public String getDebtorIBAN() {
		return debtorIBAN;
	}
	public void setDebtorIBAN(String debtorIBAN) {
		this.debtorIBAN = debtorIBAN;
	}
	public String getCreditorIBAN() {
		return creditorIBAN;
	}
	public void setCreditorIBAN(String creditorIBAN) {
		this.creditorIBAN = creditorIBAN;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
}
