package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.SmsRoute;

public class BulkSmsPackage extends GenericCollection {

	private String id;

	private String packageName;

	private Long price;

	private Long smsCredit;

	private Double costPerSms;

	private Boolean discarded = false;

	private SmsRoute smsType;

	private String detail;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Long getSmsCredit() {
		return smsCredit;
	}

	public void setSmsCredit(Long smsCredit) {
		this.smsCredit = smsCredit;
	}

	public Double getCostPerSms() {
		return costPerSms;
	}

	public void setCostPerSms(Double costPerSms) {
		this.costPerSms = costPerSms;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "BulkSmsPackage [id=" + id + ", packageName=" + packageName + ", price=" + price + ", smsCredit="
				+ smsCredit + ", costPerSms=" + costPerSms + "]";
	}

	public SmsRoute getSmsType() {
		return smsType;
	}

	public void setSmsType(SmsRoute smsType) {
		this.smsType = smsType;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

}
