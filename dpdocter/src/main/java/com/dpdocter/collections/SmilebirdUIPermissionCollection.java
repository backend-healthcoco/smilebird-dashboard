package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "smilebird_admin_ui_permission_cl")
public class SmilebirdUIPermissionCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private ObjectId adminId;
	@Field
	private List<String> uiPermissions;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getAdminId() {
		return adminId;
	}

	public void setAdminId(ObjectId adminId) {
		this.adminId = adminId;
	}

	public List<String> getUiPermissions() {
		return uiPermissions;
	}

	public void setUiPermissions(List<String> uiPermissions) {
		this.uiPermissions = uiPermissions;
	}

}
