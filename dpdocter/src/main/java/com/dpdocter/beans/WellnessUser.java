package com.dpdocter.beans;

import java.util.Arrays;

import com.dpdocter.collections.GenericCollection;

public class WellnessUser extends GenericCollection {

	private String id;

	private String userName;

	private char[] password;

	private char[] salt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public char[] getSalt() {
		return salt;
	}

	public void setSalt(char[] salt) {
		this.salt = salt;
	}

	@Override
	public String toString() {
		return "WellnessUser [id=" + id + ", userName=" + userName + ", password=" + Arrays.toString(password)
				+ ", salt=" + Arrays.toString(salt) + "]";
	}
	
	

}
