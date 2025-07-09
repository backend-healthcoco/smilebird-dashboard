package com.dpdocter.beans;

public class BloodPressure {

    private String systolic;

    private String diastolic;

    public String getSystolic() {
	return systolic;
    }

    public void setSystolic(String systolic) {
	this.systolic = systolic;
    }

    public String getDiastolic() {
	return diastolic;
    }

    public void setDiastolic(String diastolic) {
	this.diastolic = diastolic;
    }

    @Override
    public String toString() {
	return "BloodPressure [systolic=" + systolic + ", diastolic=" + diastolic + "]";
    }
}
