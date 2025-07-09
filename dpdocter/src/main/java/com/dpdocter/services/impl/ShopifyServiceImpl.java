package com.dpdocter.services.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.ShopifyCustomer;
import com.dpdocter.beans.ShopifyOrderTransaction;
import com.dpdocter.collections.ShopifyCustomerCollection;
import com.dpdocter.collections.ShopifyOrderTransactionCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.BusinessExceptionResponse;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ShopifyCustomerRepository;
import com.dpdocter.repository.ShopifyOrderTransactionRepository;
import com.dpdocter.services.ShopifyService;

@Service
public class ShopifyServiceImpl implements ShopifyService{
	
	@Autowired
	private ShopifyCustomerRepository shopifyCustomerRepository;
	
	@Autowired
	private ShopifyOrderTransactionRepository shopifyOrderTransactionRepository;
	
	private static Logger logger = LogManager.getLogger(ShopifyServiceImpl.class.getName());

	@Override
	public Boolean addEditShopifyCustomer(ShopifyCustomer request) {
		Boolean response=false;
		try {
			ShopifyCustomerCollection collection=new ShopifyCustomerCollection();
			if(request!=null)
			{
			//	request.setShopifyId(Long.toString(request.getId()));
			//	request.setId(0);
				
				BeanUtil.map(request,collection);
				collection.setId(null);
				collection.setCreatedTime(new Date());
				collection.setUpdatedTime(new Date());
				shopifyCustomerRepository.save(collection);
			}
			
		} catch (BusinessException e) {
			logger.error("Error while addEdit post " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while retrieving customer details from shopify.");
		}
		return response;
	}

	@Override
	public Boolean deleteShopifyCustomer(ShopifyCustomer request) {
		Boolean response=false;
		try {
			ShopifyCustomerCollection collection=new ShopifyCustomerCollection();
			if(request!=null)
			{
				BeanUtil.map(request,collection);
				collection.setCreatedTime(new Date());
				collection.setUpdatedTime(new Date());
				shopifyCustomerRepository.save(collection);
			}
			
		} catch (BusinessException e) {
			logger.error("Error while addEdit post " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while retrieving customer details from shopify.");
		}
		return response;
	}

	@Override
	public Boolean updateOrderTransaction(ShopifyOrderTransaction request) {
		Boolean response=false;
		try {
			ShopifyOrderTransactionCollection collection=new ShopifyOrderTransactionCollection();
			if(request!=null)
			{
				BeanUtil.map(request,collection);
				collection.setCreatedTime(new Date());
				collection.setUpdatedTime(new Date());
				shopifyOrderTransactionRepository.save(collection);
			}
			
		} catch (BusinessException e) {
			logger.error("Error while addEdit post " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while retrieving customer details from shopify.");
		}
		return response;
	}

}
