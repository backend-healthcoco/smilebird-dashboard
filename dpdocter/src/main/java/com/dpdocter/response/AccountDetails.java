package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.Address;

public class AccountDetails {

	private String mobile;
	
	private String landline;
	
	private String business_name;
	
	private String paymentdetails;
	
	private String business_model;
	
	private RegisteredAddress registered_address;
	
	private RegisteredAddress operational_address;
	
	private Long date_established;
	
	private String transaction_volume;
	
	private int average_transaction_size;
	
	private KycDetails kyc_details;
	
	private String business_type;
	

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getLandline() {
		return landline;
	}

	public void setLandline(String landline) {
		this.landline = landline;
	}

	public String getBusiness_name() {
		return business_name;
	}

	public void setBusiness_name(String business_name) {
		this.business_name = business_name;
	}

	public String getPaymentdetails() {
		return paymentdetails;
	}

	public void setPaymentdetails(String paymentdetails) {
		this.paymentdetails = paymentdetails;
	}

	public String getBusiness_model() {
		return business_model;
	}

	public void setBusiness_model(String business_model) {
		this.business_model = business_model;
	}

	
	public Long getDate_established() {
		return date_established;
	}

	public void setDate_established(Long date_established) {
		this.date_established = date_established;
	}

	public String getTransaction_volume() {
		return transaction_volume;
	}

	public void setTransaction_volume(String transaction_volume) {
		this.transaction_volume = transaction_volume;
	}

	public int getAverage_transaction_size() {
		return average_transaction_size;
	}

	public void setAverage_transaction_size(int average_transaction_size) {
		this.average_transaction_size = average_transaction_size;
	}

	public KycDetails getKyc_details() {
		return kyc_details;
	}

	public void setKyc_details(KycDetails kyc_details) {
		this.kyc_details = kyc_details;
	}

	public String getBusiness_type() {
		return business_type;
	}

	public void setBusiness_type(String business_type) {
		this.business_type = business_type;
	}

	public RegisteredAddress getRegistered_address() {
		return registered_address;
	}

	public void setRegistered_address(RegisteredAddress registered_address) {
		this.registered_address = registered_address;
	}

	public RegisteredAddress getOperational_address() {
		return operational_address;
	}

	public void setOperational_address(RegisteredAddress operational_address) {
		this.operational_address = operational_address;
	}

	
	
	
}
