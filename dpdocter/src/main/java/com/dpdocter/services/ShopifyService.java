package com.dpdocter.services;

import com.dpdocter.beans.ShopifyCustomer;
import com.dpdocter.beans.ShopifyOrderTransaction;

public interface ShopifyService {

	Boolean addEditShopifyCustomer(ShopifyCustomer request);
	
	Boolean deleteShopifyCustomer(ShopifyCustomer request);
	
	Boolean updateOrderTransaction(ShopifyOrderTransaction request);
}
