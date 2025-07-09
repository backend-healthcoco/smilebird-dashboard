package com.dpdocter.webservices;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.FeedsRequest;
import com.dpdocter.beans.FeedsResponse;
import com.dpdocter.beans.ShopifyCustomer;
import com.dpdocter.beans.ShopifyOrderTransaction;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.ShopifyService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.SHOPIFY_BASE_URL, consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.SHOPIFY_BASE_URL, description = "Endpoint for Community Building")
public class ShopifyApi {
	
	private static Logger logger = LogManager.getLogger(ShopifyApi.class.getName());
	
	@Autowired
	private ShopifyService shopifyService;
	
	@PostMapping(value = PathProxy.ShopifyUrls.ADD_EDIT_CUSTOMER)
	@ApiOperation(value = PathProxy.ShopifyUrls.ADD_EDIT_CUSTOMER, notes = PathProxy.ShopifyUrls.ADD_EDIT_CUSTOMER)
	public Response<Boolean> addEditCustomer(@RequestBody String request) throws JsonParseException, JsonMappingException, IOException {

		System.out.println("request"+request); 
		request=request.replaceFirst("id","shopifyId");
		ObjectMapper mapper = new ObjectMapper();
		ShopifyCustomer request1= mapper.readValue(request,ShopifyCustomer.class);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(shopifyService.addEditShopifyCustomer(request1));
		return response;
	}
	
	
	@PostMapping(value = PathProxy.ShopifyUrls.DELETE_CUSTOMER)
	@ApiOperation(value = PathProxy.ShopifyUrls.DELETE_CUSTOMER, notes = PathProxy.ShopifyUrls.DELETE_CUSTOMER)
	public Response<Boolean> deleteCustomer(@RequestBody String request) throws JsonParseException, JsonMappingException, IOException {

		System.out.println("request"+request); 
		request=request.replaceFirst("id","shopifyId");
		ObjectMapper mapper = new ObjectMapper();
		ShopifyCustomer request1= mapper.readValue(request,ShopifyCustomer.class);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(shopifyService.addEditShopifyCustomer(request1));
		return response;
	}
	
	
	@PostMapping(value = PathProxy.ShopifyUrls.UPDATE_ORDER_TRANSACTION)
	@ApiOperation(value = PathProxy.ShopifyUrls.UPDATE_ORDER_TRANSACTION, notes = PathProxy.ShopifyUrls.UPDATE_ORDER_TRANSACTION)
	public Response<Boolean> orderTransaction(@RequestBody String request) throws JsonParseException, JsonMappingException, IOException {

		System.out.println("request"+request); 
		request=request.replaceFirst("id","shopifyId");
		ObjectMapper mapper = new ObjectMapper();
		ShopifyOrderTransaction request1= mapper.readValue(request,ShopifyOrderTransaction.class);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(shopifyService.updateOrderTransaction(request1));
		return response;
	}

}
