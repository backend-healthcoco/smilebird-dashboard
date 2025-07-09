package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;




@Document(collection = "lab_test_customer_cl")
public class LabTestCustomerdetailCollection extends GenericCollection{

	@Id
	private ObjectId id;	
	@Field
	private ObjectId userId;	
	
	@Field
	private ObjectId planId;	
	
//	@Field
//	private List<LabTestCustomerDetail> customer;
//	@Field
//	private LabTestslot slot;
//	@Field
//	private List<LabTestPackageId> packageName;
	@Field
	private String customer_calling_number;
	@Field
	private String billing_cust_name;
	@Field
	private String gender;
	@Field
	private String mobile;
	@Field
	private String state;
	@Field
	private String city;
	@Field
	private String sub_locality;
	@Field
	private String latitude;
	@Field
	private String longitude;
	@Field
	private String address;
	@Field
	private String zipcode;
	@Field
	private String landmark;
	@Field
	private String altmobile;
	@Field
	private String altemail;
	@Field
	private int hard_copy= 0;
	@Field
	private String vendor_billing_user_id;
	@Field
	private String vendor_booking_id;
	@Field
	private String payment_option;
	@Field
	private int discounted_price;	
	@Field
	private String booking_id;
	@Field
	private Object report_url;
	@Field
	private Date appointment_date;
	@Field
	private String appointment_to_Time;
	@Field
	private String appointment_from_Time;
//	@Field
//	private LabTestAddOnBookingRequest addOnObject;
	@Field
	private Boolean isCancel = false;
	@Field
	private Boolean isReshedule = false;
//	@Field
//	private LabTestResheduleBookingRequest resheduleBooking;
	@Field 
	private String cancelReason;
	@Field 
	private String transactionStatus;
	@Field
	private String labPartner ;
	
	@Field
	private String orderId;
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public ObjectId getUserId() {
		return userId;
	}
	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}
	public ObjectId getPlanId() {
		return planId;
	}
	public void setPlanId(ObjectId planId) {
		this.planId = planId;
	}
	public String getCustomer_calling_number() {
		return customer_calling_number;
	}
	public void setCustomer_calling_number(String customer_calling_number) {
		this.customer_calling_number = customer_calling_number;
	}
	public String getBilling_cust_name() {
		return billing_cust_name;
	}
	public void setBilling_cust_name(String billing_cust_name) {
		this.billing_cust_name = billing_cust_name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getSub_locality() {
		return sub_locality;
	}
	public void setSub_locality(String sub_locality) {
		this.sub_locality = sub_locality;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getLandmark() {
		return landmark;
	}
	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}
	public String getAltmobile() {
		return altmobile;
	}
	public void setAltmobile(String altmobile) {
		this.altmobile = altmobile;
	}
	public String getAltemail() {
		return altemail;
	}
	public void setAltemail(String altemail) {
		this.altemail = altemail;
	}
	public int getHard_copy() {
		return hard_copy;
	}
	public void setHard_copy(int hard_copy) {
		this.hard_copy = hard_copy;
	}
	public String getVendor_billing_user_id() {
		return vendor_billing_user_id;
	}
	public void setVendor_billing_user_id(String vendor_billing_user_id) {
		this.vendor_billing_user_id = vendor_billing_user_id;
	}
	public String getVendor_booking_id() {
		return vendor_booking_id;
	}
	public void setVendor_booking_id(String vendor_booking_id) {
		this.vendor_booking_id = vendor_booking_id;
	}
	public String getPayment_option() {
		return payment_option;
	}
	public void setPayment_option(String payment_option) {
		this.payment_option = payment_option;
	}
	public int getDiscounted_price() {
		return discounted_price;
	}
	public void setDiscounted_price(int discounted_price) {
		this.discounted_price = discounted_price;
	}
	public String getBooking_id() {
		return booking_id;
	}
	public void setBooking_id(String booking_id) {
		this.booking_id = booking_id;
	}
	public Object getReport_url() {
		return report_url;
	}
	public void setReport_url(Object report_url) {
		this.report_url = report_url;
	}
	public Date getAppointment_date() {
		return appointment_date;
	}
	public void setAppointment_date(Date appointment_date) {
		this.appointment_date = appointment_date;
	}
	public String getAppointment_to_Time() {
		return appointment_to_Time;
	}
	public void setAppointment_to_Time(String appointment_to_Time) {
		this.appointment_to_Time = appointment_to_Time;
	}
	public String getAppointment_from_Time() {
		return appointment_from_Time;
	}
	public void setAppointment_from_Time(String appointment_from_Time) {
		this.appointment_from_Time = appointment_from_Time;
	}
	public Boolean getIsCancel() {
		return isCancel;
	}
	public void setIsCancel(Boolean isCancel) {
		this.isCancel = isCancel;
	}
	public Boolean getIsReshedule() {
		return isReshedule;
	}
	public void setIsReshedule(Boolean isReshedule) {
		this.isReshedule = isReshedule;
	}
	public String getCancelReason() {
		return cancelReason;
	}
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
	public String getTransactionStatus() {
		return transactionStatus;
	}
	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	public String getLabPartner() {
		return labPartner;
	}
	public void setLabPartner(String labPartner) {
		this.labPartner = labPartner;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	
}
