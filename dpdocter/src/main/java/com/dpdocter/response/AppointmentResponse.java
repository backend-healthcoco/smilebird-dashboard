package com.dpdocter.response;

import java.util.Date;

import com.dpdocter.beans.Discount;
import com.dpdocter.beans.DoctorConsultation;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.PaymentMode;

public class AppointmentResponse extends GenericCollection{

	private String id;


	private String problemDetailsId;

	private String transactionId;

	private String transactionStatus;

	private String speciality;	
	

	private String appointmentId;
	

	private Double amount = 0.0;

	private Double discountAmount = 0.0;

	private String specialityId;

	private PaymentMode mode = PaymentMode.ONLINE;

	//private String chequeNo;

	private String orderId;

	private String reciept;
	
	private DoctorConsultation consultationType;
	
	private Boolean isAnonymousAppointment;
	
	private String localPatientName;
	
	private String patientId;
	
	private AppointmentState state;
	
	private AppointmentType type;
	
	private WorkingHours time;
	
	private Date fromDate;

	private Date toDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public String getProblemDetailsId() {
		return problemDetailsId;
	}

	public void setProblemDetailsId(String problemDetailsId) {
		this.problemDetailsId = problemDetailsId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(Double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public PaymentMode getMode() {
		return mode;
	}

	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getReciept() {
		return reciept;
	}

	public void setReciept(String reciept) {
		this.reciept = reciept;
	}

	public DoctorConsultation getConsultationType() {
		return consultationType;
	}

	public void setConsultationType(DoctorConsultation consultationType) {
		this.consultationType = consultationType;
	}

	public Boolean getIsAnonymousAppointment() {
		return isAnonymousAppointment;
	}

	public void setIsAnonymousAppointment(Boolean isAnonymousAppointment) {
		this.isAnonymousAppointment = isAnonymousAppointment;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public AppointmentState getState() {
		return state;
	}

	public void setState(AppointmentState state) {
		this.state = state;
	}

	public AppointmentType getType() {
		return type;
	}

	public void setType(AppointmentType type) {
		this.type = type;
	}

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public String getSpecialityId() {
		return specialityId;
	}

	public void setSpecialityId(String specialityId) {
		this.specialityId = specialityId;
	}
	
	
	
}
