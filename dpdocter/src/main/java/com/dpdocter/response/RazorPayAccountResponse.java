package com.dpdocter.response;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class RazorPayAccountResponse  extends GenericCollection{

	private String id;
	
	private String entity;
	
	private Boolean live;
	
	private String name;
	
	private String email;
	
	private Boolean managed;
	
	private Boolean tnc_accepted=true;
	
	private ActivationDetails  activation_details;
	
	private SecondaryEmail secondary_emails;
	
	
	private AccountDetails account_details;
	
	private List<String>notes;
	
	private FundTransfer fund_transfer;
	
	private Boolean dashboard_access;
	
	private Boolean allow_reversals;
	
	
	
	
	
	
	
	
	

	
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getLive() {
		return live;
	}

	public void setLive(Boolean live) {
		this.live = live;
	}

	public Boolean getManaged() {
		return managed;
	}

	public void setManaged(Boolean managed) {
		this.managed = managed;
	}

		
	

	public ActivationDetails getActivation_details() {
		return activation_details;
	}

	public void setActivation_details(ActivationDetails activation_details) {
		this.activation_details = activation_details;
	}

	public SecondaryEmail getSecondary_emails() {
		return secondary_emails;
	}

	public void setSecondary_emails(SecondaryEmail secondary_emails) {
		this.secondary_emails = secondary_emails;
	}

	public AccountDetails getAccount_details() {
		return account_details;
	}

	public void setAccount_details(AccountDetails account_details) {
		this.account_details = account_details;
	}

	public List<String> getNotes() {
		return notes;
	}

	public void setNotes(List<String> notes) {
		this.notes = notes;
	}

	public FundTransfer getFund_transfer() {
		return fund_transfer;
	}

	public void setFund_transfer(FundTransfer fund_transfer) {
		this.fund_transfer = fund_transfer;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	

	public Boolean getTnc_accepted() {
		return tnc_accepted;
	}

	public void setTnc_accepted(Boolean tnc_accepted) {
		this.tnc_accepted = tnc_accepted;
	}
	
	

	public Boolean getDashboard_access() {
		return dashboard_access;
	}

	public void setDashboard_access(Boolean dashboard_access) {
		this.dashboard_access = dashboard_access;
	}

	public Boolean getAllow_reversals() {
		return allow_reversals;
	}

	public void setAllow_reversals(Boolean allow_reversals) {
		this.allow_reversals = allow_reversals;
	}

	@Override
	public String toString() {
		return "RazorPayAccountResponse [id=" + id + ", entity=" + entity + ", live=" + live + ", name=" + name
				+ ", email=" + email + ", managed=" + managed + ", tnc_accepted=" + tnc_accepted
				+ ", activation_details=" + activation_details + ", secondary_emails=" + secondary_emails
				+ ", account_details=" + account_details + ", notes=" + notes + ", fund_transfer=" + fund_transfer
				+ "]";
	}

	
	
	
}
