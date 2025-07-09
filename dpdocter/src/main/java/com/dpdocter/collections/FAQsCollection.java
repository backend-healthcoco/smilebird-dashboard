package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.FaqsObject;
import com.dpdocter.enums.FaqType;

@Document(collection = "faqs_cl")
public class FAQsCollection extends GenericCollection{

    @Id
    private ObjectId id;

    @Field
    private FaqType faqType;
    
    @Field
    private List<FaqsObject> faqs;
    
    @Field
    private Boolean discarded;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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
