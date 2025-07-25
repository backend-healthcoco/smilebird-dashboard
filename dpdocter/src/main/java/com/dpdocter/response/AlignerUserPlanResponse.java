package com.dpdocter.response;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.MakeoverImages;
import com.dpdocter.beans.User;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.PaymentMode;
import com.dpdocter.enums.PlanStatus;

public class AlignerUserPlanResponse extends GenericCollection {
	private String id;
	private String userId;
	private String planId;
	private String orderId;
	private Double discount = 0.0;
	private Double amount = 0.0;
	private Double discountAmount = 0.0;
	private Date fromDate = new Date();
	private Date toDate;
	private Boolean discarded = false;
	private Boolean isExpired = false;
	private Boolean isIPRRequird = false;
	private Boolean isAttachment = false;

	private Double discountedAmount = 0.0;
	private String transactionId;
	private PaymentMode mode = PaymentMode.ONLINE;
	private PlanStatus planStatus;
	private User user;
	private String makeoverVideothumbnailUrl;
	private Map<String, List<MakeoverImages>> makeoverImages;
	private Map<String, List<String>> makeoverVisuals;
	private String attachmentOnAligner;
	private String iPROnAligner;
	private Map<String, String> iPRROnTeeth; //teeth no,size of ipr
	private String attachmentTeeth;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
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

	public Double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(Double discountAmount) {
		this.discountAmount = discountAmount;
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

	public Double getDiscountedAmount() {
		return discountedAmount;
	}

	public void setDiscountedAmount(Double discountedAmount) {
		this.discountedAmount = discountedAmount;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
