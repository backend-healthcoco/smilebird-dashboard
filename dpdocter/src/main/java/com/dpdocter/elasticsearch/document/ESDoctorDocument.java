package com.dpdocter.elasticsearch.document;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;

import com.dpdocter.beans.Achievement;
import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.DoctorConsultation;
import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.beans.DoctorRegistrationDetail;
import com.dpdocter.beans.Education;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.elasticsearch.beans.DoctorLocation;

@Document(indexName = "doctors_in", type = "doctors")
public class ESDoctorDocument extends DoctorLocation {
	@Id
	private String id;

	@Field(type = FieldType.Text)
	private String userId;

	@Field(type = FieldType.Text)
	private String firstName;

	@Field(type = FieldType.Text)
	private String gender;

	@Field(type = FieldType.Text)
	private String emailAddress;

	@Field(type = FieldType.Text)
	private String mobileNumber;

	@Field(type = FieldType.Text)
	private String imageUrl;

	@Field(type = FieldType.Text)
	private String thumbnailUrl;

	@Field(type = FieldType.Nested)
	private ConsultationFee consultationFee;

	@Field(type = FieldType.Nested)
	private List<WorkingSchedule> workingSchedules;

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> specialities;// ids

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> services;// ids

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> parentSpecialities;

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> specialitiesValue;// value

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> servicesValue;// value

	@MultiField(mainField = @Field(type = FieldType.Keyword))
	private List<String> formattedParentSpecialities;

	@MultiField(mainField = @Field(type = FieldType.Keyword))
	private List<String> formattedSpecialitiesValue;// value

	@MultiField(mainField = @Field(type = FieldType.Keyword))
	private List<String> formattedServicesValue;// value

	@Field(type = FieldType.Nested)
	private DoctorExperience experience;

	@Field(type = FieldType.Text)
	private String facility;

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> appointmentBookingNumber;

	@Field(type = FieldType.Nested)
	private AppointmentSlot appointmentSlot;

	@Field(type = FieldType.Boolean)
	private Boolean isActive = false;

	@Field(type = FieldType.Boolean)
	private Boolean isVerified = false;

	@Field(type = FieldType.Text)
	private String coverImageUrl;

	@Field(type = FieldType.Text)
	private String coverThumbnailImageUrl;

	@Field(type = FieldType.Text)
	private String colorCode;

	@Field(type = FieldType.Text)
	private String userState;

	@Field(type = FieldType.Text)
	private String registerNumber;

	@Field(type = FieldType.Nested)
	private DOB dob;

	@Transient
	private Double distance;

	@Field(type = FieldType.Text)
	private String userUId;

	@Field(type = FieldType.Text)
	private String timeZone = "IST";

	@Field(type = FieldType.Boolean)
	private Boolean isDoctorListed = false;

	@Field(type = FieldType.Long)
	private long rankingCount = 1000;

	@Field(type = FieldType.Integer)
	private Integer noOfRecommenations = 0;

	@Field(type = FieldType.Text)
	private String doctorSlugURL;

	@Field(type = FieldType.Boolean)
	private Boolean isNutritionist = false;

	@Field(type = FieldType.Text)
	private String RegistrationImageUrl;
	@Field(type = FieldType.Text)
	private String RegistrationThumbnailUrl;

	@Field(type = FieldType.Text)
	private String photoIdImageUrl;

	@Field(type = FieldType.Boolean)
	private Boolean isRegistrationDetailsVerified = false;

	@Field(type = FieldType.Boolean)
	private Boolean isPhotoIdVerified = false;

	@Field(type = FieldType.Nested)
	private List<DoctorConsultation> consultationType;

	@Field(type = FieldType.Boolean)
	private Boolean isOnlineConsultationAvailable = false;

	@Field(type = FieldType.Nested)
	private List<WorkingSchedule> onlineWorkingSchedules;

	@Field(type = FieldType.Text)
	private String professionalStatement;

	@Field(type = FieldType.Nested)
	private List<Achievement> achievements;

	@Field(type = FieldType.Nested)
	private List<DoctorRegistrationDetail> registrationDetails;

	@Field(type = FieldType.Nested)
	private List<Education> education;

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> professionalMemberships;

	@Field(type = FieldType.Nested)
	private AppointmentSlot onlineConsultationSlot;

	@Field(type = FieldType.Boolean)
	private Boolean isHealthcocoDoctor = false;

	@Field(type = FieldType.Boolean)
	private Boolean isExpUpdated = false;
	@Field(type = FieldType.Double)
	private Double npsScore;

	public Boolean getIsNutritionist() {
		return isNutritionist;
	}

	public void setIsNutritionist(Boolean isNutritionist) {
		this.isNutritionist = isNutritionist;
	}

	public Double getNpsScore() {
		return npsScore;
	}

	public void setNpsScore(Double npsScore) {
		this.npsScore = npsScore;
	}

	public String getId() {
		return id;
	}

	public String getDoctorSlugURL() {
		return doctorSlugURL;
	}

	public void setDoctorSlugURL(String doctorSlugURL) {
		this.doctorSlugURL = doctorSlugURL;
	}

	public Boolean getIsDoctorListed() {
		return isDoctorListed;
	}

	public void setIsDoctorListed(Boolean isDoctorListed) {
		this.isDoctorListed = isDoctorListed;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public ConsultationFee getConsultationFee() {
		return consultationFee;
	}

	public void setConsultationFee(ConsultationFee consultationFee) {
		this.consultationFee = consultationFee;
	}

	public List<WorkingSchedule> getWorkingSchedules() {
		return workingSchedules;
	}

	public void setWorkingSchedules(List<WorkingSchedule> workingSchedules) {
		this.workingSchedules = workingSchedules;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public DoctorExperience getExperience() {
		return experience;
	}

	public void setExperience(DoctorExperience experience) {
		this.experience = experience;
	}

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public List<String> getAppointmentBookingNumber() {
		return appointmentBookingNumber;
	}

	public void setAppointmentBookingNumber(List<String> appointmentBookingNumber) {
		this.appointmentBookingNumber = appointmentBookingNumber;
	}

	public AppointmentSlot getAppointmentSlot() {
		return appointmentSlot;
	}

	public void setAppointmentSlot(AppointmentSlot appointmentSlot) {
		this.appointmentSlot = appointmentSlot;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	public String getCoverThumbnailImageUrl() {
		return coverThumbnailImageUrl;
	}

	public void setCoverThumbnailImageUrl(String coverThumbnailImageUrl) {
		this.coverThumbnailImageUrl = coverThumbnailImageUrl;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getUserState() {
		return userState;
	}

	public void setUserState(String userState) {
		this.userState = userState;
	}

	public String getRegisterNumber() {
		return registerNumber;
	}

	public void setRegisterNumber(String registerNumber) {
		this.registerNumber = registerNumber;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
		if (this.dob != null)
			dob.getAge();
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public String getUserUId() {
		return userUId;
	}

	public void setUserUId(String userUId) {
		this.userUId = userUId;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public long getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(long rankingCount) {
		this.rankingCount = rankingCount;
	}

	public Integer getNoOfRecommenations() {
		return noOfRecommenations;
	}

	public void setNoOfRecommenations(Integer noOfRecommenations) {
		this.noOfRecommenations = noOfRecommenations;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	public List<String> getSpecialitiesValue() {
		return specialitiesValue;
	}

	public void setSpecialitiesValue(List<String> specialitiesValue) {
		this.specialitiesValue = specialitiesValue;
	}

	public List<String> getServicesValue() {
		return servicesValue;
	}

	public void setServicesValue(List<String> servicesValue) {
		this.servicesValue = servicesValue;
	}

	public List<String> getParentSpecialities() {
		return parentSpecialities;
	}

	public void setParentSpecialities(List<String> parentSpecialities) {
		this.parentSpecialities = parentSpecialities;
	}

	public List<String> getFormattedParentSpecialities() {
		return formattedParentSpecialities;
	}

	public void setFormattedParentSpecialities(List<String> formattedParentSpecialities) {
		this.formattedParentSpecialities = formattedParentSpecialities;
	}

	public List<String> getFormattedSpecialitiesValue() {
		return formattedSpecialitiesValue;
	}

	public void setFormattedSpecialitiesValue(List<String> formattedSpecialitiesValue) {
		this.formattedSpecialitiesValue = formattedSpecialitiesValue;
	}

	public List<String> getFormattedServicesValue() {
		return formattedServicesValue;
	}

	public void setFormattedServicesValue(List<String> formattedServicesValue) {
		this.formattedServicesValue = formattedServicesValue;
	}

	public String getRegistrationImageUrl() {
		return RegistrationImageUrl;
	}

	public void setRegistrationImageUrl(String registrationImageUrl) {
		RegistrationImageUrl = registrationImageUrl;
	}

	public String getRegistrationThumbnailUrl() {
		return RegistrationThumbnailUrl;
	}

	public void setRegistrationThumbnailUrl(String registrationThumbnailUrl) {
		RegistrationThumbnailUrl = registrationThumbnailUrl;
	}

	public String getPhotoIdImageUrl() {
		return photoIdImageUrl;
	}

	public void setPhotoIdImageUrl(String photoIdImageUrl) {
		this.photoIdImageUrl = photoIdImageUrl;
	}

	public Boolean getIsRegistrationDetailsVerified() {
		return isRegistrationDetailsVerified;
	}

	public void setIsRegistrationDetailsVerified(Boolean isRegistrationDetailsVerified) {
		this.isRegistrationDetailsVerified = isRegistrationDetailsVerified;
	}

	public Boolean getIsPhotoIdVerified() {
		return isPhotoIdVerified;
	}

	public void setIsPhotoIdVerified(Boolean isPhotoIdVerified) {
		this.isPhotoIdVerified = isPhotoIdVerified;
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

	public List<WorkingSchedule> getOnlineWorkingSchedules() {
		return onlineWorkingSchedules;
	}

	public void setOnlineWorkingSchedules(List<WorkingSchedule> onlineWorkingSchedules) {
		this.onlineWorkingSchedules = onlineWorkingSchedules;
	}

	public List<Achievement> getAchievements() {
		return achievements;
	}

	public void setAchievements(List<Achievement> achievements) {
		this.achievements = achievements;
	}

	public List<DoctorRegistrationDetail> getRegistrationDetails() {
		return registrationDetails;
	}

	public void setRegistrationDetails(List<DoctorRegistrationDetail> registrationDetails) {
		this.registrationDetails = registrationDetails;
	}

	public List<Education> getEducation() {
		return education;
	}

	public void setEducation(List<Education> education) {
		this.education = education;
	}

	public String getProfessionalStatement() {
		return professionalStatement;
	}

	public void setProfessionalStatement(String professionalStatement) {
		this.professionalStatement = professionalStatement;
	}

	public List<String> getProfessionalMemberships() {
		return professionalMemberships;
	}

	public void setProfessionalMemberships(List<String> professionalMemberships) {
		this.professionalMemberships = professionalMemberships;
	}

	public Boolean getIsExpUpdated() {
		return isExpUpdated;
	}

	public void setIsExpUpdated(Boolean isExpUpdated) {
		this.isExpUpdated = isExpUpdated;
	}

	public Boolean getIsHealthcocoDoctor() {
		return isHealthcocoDoctor;
	}

	public void setIsHealthcocoDoctor(Boolean isHealthcocoDoctor) {
		this.isHealthcocoDoctor = isHealthcocoDoctor;
	}

	public AppointmentSlot getOnlineConsultationSlot() {
		return onlineConsultationSlot;
	}

	public void setOnlineConsultationSlot(AppointmentSlot onlineConsultationSlot) {
		this.onlineConsultationSlot = onlineConsultationSlot;
	}

	@Override
	public String toString() {
		return "ESDoctorDocument [id=" + id + ", userId=" + userId + ", firstName=" + firstName + ", gender=" + gender
				+ ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber + ", imageUrl=" + imageUrl
				+ ", thumbnailUrl=" + thumbnailUrl + ", consultationFee=" + consultationFee + ", workingSchedules="
				+ workingSchedules + ", specialities=" + specialities + ", services=" + services
				+ ", parentSpecialities=" + parentSpecialities + ", specialitiesValue=" + specialitiesValue
				+ ", servicesValue=" + servicesValue + ", experience=" + experience + ", facility=" + facility
				+ ", appointmentBookingNumber=" + appointmentBookingNumber + ", appointmentSlot=" + appointmentSlot
				+ ", isActive=" + isActive + ", isVerified=" + isVerified + ", coverImageUrl=" + coverImageUrl
				+ ", coverThumbnailImageUrl=" + coverThumbnailImageUrl + ", colorCode=" + colorCode + ", userState="
				+ userState + ", registerNumber=" + registerNumber + ", dob=" + dob + ", distance=" + distance
				+ ", userUId=" + userUId + ", timeZone=" + timeZone + ", isDoctorListed=" + isDoctorListed
				+ ", rankingCount=" + rankingCount + ", noOfRecommenations=" + noOfRecommenations + ", doctorSlugURL="
				+ doctorSlugURL + ", isNutritionist=" + isNutritionist + "]";
	}

}
