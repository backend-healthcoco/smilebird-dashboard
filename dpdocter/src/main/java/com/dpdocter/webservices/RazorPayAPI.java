package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.services.RazorPayService;
import com.razorpay.Payment;
import com.razorpay.Refund;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.RAZORPAY_BASE_URL, produces = MediaType.APPLICATION_JSON ,  consumes = MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.RAZORPAY_BASE_URL, description = "Endpoint for RazorPay")
public class RazorPayAPI {
	
	@Autowired
	private RazorPayService razorPayService; 

	private static Logger logger = LogManager.getLogger(RazorPayAPI.class.getName());
	
	@GetMapping(value = PathProxy.RazorPayUrls.GET_PAYMENTS)
	@ApiOperation(value = PathProxy.RazorPayUrls.GET_PAYMENTS, notes = PathProxy.RazorPayUrls.GET_PAYMENTS)
	public Response<Payment> getPayments(@RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="from") Long from, @RequestParam(required = false, value ="to") Long to) {

		List<Payment> payments = razorPayService.getAllPayments(from, to, page, size);
		Response<Payment> response = new Response<Payment>();
		response.setDataList(payments);
		return response;
	}

	@GetMapping(value = PathProxy.RazorPayUrls.GET_PAYMENT_BY_ID)
	@ApiOperation(value = PathProxy.RazorPayUrls.GET_PAYMENT_BY_ID, notes = PathProxy.RazorPayUrls.GET_PAYMENT_BY_ID)
	public Response<Payment> getPaymentById(@PathVariable("id") String id) {

		Payment payment = razorPayService.getPaymentById(id);
		Response<Payment> response = new Response<Payment>();
		response.setData(payment);
		return response;
	}

	@GetMapping(value = PathProxy.RazorPayUrls.REFUND_PAYMENT)
	@ApiOperation(value = PathProxy.RazorPayUrls.GET_PAYMENTS, notes = PathProxy.RazorPayUrls.GET_PAYMENTS)
	public Response<Refund> refundPayment(@PathVariable("id") String id, @RequestParam(required = false, value ="amount") String amount,
			@RequestParam(required = false, value ="notes") List<String> notes) {

		Refund refund = razorPayService.refundPayment(id, amount, notes);
		Response<Refund> response = new Response<Refund>();
		response.setData(refund);
		return response;
	}
}
