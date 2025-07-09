package com.dpdocter.request;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.AppointmentByType;
import com.dpdocter.enums.AppointmentForType;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.DentalAppointmentType;

public class DentalChainAppointmentRequest extends GenericCollection {

	private String appointmentId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private String localPatientName;

	private String smileBuddyId;

	private WorkingHours time;

	private Date fromDate;

	private Date toDate;

	private Boolean notifyPatientBySms;

	private Boolean notifyPatientByEmail;

	private Boolean notifyDoctorBySms;

	private Boolean notifyDoctorByEmail;

	private String cancelledBy;

	private String rescheduleBy;

	private String mobileNumber;

	private AppointmentType type = AppointmentType.APPOINTMENT;

	private AppointmentForType appointmentFor = AppointmentForType.SELF;

	private AppointmentByType appointmentBy = AppointmentByType.WEBSITE;

	private DentalAppointmentType dentalAppointmentType = DentalAppointmentType.OFFLINE;

	private String appointmentForOtherName;

	private AppointmentState state;

	private String dentalTreatment;

	private List<String> treatmentId;
    private String explanation;


	public List<String> getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(List<String> treatmentId) {
		this.treatmentId = treatmentId;
	}

	public AppointmentByType getAppointmentBy() {
		return appointmentBy;
	}

	public void setAppointmentBy(AppointmentByType appointmentBy) {
		this.appointmentBy = appointmentBy;
	}

	public String getSmileBuddyId() {
		return smileBuddyId;
	}

	public void setSmileBuddyId(String smileBuddyId) {
		this.smileBuddyId = smileBuddyId;
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

	public AppointmentState getState() {
		return state;
	}

	public void setState(AppointmentState state) {
		this.state = state;
	}

	public String getDentalTreatment() {
		return dentalTreatment;
	}

	public void setDentalTreatment(String dentalTreatment) {
		this.dentalTreatment = dentalTreatment;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
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

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Boolean getNotifyPatientBySms() {
		return notifyPatientBySms;
	}

	public void setNotifyPatientBySms(Boolean notifyPatientBySms) {
		this.notifyPatientBySms = notifyPatientBySms;
	}

	public Boolean getNotifyPatientByEmail() {
		return notifyPatientByEmail;
	}

	public void setNotifyPatientByEmail(Boolean notifyPatientByEmail) {
		this.notifyPatientByEmail = notifyPatientByEmail;
	}

	public Boolean getNotifyDoctorBySms() {
		return notifyDoctorBySms;
	}

	public void setNotifyDoctorBySms(Boolean notifyDoctorBySms) {
		this.notifyDoctorBySms = notifyDoctorBySms;
	}

	public Boolean getNotifyDoctorByEmail() {
		return notifyDoctorByEmail;
	}

	public void setNotifyDoctorByEmail(Boolean notifyDoctorByEmail) {
		this.notifyDoctorByEmail = notifyDoctorByEmail;
	}

	public String getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(String cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public AppointmentType getType() {
		return type;
	}

	public void setType(AppointmentType type) {
		this.type = type;
	}

	public DentalAppointmentType getDentalAppointmentType() {
		return dentalAppointmentType;
	}

	public void setDentalAppointmentType(DentalAppointmentType dentalAppointmentType) {
		this.dentalAppointmentType = dentalAppointmentType;
	}

	public String getRescheduleBy() {
		return rescheduleBy;
	}

	public void setRescheduleBy(String rescheduleBy) {
		this.rescheduleBy = rescheduleBy;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

}
