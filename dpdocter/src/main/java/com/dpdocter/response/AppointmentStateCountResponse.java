package com.dpdocter.response;

public class AppointmentStateCountResponse {
	private Integer confirm = 0;
	private Integer pending = 0;
	private Integer cancel = 0;
	private Integer reschedule = 0;
	private Integer noValues = 0;

	public Integer getNoValues() {
		return noValues;
	}

	public void setNoValues(Integer noValues) {
		this.noValues = noValues;
	}

	public Integer getConfirm() {
		return confirm;
	}

	public void setConfirm(Integer confirm) {
		this.confirm = confirm;
	}

	public Integer getPending() {
		return pending;
	}

	public void setPending(Integer pending) {
		this.pending = pending;
	}

	public Integer getCancel() {
		return cancel;
	}

	public void setCancel(Integer cancel) {
		this.cancel = cancel;
	}

	public Integer getReschedule() {
		return reschedule;
	}

	public void setReschedule(Integer reschedule) {
		this.reschedule = reschedule;
	}

}
