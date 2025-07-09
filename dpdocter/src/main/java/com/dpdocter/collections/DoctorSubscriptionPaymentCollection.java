package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Discount;
import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.PaymentMode;

@Document(collection = "subscription_payment_cl")
public class DoctorSubscriptionPaymentCollection extends GenericCollection {

	@Field
	private ObjectId id;

	@Field
	private ObjectId subscriptionId;

	@Field
	private ObjectId doctorId;

	@Field
	private String transactionId;

	@Field
	private String transactionStatus;

	@Field
	private Discount discount;

	@Field
	private int amount;

	@Field
	private int discountAmount;

	@Field
	private Double transferAmount = 0.0;

	@Field
	private PaymentMode mode = PaymentMode.ONLINE;

	@Field
	private String chequeNo;

	@Field
	private String accountNo;

	@Field
	private String bankName;

	@Field
	private String branch;

	@Field
	private Date chequeDate;

	@Field
	private PackageType packageName;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(ObjectId subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
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

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(int discountAmount) {
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

	public PackageType getPackageName() {
		return packageName;
	}

	public void setPackageName(PackageType packageName) {
		this.packageName = packageName;
	}

	@Override
	public String toString() {
		return "DoctorSubscriptionPaymentCollection [id=" + id + ", subscriptionId=" + subscriptionId + ", doctorId="
				+ doctorId + ", transactionId=" + transactionId + ", transactionStatus=" + transactionStatus
				+ ", discount=" + discount + ", amount=" + amount + ", discountAmount=" + discountAmount
				+ ", transferAmount=" + transferAmount + ", mode=" + mode + ", chequeNo=" + chequeNo + ", accountNo="
				+ accountNo + ", bankName=" + bankName + ", branch=" + branch + ", chequeDate=" + chequeDate
				+ ", packageName=" + packageName + "]";
	}

}
