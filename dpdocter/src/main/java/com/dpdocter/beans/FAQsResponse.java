package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.FaqType;

public class FAQsResponse extends GenericCollection {
	private String id;

	private FaqType faqType;

	private List<FaqsObject> faqs;

	private Boolean discarded;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public FaqType getFaqType() {
		return faqType;
	}

	public void setFaqType(FaqType faqType) {
		this.faqType = faqType;
	}

	public List<FaqsObject> getFaqs() {
		return faqs;
	}

	public void setFaqs(List<FaqsObject> faqs) {
		this.faqs = faqs;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
