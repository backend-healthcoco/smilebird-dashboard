package com.dpdocter.beans;

import com.dpdocter.enums.Currency;

public class ConsultationFee {
    private int amount;

    private Currency currency = Currency.INR;

    public ConsultationFee() {
	}

	public ConsultationFee(int amount, Currency currency) {
		this.amount = amount;
		this.currency = currency;
	}

	public int getAmount() {
	return amount;
    }

    public void setAmount(int amount) {
	this.amount = amount;
    }

    public Currency getCurrency() {
	return currency;
    }

    public void setCurrency(Currency currency) {
	this.currency = currency;
    }

    @Override
    public String toString() {
	return "{amount=" + amount + ", currency=" + currency + "}";
    }

}
