package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.PaymentMode;

public class AppointmentPaymentInfo extends GenericCollection {

private String id;
	
	
	
	private String patientId;
	
	private String reciept;

	
	private String appointmentId;
	
	

	private String transactionStatus;	
	
	
	
	private PaymentMode mode;
	
	private String razorPayAccountId;
	
	private String city;
	
	private String speciality;
	
	private String specialityId;
	
	private String patientName;
	
	private String patientMobileNumber;
	
	private DoctorConsultation consultationType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getReciept() {
		return reciept;
	}

	public void setReciept(String reciept) {
		this.reciept = reciept;
	}

	

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	
	public PaymentMode getMode() {
		return mode;
	}

	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}

	public String getRazorPayAccountId() {
		return razorPayAccountId;
	}

	public void setRazorPayAccountId(String razorPayAccountId) {
		this.razorPayAccountId = razorPayAccountId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public String getSpecialityId() {
		return specialityId;
	}

	public void setSpecialityId(String specialityId) {
		this.specialityId = specialityId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientMobileNumber() {
		return patientMobileNumber;
	}

	public void setPatientMobileNumber(String patientMobileNumber) {
		this.patientMobileNumber = patientMobileNumber;
	}

	public DoctorConsultation getConsultationType() {
		return consultationType;
	}

	public void setConsultationType(DoctorConsultation consultationType) {
		this.consultationType = consultationType;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	
	
	

}
