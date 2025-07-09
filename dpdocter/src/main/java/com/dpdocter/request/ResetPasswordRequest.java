package com.dpdocter.request;

public class ResetPasswordRequest {
    private String userId;
    
    private String userName;

    private char[] password;

    private String mobileNumber;
    
    public String getUserId() {
	return userId;
    }

    public void setUserId(String userId) {
	this.userId = userId;
    }

    public char[] getPassword() {
	return password;
    }

    public void setPassword(char[] password) {
	this.password = password;
    }

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "ResetPasswordRequest [userId=" + userId + ", password=" + password + ", mobileNumber=" + mobileNumber
				+ "]";
	}
}
