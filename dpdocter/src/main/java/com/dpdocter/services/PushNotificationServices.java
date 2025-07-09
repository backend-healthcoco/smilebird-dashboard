package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.UserDevice;
import com.dpdocter.collections.UserDeviceCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.request.BroadcastNotificationRequest;

public interface PushNotificationServices {

	UserDevice addDevice(UserDevice request);

	void notifyUser(String userId, String message, String componentType, String componentTypeId,
			List<UserDeviceCollection> userDeviceCollections);

	void broadcastNotification(BroadcastNotificationRequest request);

	void readNotification(String deviceId, Integer count);

	public Boolean notifyRefresh(String id, String requestId, String responseId, RoleEnum role, String message,
			ComponentType componentType);

	public Boolean notifyAll(RoleEnum role, List<ObjectId>userIds, String message);

}
