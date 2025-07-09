package com.dpdocter.collections;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.MakeoverImages;
import com.dpdocter.beans.PlanDuration;
import com.dpdocter.beans.PlanPriceDescription;
import com.dpdocter.enums.PaymentMode;
import com.dpdocter.enums.PlanStatus;

@Document(collection = "aligner_user_plan_cl")
public class AlignerUserPlanCollection extends GenericCollection{
	@Id
	private ObjectId id;
	@Field
	private ObjectId userId;
	@Field
	private ObjectId planId;
	@Field
	private String orderId;
	@Field
	private Double discount = 0.0;
	@Field
	private Double amount = 0.0;
	@Field
	private String title;
	@Field
	private Integer noOfAligners;
	@Field
	private PlanDuration duration;
	@Field
	private Date fromDate = new Date();
	@Field
	private Date toDate;
	@Field
	private Boolean discarded = false;
	@Field
	private Boolean isExpired = false;
	@Field
	private Boolean isIPRRequird = false;
	@Field 
	private Boolean isAttachment = false;
	@Field
	private String transactionId;
	@Field
	private PaymentMode mode = PaymentMode.ONLINE;
	@Field
	private PlanStatus planStatus;
	@Field
	private String makeoverVideothumbnailUrl;
	@Field
	private Map<String, List<MakeoverImages>> makeoverImages;
	@Field
	private Map<String, List<String>> makeoverVisuals;
	@Field
	private String iPROnAligner;
	@Field 
	private String attachmentOnAligner;
	@Field
	private Map<String, String> iPRROnTeeth; //teeth no,size of ipr
	@Field 
	private String attachmentTeeth;
	
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

	public ObjectId getPlanId() {
		return planId;
	}

	public void setPlanId(ObjectId planId) {
		this.planId = planId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Boolean getIsExpired() {
		return isExpired;
	}

	public void setIsExpired(Boolean isExpired) {
		this.isExpired = isExpired;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public PaymentMode getMode() {
		return mode;
	}

	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}

	public PlanStatus getPlanStatus() {
		return planStatus;
	}

	public void setPlanStatus(PlanStatus planStatus) {
		this.planStatus = planStatus;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getNoOfAligners() {
		return noOfAligners;
	}

	public void setNoOfAligners(Integer noOfAligners) {
		this.noOfAligners = noOfAligners;
	}

	public PlanDuration getDuration() {
		return duration;
	}

	public void setDuration(PlanDuration duration) {
		this.duration = duration;
	}

	public String getMakeoverVideothumbnailUrl() {
		return makeoverVideothumbnailUrl;
	}

	public void setMakeoverVideothumbnailUrl(String makeoverVideothumbnailUrl) {
		this.makeoverVideothumbnailUrl = makeoverVideothumbnailUrl;
	}

	public Map<String, List<MakeoverImages>> getMakeoverImages() {
		return makeoverImages;
	}

	public void setMakeoverImages(Map<String, List<MakeoverImages>> makeoverImages) {
		this.makeoverImages = makeoverImages;
	}

	public Map<String, List<String>> getMakeoverVisuals() {
		return makeoverVisuals;
	}

	public void setMakeoverVisuals(Map<String, List<String>> makeoverVisuals) {
		this.makeoverVisuals = makeoverVisuals;
	}

	public Boolean getIsIPRRequird() {
		return isIPRRequird;
	}

	public void setIsIPRRequird(Boolean isIPRRequird) {
		this.isIPRRequird = isIPRRequird;
	}

	public Boolean getIsAttachment() {
		return isAttachment;
	}

	public void setIsAttachment(Boolean isAttachment) {
		this.isAttachment = isAttachment;
	}

	public String getiPROnAligner() {
		return iPROnAligner;
	}

	public void setiPROnAligner(String iPROnAligner) {
		this.iPROnAligner = iPROnAligner;
	}

	public String getAttachmentOnAligner() {
		return attachmentOnAligner;
	}

	public void setAttachmentOnAligner(String attachmentOnAligner) {
		this.attachmentOnAligner = attachmentOnAligner;
	}

	public Map<String, String> getiPRROnTeeth() {
		return iPRROnTeeth;
	}

	public void setiPRROnTeeth(Map<String, String> iPRROnTeeth) {
		this.iPRROnTeeth = iPRROnTeeth;
	}

	public String getAttachmentTeeth() {
		return attachmentTeeth;
	}

	public void setAttachmentTeeth(String attachmentTeeth) {
		this.attachmentTeeth = attachmentTeeth;
	}


}
