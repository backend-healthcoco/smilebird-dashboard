package com.dpdocter.beans;

import java.util.List;

import org.json.JSONObject;

public class AppointmentPaymentTransferResponse {

	private String id;
	
	private String entity;
	
	private String source;
	
	private String recipient;
	
	private Integer amount;
	
	private String currency;
	
	private Integer amount_reversed;
	
	private Integer fees;
	
	private Integer tax;
	
	private Boolean on_hold;
	
	private String on_hold_until;
	
	private String recipient_settlement_id;
	
	private Long created_at;
	
	private List<JSONObject> linked_account_notes;
	
	private List<JSONObject> notes;
	
	private Long processed_at;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}



	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getAmount_reversed() {
		return amount_reversed;
	}

	public void setAmount_reversed(Integer amount_reversed) {
		this.amount_reversed = amount_reversed;
	}

	public Integer getFees() {
		return fees;
	}

	public void setFees(Integer fees) {
		this.fees = fees;
	}

	public Integer getTax() {
		return tax;
	}

	public void setTax(Integer tax) {
		this.tax = tax;
	}

	public Boolean getOn_hold() {
		return on_hold;
	}

	public void setOn_hold(Boolean on_hold) {
		this.on_hold = on_hold;
	}

	public String getOn_hold_until() {
		return on_hold_until;
	}

	public void setOn_hold_until(String on_hold_until) {
		this.on_hold_until = on_hold_until;
	}

	public String getRecipient_settlement_id() {
		return recipient_settlement_id;
	}

	public void setRecipient_settlement_id(String recipient_settlement_id) {
		this.recipient_settlement_id = recipient_settlement_id;
	}

	public Long getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Long created_at) {
		this.created_at = created_at;
	}

	

	

	public List<JSONObject> getLinked_account_notes() {
		return linked_account_notes;
	}

	public void setLinked_account_notes(List<JSONObject> linked_account_notes) {
		this.linked_account_notes = linked_account_notes;
	}

	public List<JSONObject> getNotes() {
		return notes;
	}

	public void setNotes(List<JSONObject> notes) {
		this.notes = notes;
	}

	public Long getProcessed_at() {
		return processed_at;
	}

	public void setProcessed_at(Long processed_at) {
		this.processed_at = processed_at;
	}
	
	
	
}
