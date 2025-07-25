package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.DOB;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.AppointmentByType;
import com.dpdocter.enums.AppointmentForType;
import com.dpdocter.enums.AppointmentState;

public class PatientTimelineHistoryResponse extends GenericCollection{

	private String id;

	private String patientId;

	private String firstName;

	private String localPatientName;

	private String emailAddress;

	private String gender;

	private String mobileNumber;

	private DOB dob;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Date followUp;

	private List<String> platform;

	private String followUpReason;

	private Date registrationDate;

	private WorkingHours time;

	private AppointmentState state;

	private Date fromDate;

	private Date toDate;

	private String appointmentId;

	private String doctorName;

	private String locationName;

	private AppointmentByType appointmentBy;

	private AppointmentForType appointmentFor;

	private String appointmentForOtherName;

	private String dentalTreatment;
	
	private String patientCreateBy;
	
	private String appointmentCreateBy;

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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Date getFollowUp() {
		return followUp;
	}

	public void setFollowUp(Date followUp) {
		this.followUp = followUp;
	}

	public List<String> getPlatform() {
		return platform;
	}

	public void setPlatform(List<String> platform) {
		this.platform = platform;
	}

	public String getFollowUpReason() {
		return followUpReason;
	}

	public void setFollowUpReason(String followUpReason) {
		this.followUpReason = followUpReason;
	}


	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public AppointmentState getState() {
		return state;
	}

	public void setState(AppointmentState state) {
		this.state = state;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public AppointmentByType getAppointmentBy() {
		return appointmentBy;
	}

	public void setAppointmentBy(AppointmentByType appointmentBy) {
		this.appointmentBy = appointmentBy;
	}

	public AppointmentForType getAppointmentFor() {
		return appointmentFor;
	}

	public void setAppointmentFor(AppointmentForType appointmentFor) {
		this.appointmentFor = appointmentFor;
	}

	public String getAppointmentForOtherName() {
		return appointmentForOtherName;
	}

	public void setAppointmentForOtherName(String appointmentForOtherName) {
		this.appointmentForOtherName = appointmentForOtherName;
	}

	public String getDentalTreatment() {
		return dentalTreatment;
	}

	public void setDentalTreatment(String dentalTreatment) {
		this.dentalTreatment = dentalTreatment;
	}

	public String getPatientCreateBy() {
		return patientCreateBy;
	}

	public void setPatientCreateBy(String patientCreateBy) {
		this.patientCreateBy = patientCreateBy;
	}

	public String getAppointmentCreateBy() {
		return appointmentCreateBy;
	}

	public void setAppointmentCreateBy(String appointmentCreateBy) {
		this.appointmentCreateBy = appointmentCreateBy;
	}
	
}
