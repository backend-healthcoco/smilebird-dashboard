package com.dpdocter.beans;

public class Slot {

	private String time;

	private Boolean isAvailable = true;

	private Integer minutesOfDay;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public Integer getMinutesOfDay() {
		return minutesOfDay;
	}

	public void setMinutesOfDay(Integer minutesOfDay) {
		this.minutesOfDay = minutesOfDay;
	}

	@Override
	public boolean equals(Object object) {
		return (this.time.equals(((Slot) object).time));
	}

	@Override
	public String toString() {
		return "Slot [time=" + time + ", isAvailable=" + isAvailable + "]";
	}
}
