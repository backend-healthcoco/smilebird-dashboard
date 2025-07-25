package com.dpdocter.response;


	import java.util.Date;
	import java.util.List;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.Fields;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.GenericCollection;
	import com.dpdocter.enums.AppointmentState;
	import com.dpdocter.enums.AppointmentType;
	import com.dpdocter.enums.ConsultationType;
	import com.dpdocter.enums.QueueStatus;
	import com.dpdocter.response.PatientTreatmentResponse;

	public class ConsultationSpeciality  extends GenericCollection {

		private String id;

		private String doctorId;

		private String locationId;

		private String hospitalId;

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

		private Boolean isFeedbackAvailable = false;

		private Boolean isAllDayEvent = false;

		private String doctorName;

		private String locationName;

		private String clinicAddress;

		private String clinicNumber;

		private Double latitude;

		private Double longitude;

		private String cancelledBy;

		private Boolean notifyPatientBySms;

		private Boolean notifyPatientByEmail;

		private Boolean notifyDoctorBySms;

		private Boolean notifyDoctorByEmail;

		private String visitId;

		private QueueStatus status = QueueStatus.SCHEDULED;

		private RegisteredPatientDetails registeredPatientDetails;

		private long waitedFor = 0;

		private long engagedFor = 0;

		private long engagedAt = 0;

		private long checkedInAt = 0;

		private long checkedOutAt = 0;

		private Integer count;

		private String category;

		private String branch;

		private String cancelledByProfile;

		private String localPatientName;

		private List<Fields> treatmentFields;

		private Boolean isCreatedByPatient = false;

		private PatientTreatmentResponse patientTreatmentResponse;

		private ConsultationType consultationType;

		private Date consultationStartedOn;

		private String problemDetailsId;

		private AppointmentSlot onlineConsultationSlot;

		private Long callDurationInMinutes = 0L;
		
		private Boolean isAnonymousAppointment = false;
		
		private String specialityId;
		
		private String paymentId;
		
		private String paymentStatus;
		
		private Boolean isPaymentTransfer=false;
		

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

		public Boolean getIsFeedbackAvailable() {
			return isFeedbackAvailable;
		}

		public void setIsFeedbackAvailable(Boolean isFeedbackAvailable) {
			this.isFeedbackAvailable = isFeedbackAvailable;
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

		public Boolean getIsAllDayEvent() {
			return isAllDayEvent;
		}

		public void setIsAllDayEvent(Boolean isAllDayEvent) {
			this.isAllDayEvent = isAllDayEvent;
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

		public String getVisitId() {
			return visitId;
		}

		public void setVisitId(String visitId) {
			this.visitId = visitId;
		}

		public QueueStatus getStatus() {
			return status;
		}

		public void setStatus(QueueStatus status) {
			this.status = status;
		}

		public RegisteredPatientDetails getRegisteredPatientDetails() {
			return registeredPatientDetails;
		}

		public void setRegisteredPatientDetails(RegisteredPatientDetails registeredPatientDetails) {
			this.registeredPatientDetails = registeredPatientDetails;
		}

		public long getWaitedFor() {
			return waitedFor;
		}

		public void setWaitedFor(long waitedFor) {
			this.waitedFor = waitedFor;
		}

		public long getEngagedFor() {
			return engagedFor;
		}

		public void setEngagedFor(long engagedFor) {
			this.engagedFor = engagedFor;
		}

		public long getEngagedAt() {
			return engagedAt;
		}

		public void setEngagedAt(long engagedAt) {
			this.engagedAt = engagedAt;
		}

		public long getCheckedInAt() {
			return checkedInAt;
		}

		public void setCheckedInAt(long checkedInAt) {
			this.checkedInAt = checkedInAt;
		}

		public long getCheckedOutAt() {
			return checkedOutAt;
		}

		public void setCheckedOutAt(long checkedOutAt) {
			this.checkedOutAt = checkedOutAt;
		}

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getBranch() {
			return branch;
		}

		public void setBranch(String branch) {
			this.branch = branch;
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

		public List<Fields> getTreatmentFields() {
			return treatmentFields;
		}

		public void setTreatmentFields(List<Fields> treatmentFields) {
			this.treatmentFields = treatmentFields;
		}

		public Boolean getIsCreatedByPatient() {
			return isCreatedByPatient;
		}

		public void setIsCreatedByPatient(Boolean isCreatedByPatient) {
			this.isCreatedByPatient = isCreatedByPatient;
		}

		public PatientTreatmentResponse getPatientTreatmentResponse() {
			return patientTreatmentResponse;
		}

		public void setPatientTreatmentResponse(PatientTreatmentResponse patientTreatmentResponse) {
			this.patientTreatmentResponse = patientTreatmentResponse;
		}

		public ConsultationType getConsultationType() {
			return consultationType;
		}

		public void setConsultationType(ConsultationType consultationType) {
			this.consultationType = consultationType;
		}

		public Date getConsultationStartedOn() {
			return consultationStartedOn;
		}

		public void setConsultationStartedOn(Date consultationStartedOn) {
			this.consultationStartedOn = consultationStartedOn;
		}

		public String getProblemDetailsId() {
			return problemDetailsId;
		}

		public void setProblemDetailsId(String problemDetailsId) {
			this.problemDetailsId = problemDetailsId;
		}

		
		public AppointmentSlot getOnlineConsultationSlot() {
			return onlineConsultationSlot;
		}

		public void setOnlineConsultationSlot(AppointmentSlot onlineConsultationSlot) {
			this.onlineConsultationSlot = onlineConsultationSlot;
		}

		public Long getCallDurationInMinutes() {
			return callDurationInMinutes;
		}

		public void setCallDurationInMinutes(Long callDurationInMinutes) {
			this.callDurationInMinutes = callDurationInMinutes;
		}
		
		

		public Boolean getIsAnonymousAppointment() {
			return isAnonymousAppointment;
		}

		public void setIsAnonymousAppointment(Boolean isAnonymousAppointment) {
			this.isAnonymousAppointment = isAnonymousAppointment;
		}

		public String getSpecialityId() {
			return specialityId;
		}

		public void setSpecialityId(String specialityId) {
			this.specialityId = specialityId;
		}
		
		

		public String getPaymentId() {
			return paymentId;
		}

		public void setPaymentId(String paymentId) {
			this.paymentId = paymentId;
		}

		public String getPaymentStatus() {
			return paymentStatus;
		}

		public void setPaymentStatus(String paymentStatus) {
			this.paymentStatus = paymentStatus;
		}

		

		public Boolean getIsPaymentTransfer() {
			return isPaymentTransfer;
		}

		public void setIsPaymentTransfer(Boolean isPaymentTransfer) {
			this.isPaymentTransfer = isPaymentTransfer;
		}

		@Override
		public String toString() {
			return "Appointment [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
					+ hospitalId + ", time=" + time + ", patient=" + patient + ", state=" + state + ", isRescheduled="
					+ isRescheduled + ", fromDate=" + fromDate + ", toDate=" + toDate + ", appointmentId=" + appointmentId
					+ ", subject=" + subject + ", explanation=" + explanation + ", type=" + type + ", isCalenderBlocked="
					+ isCalenderBlocked + ", isFeedbackAvailable=" + isFeedbackAvailable + ", isAllDayEvent="
					+ isAllDayEvent + ", doctorName=" + doctorName + ", locationName=" + locationName + ", clinicAddress="
					+ clinicAddress + ", clinicNumber=" + clinicNumber + ", latitude=" + latitude + ", longitude="
					+ longitude + ", cancelledBy=" + cancelledBy + ", notifyPatientBySms=" + notifyPatientBySms
					+ ", notifyPatientByEmail=" + notifyPatientByEmail + ", notifyDoctorBySms=" + notifyDoctorBySms
					+ ", notifyDoctorByEmail=" + notifyDoctorByEmail + "]";
		}
	}


