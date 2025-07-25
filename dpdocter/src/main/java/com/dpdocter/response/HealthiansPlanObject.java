package com.dpdocter.response;

public class HealthiansPlanObject {

	private String pid;
	 private String id;
	 private String name;
	 private String package_code;
	 private String mrp;
	 private String price;
	 private String cost_id;
	 private String channel_type;
	 private String b2b_channel_partner_id;
	 
	 private String labPartner="HEALTHIANS";
	 private String hcAmount;
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackage_code() {
		return package_code;
	}
	public void setPackage_code(String package_code) {
		this.package_code = package_code;
	}
	public String getMrp() {
		return mrp;
	}
	public void setMrp(String mrp) {
		this.mrp = mrp;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getCost_id() {
		return cost_id;
	}
	public void setCost_id(String cost_id) {
		this.cost_id = cost_id;
	}
	public String getChannel_type() {
		return channel_type;
	}
	public void setChannel_type(String channel_type) {
		this.channel_type = channel_type;
	}
	public String getB2b_channel_partner_id() {
		return b2b_channel_partner_id;
	}
	public void setB2b_channel_partner_id(String b2b_channel_partner_id) {
		this.b2b_channel_partner_id = b2b_channel_partner_id;
	}
	public String getLabPartner() {
		return labPartner;
	}
	public void setLabPartner(String labPartner) {
		this.labPartner = labPartner;
	}
	public String getHcAmount() {
		return hcAmount;
	}
	public void setHcAmount(String hcAmount) {
		this.hcAmount = hcAmount;
	}
	
	
	 
}
