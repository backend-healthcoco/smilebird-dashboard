package com.dpdocter.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ShopifyCustomer {

	
	private String id;

	private long shopifyId;
	
	private String email;
	
	private Boolean accepts_marketing;
	
	private String created_at;
	
	private String updated_at;
	
	private String first_name;
	
	private String last_name;
	
	private Integer orders_count;
	
	private String state;
	
	private String total_spent;
	
	private String last_order_id;
	
	private String note;
	
	private String verified_email;
	
	private String multipass_identifier;
	
	private String tax_exempt;
	
	private String phone;
	
	private String tags;
	
	private String last_order_name;
	
	private String currency;
	
	private List<String>addresses;
	
	private String accepts_marketing_updated_at;
	
	private String marketing_opt_in_level;
	
	private String admin_graphql_api_id;

	

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getAccepts_marketing() {
		return accepts_marketing;
	}

	public void setAccepts_marketing(Boolean accepts_marketing) {
		this.accepts_marketing = accepts_marketing;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public Integer getOrders_count() {
		return orders_count;
	}

	public void setOrders_count(Integer orders_count) {
		this.orders_count = orders_count;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTotal_spent() {
		return total_spent;
	}

	public void setTotal_spent(String total_spent) {
		this.total_spent = total_spent;
	}

	public String getLast_order_id() {
		return last_order_id;
	}

	public void setLast_order_id(String last_order_id) {
		this.last_order_id = last_order_id;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getVerified_email() {
		return verified_email;
	}

	public void setVerified_email(String verified_email) {
		this.verified_email = verified_email;
	}

	public String getMultipass_identifier() {
		return multipass_identifier;
	}

	public void setMultipass_identifier(String multipass_identifier) {
		this.multipass_identifier = multipass_identifier;
	}

	public String getTax_exempt() {
		return tax_exempt;
	}

	public void setTax_exempt(String tax_exempt) {
		this.tax_exempt = tax_exempt;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getLast_order_name() {
		return last_order_name;
	}

	public void setLast_order_name(String last_order_name) {
		this.last_order_name = last_order_name;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public List<String> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
	}

	public String getAccepts_marketing_updated_at() {
		return accepts_marketing_updated_at;
	}

	public void setAccepts_marketing_updated_at(String accepts_marketing_updated_at) {
		this.accepts_marketing_updated_at = accepts_marketing_updated_at;
	}

	public String getMarketing_opt_in_level() {
		return marketing_opt_in_level;
	}

	public void setMarketing_opt_in_level(String marketing_opt_in_level) {
		this.marketing_opt_in_level = marketing_opt_in_level;
	}

	public String getAdmin_graphql_api_id() {
		return admin_graphql_api_id;
	}

	public void setAdmin_graphql_api_id(String admin_graphql_api_id) {
		this.admin_graphql_api_id = admin_graphql_api_id;
	}

	public long getShopifyId() {
		return shopifyId;
	}

	public void setShopifyId(long shopifyId) {
		this.shopifyId = shopifyId;
	}

	
	
	
}
