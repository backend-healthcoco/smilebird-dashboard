package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DoctorSubscriptionPayment;
import com.dpdocter.beans.Subscription;
import com.dpdocter.enums.PackageType;

public interface SubscriptionServices {

	public Subscription addEditSubscription(Subscription request);

	public List<Subscription> getSubscription(int size, int page, Boolean isDiscarded, String searchTerm);

	public Integer countSubscription(Boolean isDiscarded, String searchTerm);

	public Subscription getSubscriptionById(String id);
	
	public Subscription getSubscriptionByDoctorId(String doctorId,PackageType packageName,int duration,int newAmount);

	public List<DoctorSubscriptionPayment> getSubscriptionPaymentByDoctorId(String doctorId,int size, int page);
	
	public Integer countSubscriptionPayment(String doctorId);

	
//	public void reportCurrentTime();

}
