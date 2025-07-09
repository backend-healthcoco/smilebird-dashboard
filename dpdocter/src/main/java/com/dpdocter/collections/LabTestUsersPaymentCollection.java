package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Discount;
import com.dpdocter.enums.PaymentMode;

@Document(collection="lab_test_payment_cl")

public class LabTestUsersPaymentCollection extends GenericCollection{

	@Id
	private ObjectId id;

	@Field
	private ObjectId userId;

	@Field
	private ObjectId labTestAppointmentId;

	@Field
	private String transactionId;
	
	@Field
	private ObjectId planId;	
	
	@Field
	private String razorPayAccountId;
	
	@Field
	private String paymentId;

	@Field
	private String transactionStatus;
	@Field
	private Discount discount;
	@Field
	private Double amount = 0.0;

	@Field
	private Double discountAmount = 0.0;

	@Field
	private Double transferAmount = 0.0;
	@Field
	private PaymentMode mode = PaymentMode.ONLINE;
	
	@Field
	private String orderId;
	@Field
	private String reciept;
		
	@Field
	private Date appointmentDate;
	
	@Field
	private String partnerName;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public ObjectId getLabTestAppointmentId() {
		return labTestAppointmentId;
	}

	public void setLabTestAppointmentId(ObjectId labTestAppointmentId) {
		this.labTestAppointmentId = labTestAppointmentId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public ObjectId getPlanId() {
		return planId;
	}

	public void setPlanId(ObjectId planId) {
		this.planId = planId;
	}

	public String getRazorPayAccountId() {
		return razorPayAccountId;
	}

	public void setRazorPayAccountId(String razorPayAccountId) {
		this.razorPayAccountId = razorPayAccountId;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
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

	public Double getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(Double transferAmount) {
		this.transferAmount = transferAmount;
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

	public Date getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}
	
	

}
