package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.MedicineOrderImages;
import com.dpdocter.beans.MedicineOrderItems;
import com.dpdocter.beans.RxNotes;
import com.dpdocter.beans.UserAddress;
import com.dpdocter.beans.Vendor;
import com.dpdocter.enums.DeliveryPreferences;
import com.dpdocter.enums.OrderStatus;
import com.dpdocter.enums.PaymentMode;

@Document(collection = "medicine_order_cl")
public class MedicineOrderCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId patientId;
	@Field
	private String patientName;
	@Field
	private String mobileNumber;
	@Field
	private String emailAddress;
	@Field
	private String uniqueOrderId;
	@Field
	private ObjectId collectionBoyId;
	@Field
	private CollectionBoy collectionBoy;
	@Field
	private ObjectId vendorId;
	@Field
	private UserAddress shippingAddress;
	@Field
	private UserAddress billingAddress;
	@Field
	private List<MedicineOrderItems> items;
	@Field
	private List<MedicineOrderImages> rxImage;
	@Field
	private Float totalAmount;
	@Field
	private Float discountedAmount;
	@Field
	private Float discountedPercentage;
	@Field
	private Float finalAmount;
	@Field
	private Float deliveryCharges;
	@Field
	private PaymentMode paymentMode = PaymentMode.COD;
	@Field
	private DeliveryPreferences deliveryPreference = DeliveryPreferences.ONE_TIME;
	@Field
	private Long nextDeliveryDate;
	@Field
	private Boolean discarded;
	@Field
	private String notes;
	@Field
	private OrderStatus orderStatus = OrderStatus.PENDING;
	@Field
	private Long deliveredByDate;
	@Field
	private String callingPreference;
	@Field
	private Vendor vendor;
	@Field
	private Long cancellationDate;
	@Field
	private Boolean isPrescriptionRequired;
	@Field
	private ObjectId prescriptionId;
	@Field
	private Boolean isOrderPaid=false;
	@Field
	private List<RxNotes>rxNotes;
	@Field
	private Boolean isPaid=false;
	@Field
	private Double totalPrice;
	@Field
	private Double discountedPrice;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public ObjectId getVendorId() {
		return vendorId;
	}

	public void setVendorId(ObjectId vendorId) {
		this.vendorId = vendorId;
	}

	public UserAddress getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(UserAddress shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public UserAddress getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(UserAddress billingAddress) {
		this.billingAddress = billingAddress;
	}

	public List<MedicineOrderItems> getItems() {
		return items;
	}

	public void setItems(List<MedicineOrderItems> items) {
		this.items = items;
	}

	public Float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Float getDiscountedAmount() {
		return discountedAmount;
	}

	public void setDiscountedAmount(Float discountedAmount) {
		this.discountedAmount = discountedAmount;
	}

	public Float getDiscountedPercentage() {
		return discountedPercentage;
	}

	public void setDiscountedPercentage(Float discountedPercentage) {
		this.discountedPercentage = discountedPercentage;
	}

	public Float getFinalAmount() {
		return finalAmount;
	}

	public void setFinalAmount(Float finalAmount) {
		this.finalAmount = finalAmount;
	}

	public Float getDeliveryCharges() {
		return deliveryCharges;
	}

	public void setDeliveryCharges(Float deliveryCharges) {
		this.deliveryCharges = deliveryCharges;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	public DeliveryPreferences getDeliveryPreference() {
		return deliveryPreference;
	}

	public void setDeliveryPreference(DeliveryPreferences deliveryPreference) {
		this.deliveryPreference = deliveryPreference;
	}

	public Long getNextDeliveryDate() {
		return nextDeliveryDate;
	}

	public void setNextDeliveryDate(Long nextDeliveryDate) {
		this.nextDeliveryDate = nextDeliveryDate;
	}

	public String getUniqueOrderId() {
		return uniqueOrderId;
	}

	public void setUniqueOrderId(String uniqueOrderId) {
		this.uniqueOrderId = uniqueOrderId;
	}

	public List<MedicineOrderImages> getRxImage() {
		return rxImage;
	}

	public void setRxImage(List<MedicineOrderImages> rxImage) {
		this.rxImage = rxImage;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Long getDeliveredByDate() {
		return deliveredByDate;
	}

	public void setDeliveredByDate(Long deliveredByDate) {
		this.deliveredByDate = deliveredByDate;
	}

	public String getCallingPreference() {
		return callingPreference;
	}

	public void setCallingPreference(String callingPreference) {
		this.callingPreference = callingPreference;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public ObjectId getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(ObjectId collectionBoyId) {
		this.collectionBoyId = collectionBoyId;
	}

	public CollectionBoy getCollectionBoy() {
		return collectionBoy;
	}

	public void setCollectionBoy(CollectionBoy collectionBoy) {
		this.collectionBoy = collectionBoy;
	}

	public Long getCancellationDate() {
		return cancellationDate;
	}

	public void setCancellationDate(Long cancellationDate) {
		this.cancellationDate = cancellationDate;
	}

	public Boolean getIsPrescriptionRequired() {
		return isPrescriptionRequired;
	}

	public void setIsPrescriptionRequired(Boolean isPrescriptionRequired) {
		this.isPrescriptionRequired = isPrescriptionRequired;
	}

	public ObjectId getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(ObjectId prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Boolean getIsOrderPaid() {
		return isOrderPaid;
	}

	public void setIsOrderPaid(Boolean isOrderPaid) {
		this.isOrderPaid = isOrderPaid;
	}

	public List<RxNotes> getRxNotes() {
		return rxNotes;
	}

	public void setRxNotes(List<RxNotes> rxNotes) {
		this.rxNotes = rxNotes;
	}

	public Boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Double getDiscountedPrice() {
		return discountedPrice;
	}

	public void setDiscountedPrice(Double discountedPrice) {
		this.discountedPrice = discountedPrice;
	}

	
	
	

}
