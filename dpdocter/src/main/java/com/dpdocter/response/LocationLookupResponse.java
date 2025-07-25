package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.LabAssociation;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.collections.GenericCollection;

public class LocationLookupResponse extends GenericCollection {

	private String id;

	private String locationName;

	private String country;

	private String state;

	private String city;

	private String postalCode;

	private String websiteUrl;

	private List<ClinicImage> images;

	private String logoUrl;

	private String logoThumbnailUrl;

	private String hospitalId;

	private Double latitude;

	private Double longitude;

	private String tagLine;

	private String landmarkDetails;

	private String locationEmailAddress;

	private List<String> specialization;

	private String streetAddress;

	private String locality;

	private String clinicNumber;

	private List<String> alternateClinicNumbers;

	private List<WorkingSchedule> clinicWorkingSchedules;

	private boolean isTwentyFourSevenOpen;

	private Boolean isClinic = true;

	private Boolean isLab = false;

	private Boolean isOnlineReportsAvailable = false;

	private Boolean isNABLAccredited = false;

	private Boolean isHomeServiceAvailable = false;

	private String locationUId;

	private String clinicAddress;

	private Boolean isActivate = false;

	private String patientInitial = "P";

	private int patientCounter = 1;

	private Boolean isLocationListed = false;

	private long clinicRankingCount = 0;

	private int noOfClinicRecommendations = 0;

	private Integer noOfClinicReview = 0;

	private Boolean isParent = false;

	private LabAssociation labAssociation;

	private String smsCode;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getLogoThumbnailUrl() {
		return logoThumbnailUrl;
	}

	public void setLogoThumbnailUrl(String logoThumbnailUrl) {
		this.logoThumbnailUrl = logoThumbnailUrl;
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

	public Boolean getIsClinic() {
		return isClinic;
	}

	public void setIsClinic(Boolean isClinic) {
		this.isClinic = isClinic;
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

	public String getLocationUId() {
		return locationUId;
	}

	public void setLocationUId(String locationUId) {
		this.locationUId = locationUId;
	}

	public String getClinicAddress() {
		return clinicAddress;
	}

	public void setClinicAddress(String clinicAddress) {
		this.clinicAddress = clinicAddress;
	}

	public Boolean getIsActivate() {
		return isActivate;
	}

	public void setIsActivate(Boolean isActivate) {
		this.isActivate = isActivate;
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

	public Boolean getIsLocationListed() {
		return isLocationListed;
	}

	public void setIsLocationListed(Boolean isLocationListed) {
		this.isLocationListed = isLocationListed;
	}

	public long getClinicRankingCount() {
		return clinicRankingCount;
	}

	public void setClinicRankingCount(long clinicRankingCount) {
		this.clinicRankingCount = clinicRankingCount;
	}

	public int getNoOfClinicRecommendations() {
		return noOfClinicRecommendations;
	}

	public void setNoOfClinicRecommendations(int noOfClinicRecommendations) {
		this.noOfClinicRecommendations = noOfClinicRecommendations;
	}

	public Integer getNoOfClinicReview() {
		return noOfClinicReview;
	}

	public void setNoOfClinicReview(Integer noOfClinicReview) {
		this.noOfClinicReview = noOfClinicReview;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public LabAssociation getLabAssociation() {
		return labAssociation;
	}

	public void setLabAssociation(LabAssociation labAssociation) {
		this.labAssociation = labAssociation;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	@Override
	public String toString() {
		return "LocationLookupResponse [id=" + id + ", locationName=" + locationName + ", country=" + country
				+ ", state=" + state + ", city=" + city + ", postalCode=" + postalCode + ", websiteUrl=" + websiteUrl
				+ ", images=" + images + ", logoUrl=" + logoUrl + ", logoThumbnailUrl=" + logoThumbnailUrl
				+ ", hospitalId=" + hospitalId + ", latitude=" + latitude + ", longitude=" + longitude + ", tagLine="
				+ tagLine + ", landmarkDetails=" + landmarkDetails + ", locationEmailAddress=" + locationEmailAddress
				+ ", specialization=" + specialization + ", streetAddress=" + streetAddress + ", locality=" + locality
				+ ", clinicNumber=" + clinicNumber + ", alternateClinicNumbers=" + alternateClinicNumbers
				+ ", clinicWorkingSchedules=" + clinicWorkingSchedules + ", isTwentyFourSevenOpen="
				+ isTwentyFourSevenOpen + ", isClinic=" + isClinic + ", isLab=" + isLab + ", isOnlineReportsAvailable="
				+ isOnlineReportsAvailable + ", isNABLAccredited=" + isNABLAccredited + ", isHomeServiceAvailable="
				+ isHomeServiceAvailable + ", locationUId=" + locationUId + ", clinicAddress=" + clinicAddress
				+ ", isActivate=" + isActivate + ", patientInitial=" + patientInitial + ", patientCounter="
				+ patientCounter + ", isLocationListed=" + isLocationListed + ", clinicRankingCount="
				+ clinicRankingCount + ", noOfClinicRecommendations=" + noOfClinicRecommendations
				+ ", noOfClinicReview=" + noOfClinicReview + ", isParent=" + isParent + ", labAssociation="
				+ labAssociation + "]";
	}

}
