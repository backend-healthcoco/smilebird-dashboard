package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.TransactionEnum;

public class CollectionBoyCautionMoneyHistory extends GenericCollection {

	private String id;
	private String collectionBoyId;
	private Long amount;
	private TransactionEnum transactionType = TransactionEnum.DEBIT;
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
