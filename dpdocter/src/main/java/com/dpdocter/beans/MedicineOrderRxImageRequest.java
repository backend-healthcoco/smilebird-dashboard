package com.dpdocter.beans;

import java.util.List;

public class MedicineOrderRxImageRequest {

	private String id;
	
	private String patientId;
	
	private List<MedicineOrderImages> rxImage;
	
	private List<RxNotes>rxNotes;
	
	private Double totalPrice;
	private Double discountedPrice;
	
	private Boolean discarded=false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<MedicineOrderImages> getRxImage() {
		return rxImage;
	}

	public void setRxImage(List<MedicineOrderImages> rxImage) {
		this.rxImage = rxImage;
	}

	public List<RxNotes> getRxNotes() {
		return rxNotes;
	}

	public void setRxNotes(List<RxNotes> rxNotes) {
		this.rxNotes = rxNotes;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
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
