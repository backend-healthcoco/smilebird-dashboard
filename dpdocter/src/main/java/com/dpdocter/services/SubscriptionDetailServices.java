package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.SubscriptionDetail;

public interface SubscriptionDetailServices {
	public SubscriptionDetail activate(SubscriptionDetail detail);
	
	public List<SubscriptionDetail> getSubscriptionDetails(int size,int page,String doctorId,boolean isDemo,boolean isExpired);

	public Boolean deactivate(String subscriptionDetailId);

	public List<SubscriptionDetail> addsubscriptionData();

}
