package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.TransactionEnum;

@Document(collection = "collection_boy_caution_money_history_cl")
public class CollectionBoyCautionMoneyHistoryCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId collectionBoyId;
	@Field
	private Long amount;
	@Field
	private TransactionEnum transactionType = TransactionEnum.DEBIT;
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

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public TransactionEnum getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionEnum transactionType) {
		this.transactionType = transactionType;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
