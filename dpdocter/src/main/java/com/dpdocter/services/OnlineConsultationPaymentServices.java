package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.BankDetails;
import com.dpdocter.beans.DoctorOnlineConsultation;

import com.dpdocter.beans.OnlineConsultationPayment;
import com.dpdocter.beans.OnlineConsultationSpecialityPrice;

import com.dpdocter.beans.TransactionStatusRequest;

import com.dpdocter.request.OnlineConsultationAccountRequest;
import com.dpdocter.request.RazorPayAccountRequest;
import com.dpdocter.response.RazorPayAccountResponse;
import com.razorpay.RazorpayException;

import common.util.web.Response;

public interface OnlineConsultationPaymentServices {

	Boolean editPaymentDetail(OnlineConsultationAccountRequest request);
	
	//List<OnlineConsultationPayment> getConsultationPaymentList(int page,int size,String searchTerm);

	//Response<Object> getBankDetailsList(int page, int size, String searchTerm,String city);
	
	public RazorPayAccountResponse createAccount(RazorPayAccountRequest request);

	//Response<Object> getBankDetailsList(int page, int size, String searchTerm, String city, List<String> Specialities);

//	Response<Object> getBankDetailsList(int page, int size, String searchTerm, String city, String specialities);

//	Response<Object> getBankDetailsList(int page, int size, String searchTerm, String city, String specialities,
//			Boolean isRegistrationDetailsVerified, Boolean isPhotoIdVerified);

	Response<Object> getBankDetailsList(int page, int size, String searchTerm, String city, String specialities,
			Boolean isRegistrationDetailsVerified, Boolean isPhotoIdVerified, String fromDate, String toDate);

	List<DoctorOnlineConsultation> getDoctorOnlineConsultationInfo(int page, int size, String searchTerm, String city,
			String specialities,String transactionStatus, String fromDate, String toDate);

	

	
	Boolean updateTransactionStatus(TransactionStatusRequest request);

//	public Integer countConsultationPayment(Boolean discarded, String searchTerm);
	
	
	OnlineConsultationSpecialityPrice addEditPrice(OnlineConsultationSpecialityPrice request);
	
	List<OnlineConsultationSpecialityPrice> getDoctorOnlineConsultationPriceList(int page,int size,String searchTerm);
}
