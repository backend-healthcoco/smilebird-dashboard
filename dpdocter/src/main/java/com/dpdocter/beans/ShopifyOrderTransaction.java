package com.dpdocter.beans;

import org.json.JSONObject;

import com.dpdocter.collections.GenericCollection;

public class ShopifyOrderTransaction extends GenericCollection{

	private String id;
	
	private String shopifyId;
	
	private String order_id;
	
	private String kind;
	
	private String gateway;
	
	private String status;
	
	private String message;
	
	private String created_at;
	
	private String test;
	
	private String authorization;
	
	private String location_id;
	
	private String user_id;
	
	private String parent_id;
	
	private String processed_at;
	
	private String device_id;
	
	private String error_code;
	
	private String source_name;
	
	private ShopifyPaymentDetail payment_details;
	
	private JSONObject receipt;
	
	private String amount;
	
	private String currency;
	
	private String admin_graphql_api_id;

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getAuthorization() {
		return authorization;
	}

	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	public String getLocation_id() {
		return location_id;
	}

	public void setLocation_id(String location_id) {
		this.location_id = location_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getProcessed_at() {
		return processed_at;
	}

	public void setProcessed_at(String processed_at) {
		this.processed_at = processed_at;
	}

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

	public String getSource_name() {
		return source_name;
	}

	public void setSource_name(String source_name) {
		this.source_name = source_name;
	}

	public ShopifyPaymentDetail getPayment_details() {
		return payment_details;
	}

	public void setPayment_details(ShopifyPaymentDetail payment_details) {
		this.payment_details = payment_details;
	}

	public JSONObject getReceipt() {
		return receipt;
	}

	public void setReceipt(JSONObject receipt) {
		this.receipt = receipt;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAdmin_graphql_api_id() {
		return admin_graphql_api_id;
	}

	public void setAdmin_graphql_api_id(String admin_graphql_api_id) {
		this.admin_graphql_api_id = admin_graphql_api_id;
	}
	
	

	public String getShopifyId() {
		return shopifyId;
	}

	public void setShopifyId(String shopifyId) {
		this.shopifyId = shopifyId;
	}

	@Override
	public String toString() {
		return "ShopifyOrderTransaction [order_id=" + order_id + ", id=" + id + ", kind=" + kind + ", gateway="
				+ gateway + ", status=" + status + ", message=" + message + ", created_at=" + created_at + ", test="
				+ test + ", authorization=" + authorization + ", location_id=" + location_id + ", user_id=" + user_id
				+ ", parent_id=" + parent_id + ", processed_at=" + processed_at + ", device_id=" + device_id
				+ ", error_code=" + error_code + ", source_name=" + source_name + ", payment_details=" + payment_details
				+ ", receipt=" + receipt + ", amount=" + amount + ", currency=" + currency + ", admin_graphql_api_id="
				+ admin_graphql_api_id + "]";
	}
	
	
}
