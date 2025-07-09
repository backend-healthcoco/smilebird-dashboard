package com.dpdocter.request;

import com.dpdocter.enums.BussinessType;

public class RazorPayAccountRequest {

	private String name;
	
	private String email;
	
	private Boolean tnc_accepted=true;
	
	private String business_name;
	
	private String business_type="individual";
	
	private String ifsc_code;
	
	private String beneficiary_name;
	
	private String account_number;
	
	private String account_type;
	
	private String doctorId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getTnc_accepted() {
		return tnc_accepted;
	}

	public void setTnc_accepted(Boolean tnc_accepted) {
		this.tnc_accepted = tnc_accepted;
	}

	public String getBusiness_name() {
		return business_name;
	}

	public void setBusiness_name(String business_name) {
		this.business_name = business_name;
	}

	

	
	public String getBusiness_type() {
		return business_type;
	}

	public void setBusiness_type(String business_type) {
		this.business_type = business_type;
	}

	public String getIfsc_code() {
		return ifsc_code;
	}

	public void setIfsc_code(String ifsc_code) {
		this.ifsc_code = ifsc_code;
	}

	public String getBeneficiary_name() {
		return beneficiary_name;
	}

	public void setBeneficiary_name(String beneficiary_name) {
		this.beneficiary_name = beneficiary_name;
	}

	public String getAccount_number() {
		return account_number;
	}

	public void setAccount_number(String account_number) {
		this.account_number = account_number;
	}

	public String getAccount_type() {
		return account_type;
	}

	public void setAccount_type(String account_type) {
		this.account_type = account_type;
	}
	
	

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	@Override
	public String toString() {
		return "RazorPayAccountRequest [name=" + name + ", email=" + email + ", tnc_accepted=" + tnc_accepted
				+ ", business_name=" + business_name + ", business_type=" + business_type + ", ifsc_code=" + ifsc_code
				+ ", beneficiary_name=" + beneficiary_name + ", account_number=" + account_number + ", account_type="
				+ account_type + "]";
	}
	
	
	
	
	
	
}
