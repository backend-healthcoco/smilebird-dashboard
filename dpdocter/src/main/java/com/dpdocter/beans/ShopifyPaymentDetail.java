package com.dpdocter.beans;

public class ShopifyPaymentDetail {

	private String credit_card_bin;
	
	private String avs_result_code;
	
	private String cvv_result_code;
	
	private String credit_card_number;
	
	private String credit_card_company;

	public String getCredit_card_bin() {
		return credit_card_bin;
	}

	public void setCredit_card_bin(String credit_card_bin) {
		this.credit_card_bin = credit_card_bin;
	}

	public String getAvs_result_code() {
		return avs_result_code;
	}

	public void setAvs_result_code(String avs_result_code) {
		this.avs_result_code = avs_result_code;
	}

	public String getCvv_result_code() {
		return cvv_result_code;
	}

	public void setCvv_result_code(String cvv_result_code) {
		this.cvv_result_code = cvv_result_code;
	}

	public String getCredit_card_number() {
		return credit_card_number;
	}

	public void setCredit_card_number(String credit_card_number) {
		this.credit_card_number = credit_card_number;
	}

	public String getCredit_card_company() {
		return credit_card_company;
	}

	public void setCredit_card_company(String credit_card_company) {
		this.credit_card_company = credit_card_company;
	}
	
	
}
