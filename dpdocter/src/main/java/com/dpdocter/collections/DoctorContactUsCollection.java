package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.enums.GenderType;

@Document(collection = "doctor_contact_us_cl")
public class DoctorContactUsCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String title;
	@Field
	private String firstName;
	@Indexed(unique = true)
	private String userName;
	@Field
	private GenderType gender;
	@Field
	private String emailAddress;
	@Field
	private String mobileNumber;
	@Field
	private List<String> specialities;
	@Field
	private DoctorContactStateType contactState;
	@Field
	private Date contactLaterOnDate;
	@Field
	private Boolean isVerified = Boolean.FALSE;
	@Field
	private Boolean toList = Boolean.FALSE;
	@Field
	private String deviceType;

	@Field
	private String city;

	public ObjectId getId() {
		return id;
	}

	public Date getContactLaterOnDate() {
		return contactLaterOnDate;
	}

	public void setContactLaterOnDate(Date contactLaterOnDate) {
		this.contactLaterOnDate = contactLaterOnDate;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public GenderType getGender() {
		return gender;
	}

	public void setGender(GenderType gender) {
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

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public DoctorContactStateType getContactState() {
		return contactState;
	}

	public void setContactState(DoctorContactStateType contactState) {
		this.contactState = contactState;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public Boolean getToList() {
		return toList;
	}

	public void setToList(Boolean toList) {
		this.toList = toList;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	@Override
	public String toString() {
		return "DoctorContactUsCollection [id=" + id + ", title=" + title + ", firstName=" + firstName + ", userName="
				+ userName + ", gender=" + gender + ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber
				+ ", specialities=" + specialities + ", contactState=" + contactState + ", contactLaterOnDate="
				+ contactLaterOnDate + ", isVerified=" + isVerified + ", toList=" + toList + ", deviceType="
				+ deviceType + ", city=" + city + "]";
	}
}
