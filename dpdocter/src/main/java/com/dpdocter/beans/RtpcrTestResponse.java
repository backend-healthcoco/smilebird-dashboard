package com.dpdocter.beans;

import java.util.List;

public class RtpcrTestResponse {

	private List<RtpcrTest> rtpcr;
	private Double totalAmountCollected=0.0;
	public List<RtpcrTest> getRtpcr() {
		return rtpcr;
	}
	public void setRtpcr(List<RtpcrTest> rtpcr) {
		this.rtpcr = rtpcr;
	}
	public Double getTotalAmountCollected() {
		return totalAmountCollected;
	}
	public void setTotalAmountCollected(Double totalAmountCollected) {
		this.totalAmountCollected = totalAmountCollected;
	} 
	
	
	
}
