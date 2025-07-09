package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.BroadcastRequest;
import com.dpdocter.request.BroadCastMailRequest;
import com.dpdocter.request.BroadCastRequest;
import com.dpdocter.request.MessageRequest;

public interface BroadCastService {

	public Boolean broadcastMail(BroadCastMailRequest request);

	public Boolean broadcastPushNotification(BroadCastRequest request);

	public Boolean broadcastSMS(BroadCastRequest request);

	//public Boolean sendSMSToUser(String mobileNumber, String message, String type);

//	Boolean sendSMSToUser(List<String> mobileNumber, String message, String type);

	String sendSMSToUser(MessageRequest request);

	String sendSms(List<String> mobileNumbers, String message, String type);


	Integer countUsers(BroadcastRequest request);

}
