package com.dpdocter.beans;

import java.util.Map;

public class AlignerIPRAndAttachmetDetail {
	private String planId;

	private String iPROnAligner;

	private String attachmentOnAligner;

	private Map<String, String> iPRROnTeeth;

	private String attachmentTeeth;

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getiPROnAligner() {
		return iPROnAligner;
	}

	public void setiPROnAligner(String iPROnAligner) {
		this.iPROnAligner = iPROnAligner;
	}

	public String getAttachmentOnAligner() {
		return attachmentOnAligner;
	}

	public void setAttachmentOnAligner(String attachmentOnAligner) {
		this.attachmentOnAligner = attachmentOnAligner;
	}

	public Map<String, String> getiPRROnTeeth() {
		return iPRROnTeeth;
	}

	public void setiPRROnTeeth(Map<String, String> iPRROnTeeth) {
		this.iPRROnTeeth = iPRROnTeeth;
	}

	public String getAttachmentTeeth() {
		return attachmentTeeth;
	}

	public void setAttachmentTeeth(String attachmentTeeth) {
		this.attachmentTeeth = attachmentTeeth;
	}

}
