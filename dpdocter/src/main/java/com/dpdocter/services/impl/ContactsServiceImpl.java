package com.dpdocter.services.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.SendAppLink;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.enums.AppType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.ContactsService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SMSServices;

import common.util.web.DPDoctorUtils;

@Service
public class ContactsServiceImpl implements ContactsService {

    private static Logger logger = LogManager.getLogger(ContactsServiceImpl.class.getName());

    @Autowired
    private SMSServices sMSServices;

    @Autowired
    private MailBodyGenerator mailBodyGenerator;

    @Autowired
    private MailService mailService;

    @Value(value = "${app.link.message}")
    private String appLinkMessage;
   
    @Value(value = "${patient.app.bit.link}")
    private String patientAppBitLink;
   
    @Value(value = "${doctor.app.bit.link}")
    private String doctorAppBitLink;
   
    @Value(value = "${ipad.app.bit.link}")
    private String ipadAppBitLink;
   
    @Value(value = "${mail.get.app.link.subject}")
    private String getAppLinkSubject;
 

	@Override
	public Boolean sendLink(SendAppLink request) {

		Boolean response = false;
		try {
			String appType = "", appBitLink = "";  
			if(request.getAppType().getType().equals(AppType.HEALTHCOCO)){
				appType = "Healthcoco"; appBitLink = patientAppBitLink;
			}else if(request.getAppType().getType().equals(AppType.HEALTHCOCO_PLUS)){
				appType = "Healthcoco +"; appBitLink = doctorAppBitLink;
			}else if(request.getAppType().getType().equals(AppType.HEALTHCOCO_PAD)){
				appType = "Healthcoco Pad"; appBitLink = ipadAppBitLink;
			} 
		
			if(!DPDoctorUtils.anyStringEmpty(request.getMobileNumber())){
				appLinkMessage.replace("{appType}", appType).replace("{appLink}", appBitLink);
				SMSTrackDetail smsTrackDetail = sMSServices.createSMSTrackDetail(null, null, null, null, null, appLinkMessage, request.getMobileNumber(), "Get App Link");
				sMSServices.sendSMS(smsTrackDetail, false);
			}else if(!DPDoctorUtils.anyStringEmpty(request.getEmailAddress())){
			    String body = mailBodyGenerator.generateAppLinkEmailBody(appType, appBitLink, "appLinkTemplate.vm");
				mailService.sendEmail(request.getEmailAddress(), getAppLinkSubject.replace("{appType}", appType), body, null);
			} 		
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e);
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}
}
