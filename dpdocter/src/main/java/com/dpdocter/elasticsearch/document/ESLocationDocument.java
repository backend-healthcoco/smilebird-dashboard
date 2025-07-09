package com.dpdocter.elasticsearch.document;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import com.dpdocter.beans.WorkingSchedule;

@Document(indexName = "locations_in", type = "locations")
public class ESLocationDocument {

	@Id
	private String id;

	@Field(type = FieldType.Text)
	private String locationId;

	@Field(type = FieldType.Text)
	private String hospitalId;

	@Field(type = FieldType.Text)
	private String locationName;

	@Field(type = FieldType.Text)
	private String country;

	@Field(type = FieldType.Text)
	private String state;

	@Field(type = FieldType.Text)
	private String city;

	@Field(type = FieldType.Text)
	private String postalCode;

	@Field(type = FieldType.Text)
	private String websiteUrl;

	@GeoPointField
	private GeoPoint geoPoint;

	@Field(type = FieldType.Double)
	private Double latitude;

	@Field(type = FieldType.Double)
	private Double longitude;

	@Field(type = FieldType.Text)
	private String callingNumber;

	@Field(type = FieldType.Text)
	private String landmarkDetails;

	@Field(type = FieldType.Text)
	private String locationEmailAddress;

	@Field(type = FieldType.Text)
	private String streetAddress;

	@Field(type = FieldType.Text)
	private String locality;

	@Field(type = FieldType.Text)
	private String clinicNumber;

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> alternateClinicNumbers;

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> specialization;

	@Field(type = FieldType.Boolean)
	private Boolean isClinic = true;

	@Field(type = FieldType.Boolean)
	private Boolean isLab = false;

	@Field(type = FieldType.Boolean)
	private Boolean isHospital = false;

	@Field(type = FieldType.Boolean)
	private Boolean isOnlineReportsAvailable = false;

	@Field(type = FieldType.Boolean)
	private Boolean isNABLAccredited = false;

	@Field(type = FieldType.Boolean)
	private Boolean isHomeServiceAvailable = false;

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> images;

	@Field(type = FieldType.Text)
	private String logoUrl;

	@Field(type = FieldType.Integer)
	private Integer noOfReviews = 0;

	@Field(type = FieldType.Integer)
	private Integer noOfRecommenations = 0;

	@Field(type = FieldType.Text)
	private String locationUId;

	@Field(type = FieldType.Nested)
	private List<WorkingSchedule> clinicWorkingSchedules;

	@Field(type = FieldType.Boolean)
	private Boolean isLocationListed = false;

	@Transient
	private String clinicAddress;

	@Field(type = FieldType.Long)
	private long clinicRankingCount = 0;

	@Field(type = FieldType.Integer)
	private int noOfClinicRecommendations = 0;

	@Field(type = FieldType.Boolean)
	private Boolean isActivate = false;

	@Field(type = FieldType.Integer)
	private Integer noOfClinicReview = 0;

	@Field(type = FieldType.Text)
	private String locationSlugUrl;

	@Field(type = FieldType.Boolean)
	private Boolean isDentalChain = false;
	
	@Field(type = FieldType.Text)
	private String chargeCode;

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

	public String getCallingNumber() {
		return callingNumber;
	}

	public void setCallingNumber(String callingNumber) {
		this.callingNumber = callingNumber;
	}

	public Integer getNoOfClinicReview() {
		return noOfClinicReview;
	}

	public void setNoOfClinicReview(Integer noOfClinicReview) {
		this.noOfClinicReview = noOfClinicReview;
	}

	int getNoOfClinicRecommendations() {
		return noOfClinicRecommendations;
	}

	public void setNoOfClinicRecommendations(int noOfClinicRecommendations) {
		this.noOfClinicRecommendations = noOfClinicRecommendations;
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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
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

	public GeoPoint getGeoPoint() {
		return geoPoint;
	}

	public void setGeoPoint(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
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

	public List<String> getSpecialization() {
		return specialization;
	}

	public void setSpecialization(List<String> specialization) {
		this.specialization = specialization;
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

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
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

	public String getLocationUId() {
		return locationUId;
	}

	public void setLocationUId(String locationUId) {
		this.locationUId = locationUId;
	}

	public List<WorkingSchedule> getClinicWorkingSchedules() {
		return clinicWorkingSchedules;
	}

	public void setClinicWorkingSchedules(List<WorkingSchedule> clinicWorkingSchedules) {
		this.clinicWorkingSchedules = clinicWorkingSchedules;
	}

	public Boolean getIsLocationListed() {
		return isLocationListed;
	}

	public void setIsLocationListed(Boolean isLocationListed) {
		this.isLocationListed = isLocationListed;
	}

	public String getClinicAddress() {
		return clinicAddress;
	}

	public void setClinicAddress(String clinicAddress) {
		this.clinicAddress = clinicAddress;
	}

	public long getClinicRankingCount() {
		return clinicRankingCount;
	}

	public void setClinicRankingCount(long clinicRankingCount) {
		this.clinicRankingCount = clinicRankingCount;
	}

	public Boolean getIsActivate() {
		return isActivate;
	}

	public void setIsActivate(Boolean isActivate) {
		this.isActivate = isActivate;
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

	@Override
	public String toString() {
		return "ESLocationDocument [id=" + id + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", locationName=" + locationName + ", country=" + country + ", state=" + state + ", city=" + city
				+ ", postalCode=" + postalCode + ", websiteUrl=" + websiteUrl + ", geoPoint=" + geoPoint + ", latitude="
				+ latitude + ", longitude=" + longitude + ", landmarkDetails=" + landmarkDetails
				+ ", locationEmailAddress=" + locationEmailAddress + ", streetAddress=" + streetAddress + ", locality="
				+ locality + ", clinicNumber=" + clinicNumber + ", alternateClinicNumbers=" + alternateClinicNumbers
				+ ", specialization=" + specialization + ", isClinic=" + isClinic + ", isLab=" + isLab
				+ ", isOnlineReportsAvailable=" + isOnlineReportsAvailable + ", isNABLAccredited=" + isNABLAccredited
				+ ", isHomeServiceAvailable=" + isHomeServiceAvailable + ", images=" + images + ", logoUrl=" + logoUrl
				+ ", noOfReviews=" + noOfReviews + ", noOfRecommenations=" + noOfRecommenations + ", locationUId="
				+ locationUId + ", clinicWorkingSchedules=" + clinicWorkingSchedules + ", isLocationListed="
				+ isLocationListed + ", clinicAddress=" + clinicAddress + ", clinicRankingCount=" + clinicRankingCount
				+ ", noOfClinicRecommendations=" + noOfClinicRecommendations + ", isActivate=" + isActivate
				+ ", noOfClinicReview=" + noOfClinicReview + "]";
	}

}
