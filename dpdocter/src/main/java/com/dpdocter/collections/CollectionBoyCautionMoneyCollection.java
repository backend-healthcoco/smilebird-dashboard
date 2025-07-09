package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "collection_boy_caution_money_cl")
public class CollectionBoyCautionMoneyCollection extends GenericCollection{
	@Id
	private ObjectId id;
	@Field
	private ObjectId collectionBoyId;
	@Field
	private Double cautionMoney;
	@Field
	private Double remainingMoney;
	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(ObjectId collectionBoyId) {
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
