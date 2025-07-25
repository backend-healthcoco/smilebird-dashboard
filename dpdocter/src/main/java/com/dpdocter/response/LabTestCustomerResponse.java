package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;


public class LabTestCustomerResponse extends GenericCollection{
	
	private String id;	
	
	private String userName;
	
	private String testName;
	
	private String relation;
	 		 	 
	private String mobile;
	 	 
	private String booking_id;
	 
	private Object report_url;
	 
	private Date appointment_date;
	
	private String payment_option;
	 
	private String appointment_to_Time;
	 
	private String appointment_from_Time;
	 	 
	private Boolean isCancel = false;
	 
	private Boolean isReshedule = false;
	 	  
	private String cancelReason;
	  
	private String transactionStatus;

	private String paymentId;		
		
	private String payOrderId;
	
	private String labPartner ;
	
	private String ref_orderId;
	
	private String address;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getBooking_id() {
		return booking_id;
	}

	public void setBooking_id(String booking_id) {
		this.booking_id = booking_id;
	}

	public Object getReport_url() {
		return report_url;
	}

	public void setReport_url(Object report_url) {
		this.report_url = report_url;
	}

	public Date getAppointment_date() {
		return appointment_date;
	}

	public void setAppointment_date(Date appointment_date) {
		this.appointment_date = appointment_date;
	}

	public String getAppointment_to_Time() {
		return appointment_to_Time;
	}

	public void setAppointment_to_Time(String appointment_to_Time) {
		this.appointment_to_Time = appointment_to_Time;
	}

	public String getAppointment_from_Time() {
		return appointment_from_Time;
	}

	public void setAppointment_from_Time(String appointment_from_Time) {
		this.appointment_from_Time = appointment_from_Time;
	}

	public Boolean getIsCancel() {
		return isCancel;
	}

	public void setIsCancel(Boolean isCancel) {
		this.isCancel = isCancel;
	}

	public Boolean getIsReshedule() {
		return isReshedule;
	}

	public void setIsReshedule(Boolean isReshedule) {
		this.isReshedule = isReshedule;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getPayment_option() {
		return payment_option;
	}

	public void setPayment_option(String payment_option) {
		this.payment_option = payment_option;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	
	public String getPayOrderId() {
		return payOrderId;
	}

	public void setPayOrderId(String payOrderId) {
		this.payOrderId = payOrderId;
	}

	public String getLabPartner() {
		return labPartner;
	}

	public void setLabPartner(String labPartner) {
		this.labPartner = labPartner;
	}

	public String getRef_orderId() {
		return ref_orderId;
	}

	public void setRef_orderId(String ref_orderId) {
		this.ref_orderId = ref_orderId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	
	
	
	

}
