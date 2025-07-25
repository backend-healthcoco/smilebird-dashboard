package com.dpdocter.response;

public class ClinicAppointmentCountResponse {
	private String name;
	private long count = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

}
