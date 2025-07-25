package com.dpdocter.response;

public class AppointmentStatusCountResponse {
	private Integer scheduled = 0;
	private Integer checkOut = 0;
	private Integer noShow = 0;
	private Integer noValues = 0;

	public Integer getNoValues() {
		return noValues;
	}

	public void setNoValues(Integer noValues) {
		this.noValues = noValues;
	}
	public Integer getScheduled() {
		return scheduled;
	}

	public void setScheduled(Integer scheduled) {
		this.scheduled = scheduled;
	}

	public Integer getCheckOut() {
		return checkOut;
	}

	public void setCheckOut(Integer checkOut) {
		this.checkOut = checkOut;
	}

	public Integer getNoShow() {
		return noShow;
	}

	public void setNoShow(Integer noShow) {
		this.noShow = noShow;
	}

}
