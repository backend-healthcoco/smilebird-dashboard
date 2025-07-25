package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.WorkingSchedule;

@Document(collection = "location_cl")
public class LocationCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String locationName;

	@Field
	private String country;

	@Field
	private String state;

	@Field
	private String city;

	@Field
	private String postalCode;

	@Field
	private String websiteUrl;

	@Field
	private List<ClinicImage> images;

	@Field
	private String logoUrl;

	@Field
	private String logoThumbnailUrl;

	@Field
	private ObjectId hospitalId;

	@Field
	private Double latitude;

	@Field
	private Double longitude;

	@Field
	private String tagLine;

	@Field
	private String landmarkDetails;

	@Field
	private String locationEmailAddress;

	@Field
	private List<String> specialization;

	@Field
	private String streetAddress;

	@Field
	private String locality;
	
	@Field
	private String zone;

	@Field
	private String clinicNumber;

	@Field
	private List<String> alternateClinicNumbers;

	@Field
	private List<WorkingSchedule> clinicWorkingSchedules;

	@Field
	private boolean isTwentyFourSevenOpen;

	@Field
	private Boolean isClinic = true;

	@Field
	private Boolean isLab = false;

	@Field
	private Boolean isHospital = false;

	@Field
	private Boolean isOnlineReportsAvailable = false;

	@Field
	private Boolean isNABLAccredited = false;

	@Field
	private Boolean isHomeServiceAvailable = false;

	@Field
	private String locationUId;

	@Field
	private Boolean isActivate = false;

	@Field
	private String patientInitial = "P";

	@Field
	private int patientCounter = 1;

	@Field
	private Boolean isLocationListed = false;

	@Field
	private long clinicRankingCount = 0;

	@Field
	private int noOfClinicRecommendations = 0;

	@Field
	private Integer noOfClinicReview = 0;

	@Field
	private Boolean isParent = false;

	@Field
	private List<ObjectId> associatedLabs;

	@Field
	private String locationSlugUrl;

	@Field
	private Boolean isDentalWorksLab = false;

	@Field
	private Boolean isDentalImagingLab = false;

	@Field
	private Boolean isMobileNumberOptional = false;

	@Field
	private String googleMapShortUrl;

	@Field
	private Boolean isPatientWelcomeMessageOn = false;

	@Field
	private String smsCode;

	@Field
	private Boolean isDentalChain = false;

	@Field
	private ObjectId ratelistId;
	@Field
	private String chargeCode;
	
	@Field
	private Double npsScore;
	public Boolean getIsDentalChain() {
		return isDentalChain;
	}

	public void setIsDentalChain(Boolean isDentalChain) {
		this.isDentalChain = isDentalChain;
	}

	public String getLocationSlugUrl() {
		return locationSlugUrl;
	}

	public void setLocationSlugUrl(String locationSlugUrl) {
		this.locationSlugUrl = locationSlugUrl;
	}

	public int getNoOfClinicReview() {
		return noOfClinicReview;
	}

	public void setNoOfClinicReview(int noOfClinicReview) {
		this.noOfClinicReview = noOfClinicReview;
	}

	public int getNoOfClinicRecommendations() {
		return noOfClinicRecommendations;
	}

	public void setNoOfClinicRecommendations(int noOfClinicRecommendations) {
		this.noOfClinicRecommendations = noOfClinicRecommendations;
	}

	public void setNoOfClinicReview(Integer noOfClinicReview) {
		this.noOfClinicReview = noOfClinicReview;
	}

	public ObjectId getId() {
		return id;
	}

	public Boolean getIsLocationListed() {
		return isLocationListed;
	}

	public void setIsLocationListed(Boolean isLocationListed) {
		this.isLocationListed = isLocationListed;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
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

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
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

	public String getTagLine() {
		return tagLine;
	}

	public void setTagLine(String tagLine) {
		this.tagLine = tagLine;
	}

	public String getLandmarkDetails() {
		return landmarkDetails;
	}

	public void setLandmarkDetails(String landmarkDetails) {
		this.landmarkDetails = landmarkDetails;
	}

	public String getLocationEmailAddress() {
		return locationEmailAddress;
	}

	public void setLocationEmailAddress(String locationEmailAddress) {
		this.locationEmailAddress = locationEmailAddress;
	}

	public List<String> getSpecialization() {
		return specialization;
	}

	public void setSpecialization(List<String> specialization) {
		this.specialization = specialization;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public List<WorkingSchedule> getClinicWorkingSchedules() {
		return clinicWorkingSchedules;
	}

	public void setClinicWorkingSchedules(List<WorkingSchedule> clinicWorkingSchedules) {
		this.clinicWorkingSchedules = clinicWorkingSchedules;
	}

	public boolean isTwentyFourSevenOpen() {
		return isTwentyFourSevenOpen;
	}

	public void setTwentyFourSevenOpen(boolean isTwentyFourSevenOpen) {
		this.isTwentyFourSevenOpen = isTwentyFourSevenOpen;
	}

	public String getLogoThumbnailUrl() {
		return logoThumbnailUrl;
	}

	public void setLogoThumbnailUrl(String logoThumbnailUrl) {
		this.logoThumbnailUrl = logoThumbnailUrl;
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

	public String getClinicNumber() {
		return clinicNumber;
	}

	public void setClinicNumber(String clinicNumber) {
		this.clinicNumber = clinicNumber;
	}

	public List<String> getAlternateClinicNumbers() {
		return alternateClinicNumbers;
	}

	public void setAlternateClinicNumbers(List<String> alternateClinicNumbers) {
		this.alternateClinicNumbers = alternateClinicNumbers;
	}

	public String getLocationUId() {
		return locationUId;
	}

	public void setLocationUId(String locationUId) {
		this.locationUId = locationUId;
	}

	public Boolean getIsActivate() {
		return isActivate;
	}

	public void setIsActivate(Boolean isActivate) {
		this.isActivate = isActivate;
	}

	public long getClinicRankingCount() {
		return clinicRankingCount;
	}

	public void setClinicRankingCount(long clinicRankingCount) {
		this.clinicRankingCount = clinicRankingCount;
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

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public List<ObjectId> getAssociatedLabs() {
		return associatedLabs;
	}

	public void setAssociatedLabs(List<ObjectId> associatedLabs) {
		this.associatedLabs = associatedLabs;
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

	public Boolean getIsMobileNumberOptional() {
		return isMobileNumberOptional;
	}

	public void setIsMobileNumberOptional(Boolean isMobileNumberOptional) {
		this.isMobileNumberOptional = isMobileNumberOptional;
	}

	public String getGoogleMapShortUrl() {
		return googleMapShortUrl;
	}

	public void setGoogleMapShortUrl(String googleMapShortUrl) {
		this.googleMapShortUrl = googleMapShortUrl;
	}

	public Boolean getIsPatientWelcomeMessageOn() {
		return isPatientWelcomeMessageOn;
	}

	public void setIsPatientWelcomeMessageOn(Boolean isPatientWelcomeMessageOn) {
		this.isPatientWelcomeMessageOn = isPatientWelcomeMessageOn;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public ObjectId getRatelistId() {
		return ratelistId;
	}

	public void setRatelistId(ObjectId ratelistId) {
		this.ratelistId = ratelistId;
	}

	public Boolean getIsHospital() {
		return isHospital;
	}

	public void setIsHospital(Boolean isHospital) {
		this.isHospital = isHospital;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public Double getNpsScore() {
		return npsScore;
	}

	public void setNpsScore(Double npsScore) {
		this.npsScore = npsScore;
	}

	@Override
	public String toString() {
		return "LocationCollection [id=" + id + ", locationName=" + locationName + ", country=" + country + ", state="
				+ state + ", city=" + city + ", postalCode=" + postalCode + ", websiteUrl=" + websiteUrl + ", images="
				+ images + ", logoUrl=" + logoUrl + ", logoThumbnailUrl=" + logoThumbnailUrl + ", hospitalId="
				+ hospitalId + ", latitude=" + latitude + ", longitude=" + longitude + ", tagLine=" + tagLine
				+ ", landmarkDetails=" + landmarkDetails + ", locationEmailAddress=" + locationEmailAddress
				+ ", specialization=" + specialization + ", streetAddress=" + streetAddress + ", locality=" + locality
				+ ", clinicNumber=" + clinicNumber + ", alternateClinicNumbers=" + alternateClinicNumbers
				+ ", clinicWorkingSchedules=" + clinicWorkingSchedules + ", isTwentyFourSevenOpen="
				+ isTwentyFourSevenOpen + ", isClinic=" + isClinic + ", isLab=" + isLab + ", isOnlineReportsAvailable="
				+ isOnlineReportsAvailable + ", isNABLAccredited=" + isNABLAccredited + ", isHomeServiceAvailable="
				+ isHomeServiceAvailable + ", locationUId=" + locationUId + ", isActivate=" + isActivate
				+ ", patientInitial=" + patientInitial + ", patientCounter=" + patientCounter + ", isLocationListed="
				+ isLocationListed + ", clinicRankingCount=" + clinicRankingCount + ", noOfClinicRecommendations="
				+ noOfClinicRecommendations + ", noOfClinicReview=" + noOfClinicReview + ", isParent=" + isParent
				+ ", associatedLabs=" + associatedLabs + ", locationSlugUrl=" + locationSlugUrl + ", isDentalWorksLab="
				+ isDentalWorksLab + ", isDentalImagingLab=" + isDentalImagingLab + ", isMobileNumberOptional="
				+ isMobileNumberOptional + "]";
	}

}
