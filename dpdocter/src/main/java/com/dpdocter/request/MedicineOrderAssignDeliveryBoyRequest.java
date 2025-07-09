package com.dpdocter.request;

import com.dpdocter.beans.CollectionBoy;

public class MedicineOrderAssignDeliveryBoyRequest {

	private String orderId;
	private String collectionBoyId;
	private CollectionBoy collectionBoy;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(String collectionBoyId) {
		this.collectionBoyId = collectionBoyId;
	}

	public CollectionBoy getCollectionBoy() {
		return collectionBoy;
	}

	public void setCollectionBoy(CollectionBoy collectionBoy) {
		this.collectionBoy = collectionBoy;
	}

}
