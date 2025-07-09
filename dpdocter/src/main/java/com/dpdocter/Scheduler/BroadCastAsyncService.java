package com.dpdocter.Scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.request.BroadCastMailRequest;
import com.dpdocter.request.BroadCastRequest;
import com.dpdocter.services.BroadCastService;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;

import common.util.web.DPDoctorUtils;
import de.flapdoodle.embedmongo.collections.Collections;

@Service
public class BroadCastAsyncService {

	@Autowired
	private PushNotificationServices pushNotificationServices;

	@Autowired
	private SMSServices smsServices;

	@Autowired
	private MailService mailService;
	@Autowired
	private BroadCastService broadcastService;

	@Async
	public void broadcastNotication(List<UserCollection> user, BroadCastRequest request, RoleEnum role)
			throws InterruptedException {
int size=10;
		List<ObjectId> userIds = new ArrayList<ObjectId>();
		List<ObjectId> sublist=null;
		for (UserCollection userCollection : user) {
			ObjectId userId = null;
			if (!DPDoctorUtils.anyStringEmpty(userCollection.getId())) {
//				System.out.println(userCollection.getId());
				userId=userCollection.getId();
				userIds.add(userId);
			}
		}
		System.out.println("userId"+userIds);
		if (!userIds.isEmpty() && userIds != null) {
			for(int start=0;start<userIds.size();start+=size)
			{
				int end=Math.min(start+size,userIds.size());
				 sublist=userIds.subList(start, end);
			pushNotificationServices.notifyAll(role, sublist, request.getMessage());
		
			}
		}
	}

	
	//old sms service method
//	@Async
//	public void broadcastSMS(List<UserCollection> user, BroadCastRequest request) throws InterruptedException {
//		
//		for (UserCollection userCollection : user) {
//			if (!DPDoctorUtils.anyStringEmpty(userCollection.getMobileNumber())) {
//				System.out.println(userCollection.getMobileNumber());
//				SMSTrackDetail smsTrackDetail = smsServices.createSMSTrackDetail(null, null, null, null, null,
//						request.getMassage() + "\n Thank you", userCollection.getMobileNumber(), "Broadcast");
//				smsServices.sendSMS(smsTrackDetail, false);
//				
//							}
//		}
//	
//	}
	
	
	//new sms service method
	@Async
	public void broadcastSMS(List<UserCollection> user, BroadCastRequest request) throws InterruptedException {
		List<String> mobileNumbers  = new ArrayList<>();
		Integer size=100;
		for (UserCollection userCollection : user) {
			
			if (!DPDoctorUtils.anyStringEmpty(userCollection.getMobileNumber())) {
				String mobile=null;
				if (!DPDoctorUtils.anyStringEmpty(userCollection.getCountryCode())) {
					 mobile =userCollection.getCountryCode() +userCollection.getMobileNumber();
				}
				else {
			//	System.out.println(userCollection.getMobileNumber());
				 	mobile ="+91" +userCollection.getMobileNumber();
				}
				mobileNumbers.add(mobile);
							}
		}
		List<String> sublist=null;
		for(int start=0;start<mobileNumbers.size();start+=size)
			{
				int end=Math.min(start+size,mobileNumbers.size());
				 sublist=mobileNumbers.subList(start, end);
		//		 System.out.println("sublist"+sublist);
	
		broadcastService.sendSms(sublist, request.getMessage(), request.getSmsRoute().getType());
			}
	}


	
	public void broadcastMail(List<UserCollection> user, BroadCastMailRequest request)
			throws InterruptedException, MessagingException {
		System.out.println(user.toString());
		
		List<String>email=user.stream().map(use->use.getEmailAddress()).collect(Collectors.toList());
		System.out.println("Email"+email +"");
		if (user != null && !user.isEmpty()) {
			for (UserCollection userCollection : user) {
			//	if (!DPDoctorUtils.anyStringEmpty(userCollection.getEmailAddress())) {
			//		System.out.println(userCollection.getEmailAddress());
					broadcastEmail(userCollection,request.getSubject(), request.getBody());
					//mailService.sendEmail(userCollection.getEmailAddress(), request.getSubject(), request.getBody(),
					//		null);
			//	}
			}
		}

		if (request.getRecivers() != null && !request.getRecivers().isEmpty()) {
			for (String reciver : request.getRecivers()) {
				if (!DPDoctorUtils.anyStringEmpty(reciver)) {
					System.out.println(reciver);
					mailService.sendEmail(reciver, request.getSubject(), request.getBody(), null);
				}
			}
		}
	}
	
	@Async
	void broadcastEmail(UserCollection userCollection,String subject,String body) throws MessagingException
	{
		if (!DPDoctorUtils.anyStringEmpty(userCollection.getEmailAddress())) {
			System.out.println(userCollection.getEmailAddress());
			mailService.sendEmail(userCollection.getEmailAddress(), subject,body,null);
		}
	}
}
