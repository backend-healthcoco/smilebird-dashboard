package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.enums.FollowUpType;

public class PatientCard extends GenericCollection {
    private String id;

    private String userId;

    private String firstName;
    
	private String localPatientName;

    private String userName;

    private String emailAddress;

    private String imageUrl;

    private String thumbnailUrl;

    private String bloodGroup;

    private String PID;

    private String gender;

    private String mobileNumber;

    private String secPhoneNumber;

    private DOB dob;

    private int count;

    private Long dateOfVisit;

    private List<Group> groups;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private String doctorSepecificPatientId;

    private String colorCode;

    private Boolean isDataAvailableWithOtherDoctor = false;

    private Boolean isPatientOTPVerified = false;
    
	private String referredBy;

	// @Transient
	private User user;

	private List<PatientGroupCollection> patientGroupCollections;

	private Address address;

	private Reference reference;

	private String patientId;
	private String profession;

	private List<Relations> relations;

	private List<String> consultantDoctorIds;

	private Long registrationDate;

	private List<QuestionAnswers> medicalQuestionAnswers;

	private List<QuestionAnswers> lifestyleQuestionAnswers;

	private PersonalInformation personalInformation;

	private String PNUM;

	private PatientCollection patient;

	private Boolean isChild = false;

	private String fatherName;

	private String motherName;
		
	private String backendPatientId;
	
    private Date followUp;
	
	private List<String> platform;

	private String followUpReason;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getUserName() {
	return userName;
    }

    public void setUserName(String userName) {
	this.userName = userName;
    }

    public String getEmailAddress() {
	return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
    }

    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public String getBloodGroup() {
	return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
	this.bloodGroup = bloodGroup;
    }

    public String getPID() {
	return PID;
    }

    public void setPID(String pID) {
	PID = pID;
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

    public String getSecPhoneNumber() {
	return secPhoneNumber;
    }

    public void setSecPhoneNumber(String secPhoneNumber) {
	this.secPhoneNumber = secPhoneNumber;
    }

    public DOB getDob() {
	return dob;
    }

    public void setDob(DOB dob) {
	this.dob = dob;
    }

    public int getCount() {
	return count;
    }

    public void setCount(int count) {
	this.count = count;
    }

    public Long getDateOfVisit() {
	return dateOfVisit;
    }

    public void setDateOfVisit(Long dateOfVisit) {
	this.dateOfVisit = dateOfVisit;
    }

    public List<Group> getGroups() {
	return groups;
    }

    public void setGroups(List<Group> groups) {
	this.groups = groups;
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

    public String getDoctorSepecificPatientId() {
	return doctorSepecificPatientId;
    }

    public void setDoctorSepecificPatientId(String doctorSepecificPatientId) {
	this.doctorSepecificPatientId = doctorSepecificPatientId;
    }

    public String getColorCode() {
	return colorCode;
    }

    public void setColorCode(String colorCode) {
	this.colorCode = colorCode;
    }

    public String getThumbnailUrl() {
	return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
	this.thumbnailUrl = thumbnailUrl;
    }

    public Boolean getIsDataAvailableWithOtherDoctor() {
	return isDataAvailableWithOtherDoctor;
    }

    public void setIsDataAvailableWithOtherDoctor(Boolean isDataAvailableWithOtherDoctor) {
	this.isDataAvailableWithOtherDoctor = isDataAvailableWithOtherDoctor;
    }

    public Boolean getIsPatientOTPVerified() {
	return isPatientOTPVerified;
    }

    public void setIsPatientOTPVerified(Boolean isPatientOTPVerified) {
	this.isPatientOTPVerified = isPatientOTPVerified;
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	
	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	
	public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<PatientGroupCollection> getPatientGroupCollections() {
		return patientGroupCollections;
	}

	public void setPatientGroupCollections(List<PatientGroupCollection> patientGroupCollections) {
		this.patientGroupCollections = patientGroupCollections;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public List<Relations> getRelations() {
		return relations;
	}

	public void setRelations(List<Relations> relations) {
		this.relations = relations;
	}

	public List<String> getConsultantDoctorIds() {
		return consultantDoctorIds;
	}

	public void setConsultantDoctorIds(List<String> consultantDoctorIds) {
		this.consultantDoctorIds = consultantDoctorIds;
	}

	public Long getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Long registrationDate) {
		this.registrationDate = registrationDate;
	}

	public List<QuestionAnswers> getMedicalQuestionAnswers() {
		return medicalQuestionAnswers;
	}

	public void setMedicalQuestionAnswers(List<QuestionAnswers> medicalQuestionAnswers) {
		this.medicalQuestionAnswers = medicalQuestionAnswers;
	}

	public List<QuestionAnswers> getLifestyleQuestionAnswers() {
		return lifestyleQuestionAnswers;
	}

	public void setLifestyleQuestionAnswers(List<QuestionAnswers> lifestyleQuestionAnswers) {
		this.lifestyleQuestionAnswers = lifestyleQuestionAnswers;
	}

	public PersonalInformation getPersonalInformation() {
		return personalInformation;
	}

	public void setPersonalInformation(PersonalInformation personalInformation) {
		this.personalInformation = personalInformation;
	}

	public String getPNUM() {
		return PNUM;
	}

	public void setPNUM(String pNUM) {
		PNUM = pNUM;
	}

	public PatientCollection getPatient() {
		return patient;
	}

	public void setPatient(PatientCollection patient) {
		this.patient = patient;
	}

	public Boolean getIsChild() {
		return isChild;
	}

	public void setIsChild(Boolean isChild) {
		this.isChild = isChild;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getBackendPatientId() {
		return backendPatientId;
	}

	public void setBackendPatientId(String backendPatientId) {
		this.backendPatientId = backendPatientId;
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

	@Override
	public String toString() {
		return "PatientCard [id=" + id + ", userId=" + userId + ", firstName=" + firstName + ", userName=" + userName
				+ ", emailAddress=" + emailAddress + ", imageUrl=" + imageUrl + ", thumbnailUrl=" + thumbnailUrl
				+ ", bloodGroup=" + bloodGroup + ", PID=" + PID + ", gender=" + gender + ", mobileNumber="
				+ mobileNumber + ", secPhoneNumber=" + secPhoneNumber + ", dob=" + dob + ", count=" + count
				+ ", dateOfVisit=" + dateOfVisit + ", groups=" + groups + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", doctorSepecificPatientId=" + doctorSepecificPatientId
				+ ", colorCode=" + colorCode + ", isDataAvailableWithOtherDoctor=" + isDataAvailableWithOtherDoctor
				+ ", isPatientOTPVerified=" + isPatientOTPVerified + "]";
	}
}
