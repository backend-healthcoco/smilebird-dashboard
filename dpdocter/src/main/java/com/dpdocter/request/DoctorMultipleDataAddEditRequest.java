package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.DOB;
import com.dpdocter.beans.FileDetails;

public class DoctorMultipleDataAddEditRequest {

    private String doctorId;

    private String locationId;

    private String title;

    private String firstName;

    private int experience;

    private List<String> specialities;

    private FileDetails profileImage;

    private FileDetails coverImage;
    
    private String gender;
    
    private DOB dob;

    private String feedbackURL;

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
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

    public int getExperience() {
	return experience;
    }

    public void setExperience(int experience) {
	this.experience = experience;
    }

    public List<String> getSpeciality() {
	return specialities;
    }

    public void setSpeciality(List<String> specialities) {
	this.specialities = specialities;
    }

    public FileDetails getProfileImage() {
	return profileImage;
    }

    public void setProfileImage(FileDetails profileImage) {
	this.profileImage = profileImage;
    }

    public FileDetails getCoverImage() {
	return coverImage;
    }

    public void setCoverImage(FileDetails coverImage) {
	this.coverImage = coverImage;
    }

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public String getFeedbackURL() {
		return feedbackURL;
	}

	public void setFeedbackURL(String feedbackURL) {
		this.feedbackURL = feedbackURL;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	@Override
	public String toString() {
		return "DoctorMultipleDataAddEditRequest [doctorId=" + doctorId + ", locationId=" + locationId + ", title="
				+ title + ", firstName=" + firstName + ", experience=" + experience + ", specialities=" + specialities
				+ ", profileImage=" + profileImage + ", coverImage=" + coverImage + ", gender=" + gender + ", dob="
				+ dob + ", feedbackURL=" + feedbackURL + "]";
	}
}
