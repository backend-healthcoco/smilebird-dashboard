package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.DentalTreatmentDetailCollection;
import com.dpdocter.enums.AppointmentByType;
import com.dpdocter.enums.AppointmentForType;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.DentalAppointmentType;
import com.dpdocter.enums.QueueStatus;

public class SmilebirdAppoinmentLookupResponse {
	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String smileBuddyId;

	private WorkingHours time;

	private PatientCard patient;

	private AppointmentState state;

	private Boolean isRescheduled = false;

	private Date fromDate;

	private Date toDate;

	private String appointmentId;

	private String subject;

	private String explanation;

	private AppointmentType type;

	private Boolean isCalenderBlocked = false;

	private String doctorName;

	private String locationName;

	private String clinicAddress;

	private String clinicNumber;

	private Double latitude;

	private Double longitude;

	private String cancelledBy;

	private QueueStatus status = QueueStatus.SCHEDULED;

	private Integer count;

	private String cancelledByProfile;

	private String localPatientName;

	private AppointmentByType appointmentBy;

	private AppointmentForType appointmentFor;

	private String appointmentForOtherName;

	private String dentalTreatment;

	private DentalAppointmentType dentalAppointmentType;
	
	private List<DentalTreatmentDetailCollection> dentalTreatments;

	private String smileBuddyName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getSmileBuddyId() {
		return smileBuddyId;
	}

	public void setSmileBuddyId(String smileBuddyId) {
		this.smileBuddyId = smileBuddyId;
	}

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public PatientCard getPatient() {
		return patient;
	}

	public void setPatient(PatientCard patient) {
		this.patient = patient;
	}

	public AppointmentState getState() {
		return state;
	}

	public void setState(AppointmentState state) {
		this.state = state;
	}

	public Boolean getIsRescheduled() {
		return isRescheduled;
	}

	public void setIsRescheduled(Boolean isRescheduled) {
		this.isRescheduled = isRescheduled;
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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public AppointmentType getType() {
		return type;
	}

	public void setType(AppointmentType type) {
		this.type = type;
	}

	public Boolean getIsCalenderBlocked() {
		return isCalenderBlocked;
	}

	public void setIsCalenderBlocked(Boolean isCalenderBlocked) {
		this.isCalenderBlocked = isCalenderBlocked;
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

	public String getClinicAddress() {
		return clinicAddress;
	}

	public void setClinicAddress(String clinicAddress) {
		this.clinicAddress = clinicAddress;
	}

	public String getClinicNumber() {
		return clinicNumber;
	}

	public void setClinicNumber(String clinicNumber) {
		this.clinicNumber = clinicNumber;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(String cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	public QueueStatus getStatus() {
		return status;
	}

	public void setStatus(QueueStatus status) {
		this.status = status;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getCancelledByProfile() {
		return cancelledByProfile;
	}

	public void setCancelledByProfile(String cancelledByProfile) {
		this.cancelledByProfile = cancelledByProfile;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
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

	public DentalAppointmentType getDentalAppointmentType() {
		return dentalAppointmentType;
	}

	public void setDentalAppointmentType(DentalAppointmentType dentalAppointmentType) {
		this.dentalAppointmentType = dentalAppointmentType;
	}

	public List<DentalTreatmentDetailCollection> getDentalTreatments() {
		return dentalTreatments;
	}

	public void setDentalTreatments(List<DentalTreatmentDetailCollection> dentalTreatments) {
		this.dentalTreatments = dentalTreatments;
	}

	public String getSmileBuddyName() {
		return smileBuddyName;
	}

	public void setSmileBuddyName(String smileBuddyName) {
		this.smileBuddyName = smileBuddyName;
	}
}
