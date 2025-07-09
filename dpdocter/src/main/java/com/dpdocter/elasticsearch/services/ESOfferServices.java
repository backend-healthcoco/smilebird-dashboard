package com.dpdocter.elasticsearch.services;

import com.dpdocter.elasticsearch.document.ESOfferDocument;
import com.dpdocter.elasticsearch.document.ESTrendingDocument;

public interface ESOfferServices {
	public boolean addOffer(ESOfferDocument request);
	
	public boolean addTrending(ESTrendingDocument request);
}
