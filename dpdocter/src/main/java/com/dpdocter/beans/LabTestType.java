package com.dpdocter.beans;


import com.dpdocter.enums.SampleStatus;


public class LabTestType {

	private Integer numberOfTest;
	
	private  String testType;
	
	private Double amount=0.0;

	private SampleStatus status;


	public Integer getNumberOfTest() {
		return numberOfTest;
	}

	public void setNumberOfTest(Integer numberOfTest) {
		this.numberOfTest = numberOfTest;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	
	public SampleStatus getStatus() {
		return status;
	}

	public void setStatus(SampleStatus status) {
		this.status = status;
	}


	@Override
	public String toString() {
		return "LabTestType [numberOfTest=" + numberOfTest + ", testType=" + testType + ", amount=" + amount + "]";
	}
	
	
	
}
