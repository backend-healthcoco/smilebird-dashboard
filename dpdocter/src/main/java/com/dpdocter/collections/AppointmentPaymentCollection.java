package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Discount;
import com.dpdocter.beans.DoctorConsultation;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.PaymentMode;
import com.dpdocter.beans.UserResponse;

@Document(collection = "appointment_payment_cl")
public class AppointmentPaymentCollection extends GenericCollection{

	@Id
	private ObjectId id;

	@Field
	private ObjectId userId;

	@Field
	private ObjectId problemDetailsId;
	
	@Field
	private ObjectId specialityId;	
	
	@Field
	private String razorPayAccountId;

	@Field
	private String transactionId;
	
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
	private String chequeNo;
	@Field
	private String orderId;
	@Field
	private String reciept;
	@Field
	private String accountNo;
	@Field
	private String bankName;
	@Field
	private String branch;
	@Field
	private Date chequeDate;
	@Field
	private UserResponse paymentBy;
	@Field
	private DoctorConsultation consultationType;
	
	@Field
	private ObjectId appointmentId;
	
	@Field
	private Boolean isAnonymousAppointment;
	
	@Field
	private String localPatientName;
	
	@Field
	private ObjectId patientId;
	
	@Field
	private AppointmentState state;
	
	@Field
	private AppointmentType type;
	
	@Field
	private WorkingHours time;
	
	@Field
	private Date fromDate;

	@Field
	private Date toDate;
	
	
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
	public ObjectId getProblemDetailsId() {
		return problemDetailsId;
	}
	public void setProblemDetailsId(ObjectId problemDetailsId) {
		this.problemDetailsId = problemDetailsId;
	}
	public ObjectId getSpecialityId() {
		return specialityId;
	}
	public void setSpecialityId(ObjectId specialityId) {
		this.specialityId = specialityId;
	}
	public String getRazorPayAccountId() {
		return razorPayAccountId;
	}
	public void setRazorPayAccountId(String razorPayAccountId) {
		this.razorPayAccountId = razorPayAccountId;
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
	public String getChequeNo() {
		return chequeNo;
	}
	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
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
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public Date getChequeDate() {
		return chequeDate;
	}
	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}
	public UserResponse getPaymentBy() {
		return paymentBy;
	}
	public void setPaymentBy(UserResponse paymentBy) {
		this.paymentBy = paymentBy;
	}
	public DoctorConsultation getConsultationType() {
		return consultationType;
	}
	public void setConsultationType(DoctorConsultation consultationType) {
		this.consultationType = consultationType;
	}
	public String getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	public ObjectId getAppointmentId() {
		return appointmentId;
	}
	public void setAppointmentId(ObjectId appointmentId) {
		this.appointmentId = appointmentId;
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
	public ObjectId getPatientId() {
		return patientId;
	}
	public void setPatientId(ObjectId patientId) {
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
	
	
	
	

}
