package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Subscription;

public interface SubscriptionHistoryServices {

	public Subscription addEditSubscriptionHistory(Subscription request);
	
	public List<Subscription> getSubscriptionHistory(int size, int page, Boolean isDiscarded, String searchTerm,String doctorId);

	public Integer countSubscriptionHistory(Boolean isDiscarded, String searchTerm,String doctorId);
	
	public Boolean discardSubscriptionHistory(String id,Boolean isDiscarded);
}
