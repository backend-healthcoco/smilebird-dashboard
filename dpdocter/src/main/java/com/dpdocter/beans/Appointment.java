package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.AppointmentByType;
import com.dpdocter.enums.AppointmentForType;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.DentalAppointmentType;
import com.dpdocter.enums.QueueStatus;

public class Appointment extends GenericCollection {

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
	
	private List<String> treatmentId;
	
	private List<String> treatmentNames;
	
	private String rescheduleBy;
    
	private String smileBuddyName;
	
	public String getRescheduleBy() {
		return rescheduleBy;
	}

	public void setRescheduleBy(String rescheduleBy) {
		this.rescheduleBy = rescheduleBy;
	}

	public DentalAppointmentType getDentalAppointmentType() {
		return dentalAppointmentType;
	}

	public void setDentalAppointmentType(DentalAppointmentType dentalAppointmentType) {
		this.dentalAppointmentType = dentalAppointmentType;
	}

	public String getDentalTreatment() {
		return dentalTreatment;
	}

	public void setDentalTreatment(String dentalTreatment) {
		this.dentalTreatment = dentalTreatment;
	}

	public List<String> getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(List<String> treatmentId) {
		this.treatmentId = treatmentId;
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

	public AppointmentByType getAppointmentBy() {
		return appointmentBy;
	}

	public void setAppointmentBy(AppointmentByType appointmentBy) {
		this.appointmentBy = appointmentBy;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getClinicNumber() {
		return clinicNumber;
	}

	public void setClinicNumber(String clinicNumber) {
		this.clinicNumber = clinicNumber;
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

	public List<String> getTreatmentNames() {
		return treatmentNames;
	}

	public void setTreatmentNames(List<String> treatmentNames) {
		this.treatmentNames = treatmentNames;
	}

	public String getSmileBuddyName() {
		return smileBuddyName;
	}

	public void setSmileBuddyName(String smileBuddyName) {
		this.smileBuddyName = smileBuddyName;
	}

	@Override
	public String toString() {
		return "Appointment [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", smileBuddyId=" + smileBuddyId + ", time=" + time + ", patient=" + patient + ", state="
				+ state + ", isRescheduled=" + isRescheduled + ", fromDate=" + fromDate + ", toDate=" + toDate
				+ ", appointmentId=" + appointmentId + ", subject=" + subject + ", explanation=" + explanation
				+ ", type=" + type + ", isCalenderBlocked=" + isCalenderBlocked + ", doctorName=" + doctorName
				+ ", locationName=" + locationName + ", clinicAddress=" + clinicAddress + ", clinicNumber="
				+ clinicNumber + ", latitude=" + latitude + ", longitude=" + longitude + ", cancelledBy=" + cancelledBy
				+ ", status=" + status + ", count=" + count + ", cancelledByProfile=" + cancelledByProfile
				+ ", localPatientName=" + localPatientName + ", appointmentBy=" + appointmentBy + ", appointmentFor="
				+ appointmentFor + ", appointmentForOtherName=" + appointmentForOtherName + ", dentalTreatment="
				+ dentalTreatment + "]";
	}

}
