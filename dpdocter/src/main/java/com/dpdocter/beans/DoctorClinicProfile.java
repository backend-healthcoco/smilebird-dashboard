package com.dpdocter.beans;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.ConsultationType;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.enums.LabType;
import com.dpdocter.enums.PackageType;

public class DoctorClinicProfile {
	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String clinicAddress;

	private String locationName;

	private String country;

	private String state;

	private String city;

	private String postalCode;

	private Double latitude;

	private Double longitude;

	private String patientInitial = "P";

	private int patientCounter = 0;

	private List<String> appointmentBookingNumber;

	private ConsultationFee consultationFee;

	private AppointmentSlot appointmentSlot;

	private List<WorkingSchedule> workingSchedules;

	private DoctorFacility facility;

	private List<ClinicImage> images;

	private String logoUrl;

	private String logoThumbnailUrl;

	private Integer noOfReviews = 0;

	private Integer noOfRecommenations = 0;

	private Boolean isClinic = true;

	private Boolean isLab = false;

	private Boolean isParent = false;

	private Boolean isOnlineReportsAvailable = false;

	private Boolean isNABLAccredited = false;

	private Boolean isHomeServiceAvailable = false;

	private String locality;

	private String timeZone = "IST";

	private Boolean isDoctorListed = false;

	private long rankingCount = 0;

	private String packageType = PackageType.BASIC.getType();

	private String doctorSlugURL;

	private String labType = LabType.DIAGNOSTIC.getType();

	private Boolean isDentalWorksLab = false;

	private Boolean isDentalImagingLab = false;

	private Boolean iskiosk = false;
	
	private Boolean isBulkSMSOn = true;

	private Boolean isNutritionist = false;

	private Boolean isSuperAdmin = false;
	
	


	private String mrCode;

	private List<ObjectId> divisionIds;

	private String cityId;

	private Boolean isVaccinationModuleOn = false;

	private String feedbackURL;

	private Boolean isAdminNutritionist = false;
	
	private List<WorkingSchedule> onlineWorkingSchedules;
	
	private Boolean isSendBirthdaySMS = false;
	
//	private Map<DoctorConsultation, String> onlineConsultationFees;
	
	private List<DoctorConsultation> consultationType;
	
	private String clinicOwnershipImageUrl;
	
	private Boolean isClinicOwnershipVerified=false;
	
	private Boolean isOnlineConsultationAvailable = false;
	
	private AppointmentSlot onlineConsultationSlot;
	
	private Boolean isRegisteredNDHMFacility = false;
	
//	private BulkSmsCredits bulkSmsCredit;
	private Boolean isPatientWelcomeMessageOn = false;
	
	public Boolean getIsSuperAdmin() {
		return isSuperAdmin;
	}

	public void setIsSuperAdmin(Boolean isSuperAdmin) {
		this.isSuperAdmin = isSuperAdmin;
	}

	public Boolean getIsNutritionist() {
		return isNutritionist;
	}

	public void setIsNutritionist(Boolean isNutritionist) {
		this.isNutritionist = isNutritionist;
	}

	public Boolean getIskiosk() {
		return iskiosk;
	}

	public void setIskiosk(Boolean iskiosk) {
		this.iskiosk = iskiosk;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getClinicAddress() {
		return clinicAddress;
	}

	public void setClinicAddress(String clinicAddress) {
		this.clinicAddress = clinicAddress;
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

	public List<ClinicImage> getImages() {
		return images;
	}

	public void setImages(List<ClinicImage> images) {
		this.images = images;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getLogoThumbnailUrl() {
		return logoThumbnailUrl;
	}

	public void setLogoThumbnailUrl(String logoThumbnailUrl) {
		this.logoThumbnailUrl = logoThumbnailUrl;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
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

	public Boolean getIsLab() {
		return isLab;
	}

	public void setIsLab(Boolean isLab) {
		this.isLab = isLab;
	}

	public Boolean getIsOnlineReportsAvailable() {
		return isOnlineReportsAvailable;
	}

	public void setIsOnlineReportsAvailable(Boolean isOnlineReportsAvailable) {
		this.isOnlineReportsAvailable = isOnlineReportsAvailable;
	}

	public Boolean getIsNABLAccredited() {
		return isNABLAccredited;
	}

	public void setIsNABLAccredited(Boolean isNABLAccredited) {
		this.isNABLAccredited = isNABLAccredited;
	}

	public Boolean getIsHomeServiceAvailable() {
		return isHomeServiceAvailable;
	}

	public void setIsHomeServiceAvailable(Boolean isHomeServiceAvailable) {
		this.isHomeServiceAvailable = isHomeServiceAvailable;
	}

	public Boolean getIsClinic() {
		return isClinic;
	}

	public void setIsClinic(Boolean isClinic) {
		this.isClinic = isClinic;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
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

	public Boolean getIsDentalWorksLab() {
		return isDentalWorksLab;
	}

	public void setIsDentalWorksLab(Boolean isDentalWorksLab) {
		this.isDentalWorksLab = isDentalWorksLab;
	}

	public Boolean getIsDentalImagingLab() {
		return isDentalImagingLab;
	}

	public void setIsDentalImagingLab(Boolean isDentalImagingLab) {
		this.isDentalImagingLab = isDentalImagingLab;
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

	public Boolean getIsBulkSMSOn() {
		return isBulkSMSOn;
	}

	public void setIsBulkSMSOn(Boolean isBulkSMSOn) {
		this.isBulkSMSOn = isBulkSMSOn;
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

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
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

	public Boolean getIsAdminNutritionist() {
		return isAdminNutritionist;
	}

	public void setIsAdminNutritionist(Boolean isAdminNutritionist) {
		this.isAdminNutritionist = isAdminNutritionist;
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

	

	public String getClinicOwnershipImageUrl() {
		return clinicOwnershipImageUrl;
	}

	public void setClinicOwnershipImageUrl(String clinicOwnershipImageUrl) {
		this.clinicOwnershipImageUrl = clinicOwnershipImageUrl;
	}

	public Boolean getIsOnlineConsultationAvailable() {
		return isOnlineConsultationAvailable;
	}

	public void setIsOnlineConsultationAvailable(Boolean isOnlineConsultationAvailable) {
		this.isOnlineConsultationAvailable = isOnlineConsultationAvailable;
	}
	
	
	
	

	

	public AppointmentSlot getOnlineConsultationSlot() {
		return onlineConsultationSlot;
	}

	public void setOnlineConsultationSlot(AppointmentSlot onlineConsultationSlot) {
		this.onlineConsultationSlot = onlineConsultationSlot;
	}

	public Boolean getIsSendBirthdaySMS() {
		return isSendBirthdaySMS;
	}

	public void setIsSendBirthdaySMS(Boolean isSendBirthdaySMS) {
		this.isSendBirthdaySMS = isSendBirthdaySMS;
	}

	public Boolean getIsClinicOwnershipVerified() {
		return isClinicOwnershipVerified;
	}

	public void setIsClinicOwnershipVerified(Boolean isClinicOwnershipVerified) {
		this.isClinicOwnershipVerified = isClinicOwnershipVerified;
	}
	
	

	public Boolean getIsRegisteredNDHMFacility() {
		return isRegisteredNDHMFacility;
	}

	public void setIsRegisteredNDHMFacility(Boolean isRegisteredNDHMFacility) {
		this.isRegisteredNDHMFacility = isRegisteredNDHMFacility;
	}
	public Boolean getIsPatientWelcomeMessageOn() {
		return isPatientWelcomeMessageOn;
	}

	public void setIsPatientWelcomeMessageOn(Boolean isPatientWelcomeMessageOn) {
		this.isPatientWelcomeMessageOn = isPatientWelcomeMessageOn;
	}

	@Override
	public String toString() {
		return "DoctorClinicProfile [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", clinicAddress=" + clinicAddress + ", locationName=" + locationName
				+ ", country=" + country + ", state=" + state + ", city=" + city + ", postalCode=" + postalCode
				+ ", latitude=" + latitude + ", longitude=" + longitude + ", patientInitial=" + patientInitial
				+ ", patientCounter=" + patientCounter + ", appointmentBookingNumber=" + appointmentBookingNumber
				+ ", consultationFee=" + consultationFee + ", appointmentSlot=" + appointmentSlot
				+ ", workingSchedules=" + workingSchedules + ", facility=" + facility + ", images=" + images
				+ ", logoUrl=" + logoUrl + ", logoThumbnailUrl=" + logoThumbnailUrl + ", noOfReviews=" + noOfReviews
				+ ", noOfRecommenations=" + noOfRecommenations + ", isClinic=" + isClinic + ", isLab=" + isLab
				+ ", isParent=" + isParent + ", isOnlineReportsAvailable=" + isOnlineReportsAvailable
				+ ", isNABLAccredited=" + isNABLAccredited + ", isHomeServiceAvailable=" + isHomeServiceAvailable
				+ ", locality=" + locality + ", timeZone=" + timeZone + ", isDoctorListed=" + isDoctorListed
				+ ", rankingCount=" + rankingCount + ", packageType=" + packageType + ", doctorSlugURL=" + doctorSlugURL
				+ ", labType=" + labType + ", isDentalWorksLab=" + isDentalWorksLab + ", isDentalImagingLab="
				+ isDentalImagingLab + ", iskiosk=" + iskiosk + ", isBulkSMSOn=" + isBulkSMSOn + ", isNutritionist="
				+ isNutritionist + ", isSuperAdmin=" + isSuperAdmin + ", mrCode=" + mrCode + ", divisionIds="
				+ divisionIds + ", cityId=" + cityId + ", isVaccinationModuleOn=" + isVaccinationModuleOn
				+ ", feedbackURL=" + feedbackURL + "]";
	}

}
