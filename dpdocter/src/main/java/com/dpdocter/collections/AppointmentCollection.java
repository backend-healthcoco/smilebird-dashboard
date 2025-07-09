package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.FieldsCollection;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.enums.AppointmentByType;
import com.dpdocter.enums.AppointmentForType;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.ConsultationType;
import com.dpdocter.enums.DentalAppointmentType;
import com.dpdocter.enums.QueueStatus;

@Document(collection = "appointment_cl")
@CompoundIndexes({
    @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}")
})
public class AppointmentCollection extends GenericCollection {

    @Id
    private ObjectId id;

    @Field
    private String subject;

    @Field
    private String explanation;

    @Indexed
    private ObjectId doctorId;

    @Field
    private ObjectId locationId;

    @Field
    private ObjectId hospitalId;

    @Field
    private String appointmentId;

    @Field
    private WorkingHours time;

    @Indexed
    private ObjectId patientId;

    @Field
    private AppointmentState state = AppointmentState.NEW;

    @Field
    private AppointmentType type = AppointmentType.APPOINTMENT;

    @Field
    private Boolean isRescheduled = false;

    @Field
    private Date fromDate;

    @Field
    private Date toDate;
    
    @Field
    private Boolean isCalenderBlocked = false;

    @Field
    private Boolean isFeedbackAvailable = false;

    @Field
    private Boolean isAllDayEvent = false;
    
    @Field
    private String cancelledBy;
   
    @Field
    private String rescheduleBy;
    
    @Field
    private Boolean notifyPatientBySms;

    @Field
    private Boolean notifyPatientByEmail;

    @Field
    private Boolean notifyDoctorBySms;

    @Field
    private Boolean notifyDoctorByEmail;
    
	@Field
	private ObjectId visitId;

	@Field
	private QueueStatus status = QueueStatus.SCHEDULED;

	@Field
	private long waitedFor = 0;

	@Field
	private long engagedFor = 0;

	@Field
	private long engagedAt = 0;

	@Field
	private long checkedInAt = 0;

	@Field
	private long checkedOutAt = 0;

	@Field
	private String category;

	@Field
	private String branch;
	
	@Field
	private Boolean isPatientDiscarded = false;

	@Field
	private String cancelledByProfile;
	
	@Field
    private String rescheduleByProfile;

	@Field
	private String localPatientName;
	
	@Field
	private List<ObjectId> doctorIds;

	@Field
	private List<FieldsCollection> treatmentFields;

	@Field
	private Boolean isCreatedByPatient = false;
	
	@Field
	private ConsultationType consultationType;
	
	@Field
	private Date consultationStartedOn;
	@Field
	private ObjectId problemDetailsId;
	
	@Field
	private AppointmentSlot onlineConsultationSlot;
	
	@Field
	private Long callDurationInMinutes=0L;
	
	@Field
	private Boolean isAnonymousAppointment = false;
	@Field
	private ObjectId specialityId;
	@Field
	private AppointmentByType appointmentBy = AppointmentByType.CALL;
	@Field
	private AppointmentForType appointmentFor = AppointmentForType.SELF;
	@Field
	private String appointmentForOtherName ;
	@Field
	private String dentalTreatment;
	@Field
	private Boolean isDentalChainAppointment = false;
	@Field
	private DentalAppointmentType dentalAppointmentType = DentalAppointmentType.OFFLINE;
	@Field
	private List<ObjectId> treatmentId;
	
	@Field
	private ObjectId smileBuddyId;

	public List<ObjectId> getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(List<ObjectId> treatmentId) {
		this.treatmentId = treatmentId;
	}

	public DentalAppointmentType getDentalAppointmentType() {
		return dentalAppointmentType;
	}

	public void setDentalAppointmentType(DentalAppointmentType dentalAppointmentType) {
		this.dentalAppointmentType = dentalAppointmentType;
	}

	public Boolean getIsDentalChainAppointment() {
		return isDentalChainAppointment;
	}

	public void setIsDentalChainAppointment(Boolean isDentalChainAppointment) {
		this.isDentalChainAppointment = isDentalChainAppointment;
	}
	public String getDentalTreatment() {
		return dentalTreatment;
	}

	public void setDentalTreatment(String dentalTreatment) {
		this.dentalTreatment = dentalTreatment;
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

	public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
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

    public ObjectId getPatientId() {
	return patientId;
    }

    public void setPatientId(ObjectId patientId) {
	this.patientId = patientId;
    }

    public AppointmentState getState() {
	return state;
    }

    public void setState(AppointmentState state) {
	this.state = state;
    }

    public AppointmentType getType() {
	return type;
    }

    public void setType(AppointmentType type) {
	this.type = type;
    }

    public Boolean getIsRescheduled() {
	return isRescheduled;
    }

    public void setIsRescheduled(Boolean isRescheduled) {
	this.isRescheduled = isRescheduled;
    }

    public Boolean getIsCalenderBlocked() {
	return isCalenderBlocked;
    }

    public void setIsCalenderBlocked(Boolean isCalenderBlocked) {
	this.isCalenderBlocked = isCalenderBlocked;
    }

    public ObjectId getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(ObjectId doctorId) {
	this.doctorId = doctorId;
    }

    public ObjectId getLocationId() {
	return locationId;
    }

    public void setLocationId(ObjectId locationId) {
	this.locationId = locationId;
    }

    public ObjectId getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(ObjectId hospitalId) {
	this.hospitalId = hospitalId;
    }

    public Boolean getIsFeedbackAvailable() {
	return isFeedbackAvailable;
    }

    public void setIsFeedbackAvailable(Boolean isFeedbackAvailable) {
	this.isFeedbackAvailable = isFeedbackAvailable;
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

	
	public ObjectId getVisitId() {
		return visitId;
	}

	public void setVisitId(ObjectId visitId) {
		this.visitId = visitId;
	}

	public QueueStatus getStatus() {
		return status;
	}

	public void setStatus(QueueStatus status) {
		this.status = status;
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

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
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

	public List<ObjectId> getDoctorIds() {
		return doctorIds;
	}

	public void setDoctorIds(List<ObjectId> doctorIds) {
		this.doctorIds = doctorIds;
	}

	public List<FieldsCollection> getTreatmentFields() {
		return treatmentFields;
	}

	public void setTreatmentFields(List<FieldsCollection> treatmentFields) {
		this.treatmentFields = treatmentFields;
	}

	public Boolean getIsCreatedByPatient() {
		return isCreatedByPatient;
	}

	public void setIsCreatedByPatient(Boolean isCreatedByPatient) {
		this.isCreatedByPatient = isCreatedByPatient;
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

	public ObjectId getProblemDetailsId() {
		return problemDetailsId;
	}

	public void setProblemDetailsId(ObjectId problemDetailsId) {
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

	

	public ObjectId getSpecialityId() {
		return specialityId;
	}

	public void setSpecialityId(ObjectId specialityId) {
		this.specialityId = specialityId;
	}

	public ObjectId getSmileBuddyId() {
		return smileBuddyId;
	}

	public void setSmileBuddyId(ObjectId smileBuddyId) {
		this.smileBuddyId = smileBuddyId;
	}

	public String getRescheduleBy() {
		return rescheduleBy;
	}

	public void setRescheduleBy(String rescheduleBy) {
		this.rescheduleBy = rescheduleBy;
	}

	public String getRescheduleByProfile() {
		return rescheduleByProfile;
	}

	public void setRescheduleByProfile(String rescheduleByProfile) {
		this.rescheduleByProfile = rescheduleByProfile;
	}

	@Override
	public String toString() {
		return "AppointmentCollection [id=" + id + ", subject=" + subject + ", explanation=" + explanation
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", appointmentId=" + appointmentId + ", time=" + time + ", patientId=" + patientId + ", state="
				+ state + ", type=" + type + ", isRescheduled=" + isRescheduled + ", fromDate=" + fromDate + ", toDate="
				+ toDate + ", isCalenderBlocked=" + isCalenderBlocked + ", isFeedbackAvailable=" + isFeedbackAvailable
				+ ", isAllDayEvent=" + isAllDayEvent + ", cancelledBy=" + cancelledBy + ", notifyPatientBySms="
				+ notifyPatientBySms + ", notifyPatientByEmail=" + notifyPatientByEmail + ", notifyDoctorBySms="
				+ notifyDoctorBySms + ", notifyDoctorByEmail=" + notifyDoctorByEmail + "]";
	}
}
