package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;
import com.dpdocter.enums.DeliveryStatus;

@Document(collection = "aligner_delivery_detail_cl")
public class AlignerDeliveryDetailCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId planId;

	@Field
	private ObjectId userId;

	@Field
	private String orderId;

	@Field
	private String dispatchDate;

	@Field
	private String note;

	@Field
	private String trackingLink;

	@Field
	private Address shippingAddress;

	@Field
	private DeliveryStatus status;

	@Field
	private Integer qty;

	@Field
	private Integer noOfAligners;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getPlanId() {
		return planId;
	}

	public void setPlanId(ObjectId planId) {
		this.planId = planId;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getDispatchDate() {
		return dispatchDate;
	}

	public void setDispatchDate(String dispatchDate) {
		this.dispatchDate = dispatchDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getTrackingLink() {
		return trackingLink;
	}

	public void setTrackingLink(String trackingLink) {
		this.trackingLink = trackingLink;
	}

	public Address getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public DeliveryStatus getStatus() {
		return status;
	}

	public void setStatus(DeliveryStatus status) {
		this.status = status;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public Integer getNoOfAligners() {
		return noOfAligners;
	}

	public void setNoOfAligners(Integer noOfAligners) {
		this.noOfAligners = noOfAligners;
	}

}
