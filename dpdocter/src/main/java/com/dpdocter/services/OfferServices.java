package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.Offer;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.OfferResponse;

public interface OfferServices {
	public OfferResponse addEditOffer(Offer request);

	public OfferResponse getOffer(String id);

	public OfferResponse deleteOffer(String id, boolean discarded);

	public List<Offer> getOffers(int size, int page, Boolean discarded, String searchTerm, String productId,
			String offerType, String productType);

	public ImageURLResponse uploadImages(FileDetails request, String module);
}
