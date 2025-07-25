package com.dpdocter.response;

import java.util.Date;

import com.dpdocter.beans.WorkingHours;

public class AppointmentFeedbackPatientSMSResponse {

	private String doctorTitle;
	
	private String doctorName;
	
	private String patientMobileNumber;
	
	private String patientName;

	private String locationName;
	
	private String clinicNumber;

    private String appointmentId;

    private WorkingHours time;

    private Date fromDate;

    private String doctorId;

    private String locationId;
    
	public String getDoctorTitle() {
		return doctorTitle;
	}

	public void setDoctorTitle(String doctorTitle) {
		this.doctorTitle = doctorTitle;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getPatientMobileNumber() {
		return patientMobileNumber;
	}

	public void setPatientMobileNumber(String patientMobileNumber) {
		this.patientMobileNumber = patientMobileNumber;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getClinicNumber() {
		return clinicNumber;
	}

	public void setClinicNumber(String clinicNumber) {
		this.clinicNumber = clinicNumber;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	@Override
	public String toString() {
		return "AppointmentFeedbackPatientSMSResponse [doctorTitle=" + doctorTitle + ", doctorName=" + doctorName
				+ ", patientMobileNumber=" + patientMobileNumber + ", patientName=" + patientName + ", locationName="
				+ locationName + ", clinicNumber=" + clinicNumber + ", appointmentId=" + appointmentId + ", time="
				+ time + ", fromDate=" + fromDate + ", doctorId=" + doctorId + ", locationId=" + locationId + "]";
	}
}
