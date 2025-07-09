package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.AdminUIPermission;

public interface AdminUIPermissionServices {
	public AdminUIPermission addPermission(AdminUIPermission adminUIPermission);

	public List<AdminUIPermission> getUIPermission(int page, int size);

	public AdminUIPermission getUIPermissionByAdminId(String adminId);

	public AdminUIPermission getUIPermissionById(String id);

}
