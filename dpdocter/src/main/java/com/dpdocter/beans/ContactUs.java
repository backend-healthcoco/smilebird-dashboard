package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class ContactUs extends GenericCollection{

	private String id;

    private String name;

    private String mobileNumber;

    private String emailAddress;

    private String message;
    
    private String type;
    
    private String path;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

	

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "ContactUs [id=" + id + ", name=" + name + ", mobileNumber=" + mobileNumber + ", emailAddress="
				+ emailAddress + ", message=" + message + "]";
	}
}
