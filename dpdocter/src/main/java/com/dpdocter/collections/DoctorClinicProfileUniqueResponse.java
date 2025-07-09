package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.DoctorConsultation;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.enums.LabType;
import com.dpdocter.enums.PackageType;

public class DoctorClinicProfileUniqueResponse extends GenericCollection {

	@Id
	private ObjectId id;

	@Indexed
	private ObjectId userLocationId;

	@Indexed
	private ObjectId doctorId;

	@Indexed
	private ObjectId locationId;

	@Field
	private Boolean isActivate = false;

	@Field
	private Boolean isVerified = true;

	@Field
	private Boolean discarded = false;

	@Field
	private String patientInitial = "P";

	@Field
	private int patientCounter = 1;

	@Field
	private List<String> appointmentBookingNumber;

	@Field
	private ConsultationFee consultationFee;

	@Field
	private AppointmentSlot appointmentSlot;

	@Field
	private List<WorkingSchedule> workingSchedules;

	@Field
	private DoctorFacility facility = DoctorFacility.CALL;

	@Field
	private Integer noOfReviews = 0;

	@Field
	private Integer noOfRecommenations = 0;

	@Field
	private String timeZone = "IST";

	@Field
	private Boolean isDoctorListed = false;

	@Field
	private long rankingCount = 1000;

	@Field
	private String packageType = PackageType.BASIC.getType();

	@Field
	private String doctorSlugURL;

	@Field
	private String labType = LabType.DIAGNOSTIC.getType();

	@Field
	private Boolean iskiosk = false;

	@Field
	private Boolean isNutritionist = false;

	@Field
	private Boolean isAdminNutritionist = false;

	@Field
	private Boolean isSuperAdmin = false;

	@Field
	private String mrCode;

	@Field
	private List<ObjectId> divisionIds;

	@Field
	private ObjectId cityId;

	@Field
	private Boolean isVaccinationModuleOn = false;

	@Field
	private String feedbackURL;
	
	@Field
	private List<WorkingSchedule> onlineWorkingSchedules;
	
	@Field
	private List<DoctorConsultation>  consultationType;
	
	@Field
	private Boolean isOnlineConsultationAvailable = false;
	
	@Field
	private Boolean isSendBirthdaySMS = false;

	
	@Field
	private String clinicOwnershipImageUrl;
	
	@Field
	private Boolean isClinicOwnershipVerified=false;
	
	@Field
	private Date activationDate;//to set the activation date 

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getUserLocationId() {
		return userLocationId;
	}

	public void setUserLocationId(ObjectId userLocationId) {
		this.userLocationId = userLocationId;
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

	public Boolean getIsActivate() {
		return isActivate;
	}

	public void setIsActivate(Boolean isActivate) {
		this.isActivate = isActivate;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getPatientInitial() {
		return patientInitial;
	}

	public void setPatientInitial(String patientInitial) {
		this.patientInitial = patientInitial;
	}

	public int getPatientCounter() {
		return patientCounter;
	}

	public void setPatientCounter(int patientCounter) {
		this.patientCounter = patientCounter;
	}

	public List<String> getAppointmentBookingNumber() {
		return appointmentBookingNumber;
	}

	public void setAppointmentBookingNumber(List<String> appointmentBookingNumber) {
		this.appointmentBookingNumber = appointmentBookingNumber;
	}

	public ConsultationFee getConsultationFee() {
		return consultationFee;
	}

	public void setConsultationFee(ConsultationFee consultationFee) {
		this.consultationFee = consultationFee;
	}

	public AppointmentSlot getAppointmentSlot() {
		return appointmentSlot;
	}

	public void setAppointmentSlot(AppointmentSlot appointmentSlot) {
		this.appointmentSlot = appointmentSlot;
	}

	public List<WorkingSchedule> getWorkingSchedules() {
		return workingSchedules;
	}

	public void setWorkingSchedules(List<WorkingSchedule> workingSchedules) {
		this.workingSchedules = workingSchedules;
	}

	public DoctorFacility getFacility() {
		return facility;
	}

	public void setFacility(DoctorFacility facility) {
		this.facility = facility;
	}

	public Integer getNoOfReviews() {
		return noOfReviews;
	}

	public void setNoOfReviews(Integer noOfReviews) {
		this.noOfReviews = noOfReviews;
	}

	public Integer getNoOfRecommenations() {
		return noOfRecommenations;
	}

	public void setNoOfRecommenations(Integer noOfRecommenations) {
		this.noOfRecommenations = noOfRecommenations;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public Boolean getIsDoctorListed() {
		return isDoctorListed;
	}

	public void setIsDoctorListed(Boolean isDoctorListed) {
		this.isDoctorListed = isDoctorListed;
	}

	public long getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(long rankingCount) {
		this.rankingCount = rankingCount;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public String getDoctorSlugURL() {
		return doctorSlugURL;
	}

	public void setDoctorSlugURL(String doctorSlugURL) {
		this.doctorSlugURL = doctorSlugURL;
	}

	public String getLabType() {
		return labType;
	}

	public void setLabType(String labType) {
		this.labType = labType;
	}

	public Boolean getIskiosk() {
		return iskiosk;
	}

	public void setIskiosk(Boolean iskiosk) {
		this.iskiosk = iskiosk;
	}

	public Boolean getIsNutritionist() {
		return isNutritionist;
	}

	public void setIsNutritionist(Boolean isNutritionist) {
		this.isNutritionist = isNutritionist;
	}

	public Boolean getIsAdminNutritionist() {
		return isAdminNutritionist;
	}

	public void setIsAdminNutritionist(Boolean isAdminNutritionist) {
		this.isAdminNutritionist = isAdminNutritionist;
	}

	public Boolean getIsSuperAdmin() {
		return isSuperAdmin;
	}

	public void setIsSuperAdmin(Boolean isSuperAdmin) {
		this.isSuperAdmin = isSuperAdmin;
	}

	public String getMrCode() {
		return mrCode;
	}

	public void setMrCode(String mrCode) {
		this.mrCode = mrCode;
	}

	public List<ObjectId> getDivisionIds() {
		return divisionIds;
	}

	public void setDivisionIds(List<ObjectId> divisionIds) {
		this.divisionIds = divisionIds;
	}

	public ObjectId getCityId() {
		return cityId;
	}

	public void setCityId(ObjectId cityId) {
		this.cityId = cityId;
	}

	public Boolean getIsVaccinationModuleOn() {
		return isVaccinationModuleOn;
	}

	public void setIsVaccinationModuleOn(Boolean isVaccinationModuleOn) {
		this.isVaccinationModuleOn = isVaccinationModuleOn;
	}

	public String getFeedbackURL() {
		return feedbackURL;
	}

	public void setFeedbackURL(String feedbackURL) {
		this.feedbackURL = feedbackURL;
	}

	public List<WorkingSchedule> getOnlineWorkingSchedules() {
		return onlineWorkingSchedules;
	}

	public void setOnlineWorkingSchedules(List<WorkingSchedule> onlineWorkingSchedules) {
		this.onlineWorkingSchedules = onlineWorkingSchedules;
	}

	public List<DoctorConsultation> getConsultationType() {
		return consultationType;
	}

	public void setConsultationType(List<DoctorConsultation> consultationType) {
		this.consultationType = consultationType;
	}

	public Boolean getIsOnlineConsultationAvailable() {
		return isOnlineConsultationAvailable;
	}

	public void setIsOnlineConsultationAvailable(Boolean isOnlineConsultationAvailable) {
		this.isOnlineConsultationAvailable = isOnlineConsultationAvailable;
	}

	public Boolean getIsSendBirthdaySMS() {
		return isSendBirthdaySMS;
	}

	public void setIsSendBirthdaySMS(Boolean isSendBirthdaySMS) {
		this.isSendBirthdaySMS = isSendBirthdaySMS;
	}

	public String getClinicOwnershipImageUrl() {
		return clinicOwnershipImageUrl;
	}

	public void setClinicOwnershipImageUrl(String clinicOwnershipImageUrl) {
		this.clinicOwnershipImageUrl = clinicOwnershipImageUrl;
	}

	public Boolean getIsClinicOwnershipVerified() {
		return isClinicOwnershipVerified;
	}

	public void setIsClinicOwnershipVerified(Boolean isClinicOwnershipVerified) {
		this.isClinicOwnershipVerified = isClinicOwnershipVerified;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}
	
	
}
