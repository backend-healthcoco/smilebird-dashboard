package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class CollectionBoyCautionMoney extends GenericCollection {

	private String id;
	private String collectionBoyId;
	private Double cautionMoney;
	private Double remainingMoney;
	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(String collectionBoyId) {
		this.collectionBoyId = collectionBoyId;
	}

	public Double getCautionMoney() {
		return cautionMoney;
	}

	public void setCautionMoney(Double cautionMoney) {
		this.cautionMoney = cautionMoney;
	}

	public Double getRemainingMoney() {
		return remainingMoney;
	}

	public void setRemainingMoney(Double remainingMoney) {
		this.remainingMoney = remainingMoney;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
