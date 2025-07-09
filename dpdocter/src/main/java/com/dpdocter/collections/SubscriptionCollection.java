package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.PaymentMode;

@Document(collection = "subscription_cl")
public class SubscriptionCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private ObjectId doctorId;

	@Field
	private Date fromDate;

	@Field
	private Date toDate;

	@Field
	private PackageType packageName;

	@Field
	private int amount = 0;

	@Field
	private int discountAmount = 0;

	@Field
	private String accountNo;

	@Field
	private PaymentMode mode;

	@Field
	private String bankName;

	@Field
	private String countryCode;

	@Field
	private String chequeNo;

	@Field
	private String branch;

	@Field
	private Date chequeDate;

	@Field
	private String emailAddress;

	@Field
	private String mobileNumber;

	@Field
	private Boolean isAdvertisement = Boolean.FALSE;

	@Field
	private Boolean isDiscarded = Boolean.FALSE;

	@Field
	private Boolean paymentStatus = Boolean.FALSE;
	
	@Field
	private String transactionStatus;

//	@Field
//	private Boolean isExtended = Boolean.FALSE;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
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

	public PackageType getPackageName() {
		return packageName;
	}

	public void setPackageName(PackageType packageName) {
		this.packageName = packageName;
	}

	public Boolean getIsAdvertisement() {
		return isAdvertisement;
	}

	public void setIsAdvertisement(Boolean isAdvertisement) {
		this.isAdvertisement = isAdvertisement;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public int getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(int discountAmount) {
		this.discountAmount = discountAmount;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public PaymentMode getMode() {
		return mode;
	}

	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
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

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Boolean getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(Boolean paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	@Override
	public String toString() {
		return "SubscriptionCollection [id=" + id + ", doctorId=" + doctorId + ", fromDate=" + fromDate + ", toDate="
				+ toDate + ", packageName=" + packageName + ", amount=" + amount + ", discountAmount=" + discountAmount
				+ ", accountNo=" + accountNo + ", mode=" + mode + ", bankName=" + bankName + ", countryCode="
				+ countryCode + ", chequeNo=" + chequeNo + ", branch=" + branch + ", chequeDate=" + chequeDate
				+ ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber + ", isAdvertisement="
				+ isAdvertisement + ", isDiscarded=" + isDiscarded + ", paymentStatus=" + paymentStatus + "]";
	}

}
