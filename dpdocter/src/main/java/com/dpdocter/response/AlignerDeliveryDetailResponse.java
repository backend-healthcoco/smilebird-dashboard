package com.dpdocter.response;

import com.dpdocter.beans.Address;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.DeliveryStatus;

public class AlignerDeliveryDetailResponse extends GenericCollection {
	private String id;

	private String planId;

	private String userId;

	private String orderId;

	private String dispatchDate;

	private String note;

	private String trackingLink;

	private Address shippingAddress;

	private DeliveryStatus status;

	private Integer qty;

	private Integer noOfAligners;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
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
