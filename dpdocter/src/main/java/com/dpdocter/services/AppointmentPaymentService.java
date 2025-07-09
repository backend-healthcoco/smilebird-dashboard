package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.AppointmentPaymentInfo;
import com.dpdocter.request.AppointmentPaymentTransferRequest;
import com.dpdocter.response.AppointmentResponse;


public interface AppointmentPaymentService {

	List<AppointmentPaymentInfo> getAppointmentPaymentInfo(int page, int size, String searchTerm,String city,
			String specialities,String transactionStatus,
			String fromDate,String toDate);

	Boolean createPaymentTransfer(AppointmentPaymentTransferRequest request);


	List<AppointmentResponse> getAppointmentRequest(int size, int page, String searchTerm, String transactionStatus);
	
	
}
