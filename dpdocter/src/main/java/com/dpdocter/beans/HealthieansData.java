package com.dpdocter.beans;

import java.util.List;

public class HealthieansData {

	private String booking_id;
	private String booking_status;
	private List<HealthiensCustomer> customer;
	public String getBooking_id() {
		return booking_id;
	}
	public void setBooking_id(String booking_id) {
		this.booking_id = booking_id;
	}
	public String getBooking_status() {
		return booking_status;
	}
	public void setBooking_status(String booking_status) {
		this.booking_status = booking_status;
	}
	public List<HealthiensCustomer> getCustomer() {
		return customer;
	}
	public void setCustomer(List<HealthiensCustomer> customer) {
		this.customer = customer;
	}
	
	
	
}
