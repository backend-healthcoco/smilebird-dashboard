package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "broadCast_cl")
public class BroadCastCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private List<String> userTypes;
	@Field
	private String message;
	@Field
	private String messageType;
	@Field
	private ObjectId adminId;

	
	public ObjectId getAdminId() {
		return adminId;
	}

	public void setAdminId(ObjectId adminId) {
		this.adminId = adminId;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<String> getUserTypes() {
		return userTypes;
	}

	public void setUserTypes(List<String> userTypes) {
		this.userTypes = userTypes;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	

}
