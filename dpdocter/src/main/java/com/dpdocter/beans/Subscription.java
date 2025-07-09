package com.dpdocter.beans;

import java.util.Date;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.PaymentMode;

public class Subscription extends GenericCollection {

	private String id;

	private String doctorId;

	private Date fromDate;

	private Date toDate;

	private PackageType packageName;

	private int amount = 0;

	private int discountAmount = 0;

	private String accountNo;

	private PaymentMode mode;

	private String bankName;

	private String countryCode;

	private String chequeNo;

	private String branch;

	private Date chequeDate;

	private Boolean isAdvertisement = Boolean.FALSE;

	private Boolean isDiscarded = Boolean.FALSE;

	private Boolean paymentStatus = Boolean.FALSE;

	private String transactionStatus;

//	private Boolean isExtended = Boolean.FALSE;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
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

	public int getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(int discountAmount) {
		this.discountAmount = discountAmount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public Boolean getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(Boolean paymentStatus) {
		this.paymentStatus = paymentStatus;
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

	public Boolean getIsAdvertisement() {
		return isAdvertisement;
	}

	public void setIsAdvertisement(Boolean isAdvertisement) {
		this.isAdvertisement = isAdvertisement;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}
	public int getAmount() {
		return amount;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	@Override
	public String toString() {
		return "Subscription [id=" + id + ", doctorId=" + doctorId + ", fromDate=" + fromDate + ", toDate=" + toDate
				+ ", packageName=" + packageName + ", amount=" + amount + ", discountAmount=" + discountAmount
				+ ", accountNo=" + accountNo + ", mode=" + mode + ", bankName=" + bankName + ", countryCode="
				+ countryCode + ", chequeNo=" + chequeNo + ", branch=" + branch + ", chequeDate=" + chequeDate
				+ ", isAdvertisement=" + isAdvertisement + ", isDiscarded=" + isDiscarded + ", paymentStatus="
				+ paymentStatus + "]";
	}

}
