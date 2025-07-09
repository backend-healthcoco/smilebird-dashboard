package com.dpdocter.collections;

import java.util.Arrays;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "wellness_user_cl")
public class WellnessUserCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Indexed(unique = true)
	private String userName;

	@Field
	private char[] password;

	@Field
	private char[] salt;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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
		return "WellnessUserCollection [id=" + id + ", userName=" + userName + ", password=" + Arrays.toString(password)
				+ ", salt=" + Arrays.toString(salt) + "]";
	}

}
