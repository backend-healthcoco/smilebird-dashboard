package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.LocaleType;
import com.dpdocter.enums.UserState;

public class Locale extends GenericCollection {

	private String id;
	private String localeName;
	private String registeredOwnerName;
	private String licenseNumber;
	private List<LocaleImage> localeImages;
	private String logoUrl;
	private String logoThumbnailUrl;
	private String contactNumber;
	private List<String> alternateContactNumbers;
	private List<LocaleWorkingSchedule> localeWorkingSchedules;
	private Address address;
	private String localeAddress;
	private String websiteUrl;
	private String localeEmailAddress;
	private Boolean isTwentyFourSevenOpen = false;
	private String localeUId;
	private int openSince;
	private UserState userState;
	private Boolean isActivate = false;
	private Boolean isVerified = false;
	private Boolean isLocaleListed = false;
	private long localeRankingCount = 0;
	private long noOfLocaleRecommendation = 0;
	private Boolean isHomeDeliveryAvailable = false;
	private double homeDeliveryRadius;
	private String paymentInfo;
	private String localeType = LocaleType.PHARMACY.getType();
	private Boolean isPasswordVerified = false;
	private String callingNumber;
	private Boolean isAcceptRequest = true;
	private List<String> pharmacyType;
	private Boolean isGenericMedicineAvailable = false;
	private Double minimumAmountForDelivery = 0.0;
	private Integer totalRequest = 0;
	private Integer countForNoReply = 0;
	private Integer countForNo = 0;
	private Integer countForYes = 0;
	private List<String> paymentInfos;
	private String pharmacySlugUrl;

	public String getCallingNumber() {
		return callingNumber;
	}

	public void setCallingNumber(String callingNumber) {
		this.callingNumber = callingNumber;
	}

	public Integer getTotalRequest() {
		return totalRequest;
	}

	public List<String> getPaymentInfos() {
		return paymentInfos;
	}

	public void setPaymentInfos(List<String> paymentInfos) {
		this.paymentInfos = paymentInfos;
	}

	public Integer getCountForNoReply() {
		return countForNoReply;
	}

	public Integer getCountForNo() {
		return countForNo;
	}

	public Integer getCountForYes() {
		return countForYes;
	}

	public void setTotalRequest(Integer totalRequest) {
		this.totalRequest = totalRequest;
	}

	public void setCountForNoReply(Integer countForNoReply) {
		this.countForNoReply = countForNoReply;
	}

	public void setCountForNo(Integer countForNo) {
		this.countForNo = countForNo;
	}

	public void setCountForYes(Integer countForYes) {
		this.countForYes = countForYes;
	}

	public Boolean getIsLocaleListed() {
		return isLocaleListed;
	}

	public long getLocaleRankingCount() {
		return localeRankingCount;
	}

	public long getNoOfLocaleRecommendation() {
		return noOfLocaleRecommendation;
	}

	public List<String> getPharmacyType() {
		return pharmacyType;
	}

	public Boolean getIsGenericMedicineAvailable() {
		return isGenericMedicineAvailable;
	}

	public void setIsLocaleListed(Boolean isLocaleListed) {
		this.isLocaleListed = isLocaleListed;
	}

	public void setLocaleRankingCount(long localeRankingCount) {
		this.localeRankingCount = localeRankingCount;
	}

	public void setNoOfLocaleRecommendation(long noOfLocaleRecommendation) {
		this.noOfLocaleRecommendation = noOfLocaleRecommendation;
	}

	public void setPharmacyType(List<String> pharmacyType) {
		this.pharmacyType = pharmacyType;
	}

	public void setIsGenericMedicineAvailable(Boolean isGenericMedicineAvailable) {
		this.isGenericMedicineAvailable = isGenericMedicineAvailable;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocaleName() {
		return localeName;
	}

	public void setLocaleName(String localeName) {
		this.localeName = localeName;
	}

	public String getRegisteredOwnerName() {
		return registeredOwnerName;
	}

	public void setRegisteredOwnerName(String registeredOwnerName) {
		this.registeredOwnerName = registeredOwnerName;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public List<LocaleImage> getLocaleImages() {
		return localeImages;
	}

	public void setLocaleImages(List<LocaleImage> localeImages) {
		this.localeImages = localeImages;
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

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public List<String> getAlternateContactNumbers() {
		return alternateContactNumbers;
	}

	public void setAlternateContactNumbers(List<String> alternateContactNumbers) {
		this.alternateContactNumbers = alternateContactNumbers;
	}

	public List<LocaleWorkingSchedule> getLocaleWorkingSchedules() {
		return localeWorkingSchedules;
	}

	public void setLocaleWorkingSchedules(List<LocaleWorkingSchedule> localeWorkingSchedules) {
		this.localeWorkingSchedules = localeWorkingSchedules;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getLocaleAddress() {
		return localeAddress;
	}

	public void setLocaleAddress(String localeAddress) {
		this.localeAddress = localeAddress;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getLocaleEmailAddress() {
		return localeEmailAddress;
	}

	public void setLocaleEmailAddress(String localeEmailAddress) {
		this.localeEmailAddress = localeEmailAddress;
	}

	public Boolean getIsTwentyFourSevenOpen() {
		return isTwentyFourSevenOpen;
	}

	public void setIsTwentyFourSevenOpen(Boolean isTwentyFourSevenOpen) {
		this.isTwentyFourSevenOpen = isTwentyFourSevenOpen;
	}

	public String getLocaleUId() {
		return localeUId;
	}

	public void setLocaleUId(String localeUId) {
		this.localeUId = localeUId;
	}

	public int getOpenSince() {
		return openSince;
	}

	public void setOpenSince(int openSince) {
		this.openSince = openSince;
	}

	public UserState getUserState() {
		return userState;
	}

	public void setUserState(UserState userState) {
		this.userState = userState;
	}

	public Boolean getIsActivate() {
		return isActivate;
	}

	public void setIsActivate(Boolean isActivate) {
		this.isActivate = isActivate;
	}

	public Boolean getIsHomeDeliveryAvailable() {
		return isHomeDeliveryAvailable;
	}

	public void setIsHomeDeliveryAvailable(Boolean isHomeDeliveryAvailable) {
		this.isHomeDeliveryAvailable = isHomeDeliveryAvailable;
	}

	public double getHomeDeliveryRadius() {
		return homeDeliveryRadius;
	}

	public void setHomeDeliveryRadius(double homeDeliveryRadius) {
		this.homeDeliveryRadius = homeDeliveryRadius;
	}

	public String getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	public String getLocaleType() {
		return localeType;
	}

	public void setLocaleType(String localeType) {
		this.localeType = localeType;
	}

	public Boolean getIsPasswordVerified() {
		return isPasswordVerified;
	}

	public void setIsPasswordVerified(Boolean isPasswordVerified) {
		this.isPasswordVerified = isPasswordVerified;
	}

	@Override
	public String toString() {
		return "Locale [id=" + id + ", localeName=" + localeName + ", registeredOwnerName=" + registeredOwnerName
				+ ", licenseNumber=" + licenseNumber + ", localeImages=" + localeImages + ", logoUrl=" + logoUrl
				+ ", logoThumbnailUrl=" + logoThumbnailUrl + ", contactNumber=" + contactNumber
				+ ", alternateContactNumbers=" + alternateContactNumbers + ", localeWorkingSchedules="
				+ localeWorkingSchedules + ", address=" + address + ", localeAddress=" + localeAddress + ", websiteUrl="
				+ websiteUrl + ", localeEmailAddress=" + localeEmailAddress + ", isTwentyFourSevenOpen="
				+ isTwentyFourSevenOpen + ", localeUId=" + localeUId + ", openSince=" + openSince + ", userState="
				+ userState + ", isActivate=" + isActivate + ", isLocaleListed=" + isLocaleListed
				+ ", localeRankingCount=" + localeRankingCount + ", noOfLocaleRecommendation="
				+ noOfLocaleRecommendation + ", isHomeDeliveryAvailable=" + isHomeDeliveryAvailable
				+ ", homeDeliveryRadius=" + homeDeliveryRadius + ", paymentInfo=" + paymentInfo + ", localeType="
				+ localeType + ", isPasswordVerified=" + isPasswordVerified + "]";
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public Boolean getIsAcceptRequest() {
		return isAcceptRequest;
	}

	public void setIsAcceptRequest(Boolean isAcceptRequest) {
		this.isAcceptRequest = isAcceptRequest;
	}

	public Double getMinimumAmountForDelivery() {
		return minimumAmountForDelivery;
	}

	public void setMinimumAmountForDelivery(Double minimumAmountForDelivery) {
		this.minimumAmountForDelivery = minimumAmountForDelivery;
	}

	public String getPharmacySlugUrl() {
		return pharmacySlugUrl;
	}

	public void setPharmacySlugUrl(String pharmacySlugUrl) {
		this.pharmacySlugUrl = pharmacySlugUrl;
	}

}