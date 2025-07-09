package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.SMSDeliveryReports;
import com.dpdocter.beans.SMSFormat;
import com.dpdocter.beans.SMSTrack;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.response.SMSResponse;

public interface SMSServices {
	Boolean sendSMS(SMSTrackDetail smsTrackDetail, Boolean save);

    SMSResponse getSMS(int page, int size, String doctorId, String locationId, String hospitalId);

    List<SMSTrack> getSMSDetails(int page, int size, String patientId, String doctorId, String locationId, String hospitalId);

    void updateDeliveryReports(List<SMSDeliveryReports> request);

    void addNumber(String mobileNumber);

    void deleteNumber(String mobileNumber);

    SMSTrackDetail createSMSTrackDetail(String doctorId, String locationId, String hospitalId, String patientId, String patientName, String message,
	    String mobileNumber, String type);

    SMSFormat addSmsFormat(SMSFormat request);

    List<SMSFormat> getSmsFormat(String doctorId, String locationId, String hospitalId, String type);
    public String sendBulkSMSResponse(List<String> mobileNumbers, String message, String senderId);

	String getSmsStatus();

	Boolean sendOTPSMS(SMSTrackDetail smsTrackDetail, Boolean save);

	Boolean sendWhatsappMessage(SMSTrackDetail smsTrackDetail, Boolean save);

	Boolean sendDentalChainSMS(SMSTrackDetail smsTrackDetail, boolean b);
}
