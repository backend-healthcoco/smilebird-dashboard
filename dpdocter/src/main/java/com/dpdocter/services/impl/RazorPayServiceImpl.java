package com.dpdocter.services.impl;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.services.RazorPayService;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;

@Service
public class RazorPayServiceImpl implements RazorPayService {

	@Value(value = "${razorpay.api.key}")
	private String RP_API_KEY;

	@Value(value = "${razorpay.api.secret}")
	private String RP_API_SECRET;

	@Override
	public List<Payment> getAllPayments(Long from, Long to, int page, int size) {
		List<Payment> payments = null;
		// RazorpayClient razorpay = null;
		try {
			RazorpayClient razorpay = new RazorpayClient(RP_API_KEY, RP_API_SECRET);
			JSONObject paymentRequest = new JSONObject();
			// supported option filters (from, to, count, skip)
			if (from != null) {
				paymentRequest.put("from", from);
			}
			if (to != null) {
				paymentRequest.put("to", to);
			}
			paymentRequest.put("count", size);
			paymentRequest.put("skip", page);

			payments = razorpay.Payments.fetchAll(paymentRequest);
		} catch (RazorpayException e) {
			// Handle Exception
			System.out.println(e.getMessage());
		}
		return payments;
	}

	@Override
	public Payment getPaymentById(String id) {
		RazorpayClient razorpay = null;
		Payment payment = null;
		try {
			razorpay = new RazorpayClient(RP_API_KEY, RP_API_SECRET);
			payment = razorpay.Payments.fetch(id);
		} catch (RazorpayException e) {
			// Handle Exception
			System.out.println(e.getMessage());
		}
		return payment;
	}

	@Override
	public Refund refundPayment(String id, String amount, List<String> notes) {
		Refund refund = null;
		try {
			RazorpayClient razorpay = new RazorpayClient(RP_API_KEY, RP_API_SECRET);
			/*
			 * // Full Refund Refund refund =
			 * razorpay.Payments.refund("<payment_id>");
			 */
			
			
			// Partial Refund
			JSONObject refundRequest = new JSONObject();
			if (amount != null) {
				refundRequest.put("amount", amount); // Amount should be in
														// paise
			}
			if (notes != null && !notes.isEmpty()) {
				refundRequest.put("notes", notes);
			}
			refund = razorpay.Payments.refund(id, refundRequest);
		} catch (RazorpayException e) {
			// Handle Exception
			System.out.println(e.getMessage());
		}
		return refund;
	}
	
	

}
