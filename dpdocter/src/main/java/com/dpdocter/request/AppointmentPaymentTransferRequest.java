package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.Notes;

public class AppointmentPaymentTransferRequest {


	private String userId;
	private String doctorId;
	private String appointmentId;
	
	private String account;
	private int amount;
	private String currency = "INR";
	private Notes notes;



	



	public String getUserId() {
		return userId;
	}



	public void setUserId(String userId) {
		this.userId = userId;
	}



	public String getDoctorId() {
		return doctorId;
	}



	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}



	public String getAppointmentId() {
		return appointmentId;
	}



	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}



	public String getAccount() {
		return account;
	}



	public void setAccount(String account) {
		this.account = account;
	}



	public int getAmount() {
		return amount;
	}



	public void setAmount(int amount) {
		this.amount = amount;
	}



	public String getCurrency() {
		return currency;
	}



	public void setCurrency(String currency) {
		this.currency = currency;
	}



	public Notes getNotes() {
		return notes;
	}



	public void setNotes(Notes notes) {
		this.notes = notes;
	}



	
	
	
}
