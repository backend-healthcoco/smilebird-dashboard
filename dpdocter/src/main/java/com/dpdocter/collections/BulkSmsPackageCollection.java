package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.SmsRoute;

@Document(collection = "bulk_sms_package_cl")
public class BulkSmsPackageCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String packageName;
	@Field
	private Long price;
	@Field
	private Long smsCredit;
	@Field
	private Double costPerSms;

	@Field
	private SmsRoute smsType;

	@Field
	private String detail;
	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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
