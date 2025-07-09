package com.dpdocter.services;

import java.util.List;

import com.razorpay.Payment;
import com.razorpay.Refund;


public interface RazorPayService {

	List<Payment> getAllPayments(Long from, Long to, int page, int size);

	Payment getPaymentById(String id);

	Refund refundPayment(String id, String amount, List<String> notes);

}
