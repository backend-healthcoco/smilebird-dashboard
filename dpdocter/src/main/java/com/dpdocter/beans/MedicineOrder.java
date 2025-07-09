package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.DeliveryPreferences;
import com.dpdocter.enums.OrderStatus;
import com.dpdocter.enums.PaymentMode;

public class MedicineOrder extends GenericCollection {

	private String id;
	private String patientName;
	private String mobileNumber;
	private String patientId;
	private String vendorId;
	private String collectionBoyId;
	private String prescriptionId;
	private CollectionBoy collectionBoy;
	private UserAddress shippingAddress;
	private UserAddress billingAddress;
	private Vendor vendor;
	private String uniqueOrderId;
	private List<MedicineOrderAddEditItems> items;
	private List<MedicineOrderImages> rxImage;
	private Float totalAmount;
	private Float discountedAmount;
	private Float discountedPercentage;
	private Float finalAmount;
	private Float deliveryCharges;
	private OrderStatus orderStatus = OrderStatus.PENDING;
	private PaymentMode paymentMode = PaymentMode.COD;
	private DeliveryPreferences deliveryPreference = DeliveryPreferences.ONE_TIME;
	private Long nextDeliveryDate;
	private Long deliveredByDate;
	private Long cancellationDate;
	private String callingPreference;
	private Boolean discarded = false;
	private Boolean isPrescriptionRequired;
	private Prescription prescription;
	private String notes;
	private Boolean isOrderPaid=false;
	private List<RxNotes>rxNotes;
	private Boolean isPaid=false;
	private Double totalPrice;
	private Double discountedPrice;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
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

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public List<MedicineOrderAddEditItems> getItems() {
		return items;
	}

	public void setItems(List<MedicineOrderAddEditItems> items) {
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public void setRxImage(List<MedicineOrderImages> rxImage) {
		this.rxImage = rxImage;
	}

	public List<MedicineOrderImages> getRxImage() {
		return rxImage;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getUniqueOrderId() {
		return uniqueOrderId;
	}

	public void setUniqueOrderId(String uniqueOrderId) {
		this.uniqueOrderId = uniqueOrderId;
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

	public Long getCancellationDate() {
		return cancellationDate;
	}

	public void setCancellationDate(Long cancellationDate) {
		this.cancellationDate = cancellationDate;
	}

	public String getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(String collectionBoyId) {
		this.collectionBoyId = collectionBoyId;
	}

	public CollectionBoy getCollectionBoy() {
		return collectionBoy;
	}

	public void setCollectionBoy(CollectionBoy collectionBoy) {
		this.collectionBoy = collectionBoy;
	}

	public Boolean getIsPrescriptionRequired() {
		return isPrescriptionRequired;
	}

	public void setIsPrescriptionRequired(Boolean isPrescriptionRequired) {
		this.isPrescriptionRequired = isPrescriptionRequired;
	}

	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public Prescription getPrescription() {
		return prescription;
	}

	public void setPrescription(Prescription prescription) {
		this.prescription = prescription;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
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

	@Override
	public String toString() {
		return "MedicineOrder [id=" + id + ", patientId=" + patientId + ", vendorId=" + vendorId + ", shippingAddress="
				+ shippingAddress + ", billingAddress=" + billingAddress + ", vendor=" + vendor + ", items=" + items
				+ ", rxImage=" + rxImage + ", totalAmount=" + totalAmount + ", discountedAmount=" + discountedAmount
				+ ", discountedPercentage=" + discountedPercentage + ", finalAmount=" + finalAmount
				+ ", deliveryCharges=" + deliveryCharges + ", paymentMode=" + paymentMode + ", deliveryPreference="
				+ deliveryPreference + ", nextDeliveryDate=" + nextDeliveryDate + ", discarded=" + discarded + "]";
	}

}
