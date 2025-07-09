package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.Offer;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.OfferResponse;
import com.dpdocter.services.OfferServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.OFFER_URL, produces = MediaType.APPLICATION_JSON , consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.OFFER_URL, description = "Endpoint for offer")
public class OfferApi {

	private static Logger logger = LogManager.getLogger(OfferApi.class.getName());

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private OfferServices offerService;

	@PostMapping(value = PathProxy.OfferUrls.ADD_EDIT_OFFER)
	@ApiOperation(value = PathProxy.OfferUrls.ADD_EDIT_OFFER, notes = PathProxy.OfferUrls.ADD_EDIT_OFFER)
	public Response<OfferResponse> addEditOffers(@RequestBody Offer request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		OfferResponse offer = offerService.addEditOffer(request);
		Response<OfferResponse> response = new Response<OfferResponse>();

		if (offer != null) {
			if (offer.getTitleImage() != null) {
				if (!DPDoctorUtils.anyStringEmpty(offer.getTitleImage().getImageUrl()))
					offer.getTitleImage().setImageUrl(getFinalImageURL(offer.getTitleImage().getImageUrl()));

				if (!DPDoctorUtils.anyStringEmpty(offer.getTitleImage().getThumbnailUrl()))
					offer.getTitleImage().setThumbnailUrl(getFinalImageURL(offer.getTitleImage().getThumbnailUrl()));

			}
		}
		response.setData(offer);
		return response;
	}

	@DeleteMapping(value = PathProxy.OfferUrls.DELETE_OFFER)
	@ApiOperation(value = PathProxy.OfferUrls.DELETE_OFFER, notes = PathProxy.OfferUrls.DELETE_OFFER)
	public Response<OfferResponse> deleteOffer(@PathVariable("id") String id,
			@RequestParam(required = false, value ="discarded", defaultValue = "true") Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<OfferResponse> response = new Response<OfferResponse>();
		OfferResponse offer = offerService.deleteOffer(id, discarded);
		if (offer != null) {
			if (offer.getTitleImage() != null) {
				if (!DPDoctorUtils.anyStringEmpty(offer.getTitleImage().getImageUrl()))
					offer.getTitleImage().setImageUrl(getFinalImageURL(offer.getTitleImage().getImageUrl()));

				if (!DPDoctorUtils.anyStringEmpty(offer.getTitleImage().getThumbnailUrl()))
					offer.getTitleImage().setThumbnailUrl(getFinalImageURL(offer.getTitleImage().getThumbnailUrl()));

			}
		}
		response.setData(offer);
		return response;
	}

	@GetMapping(value = PathProxy.OfferUrls.GET_OFFER)
	@ApiOperation(value = PathProxy.OfferUrls.GET_OFFER, notes = PathProxy.OfferUrls.GET_OFFER)
	public Response<OfferResponse> getOffer(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<OfferResponse> response = new Response<OfferResponse>();

		OfferResponse offer = offerService.getOffer(id);

		response.setData(offer);
		return response;
	}

	@GetMapping(value = PathProxy.OfferUrls.GET_OFFERS)
	@ApiOperation(value = PathProxy.OfferUrls.GET_OFFERS, notes = PathProxy.OfferUrls.GET_OFFERS)
	public Response<Offer> getOffers(@RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="discarded") Boolean discarded, @RequestParam(required = false, value ="searchTerm") String searchTerm,
			@RequestParam(required = false, value ="productId") String productId, @RequestParam(required = false, value ="offerType") String offerType,
			@RequestParam(required = false, value ="productType") String productType) {

		Response<Offer> response = new Response<Offer>();
		List<Offer> offers = offerService.getOffers(size, page, discarded, searchTerm, productId, offerType,
				productType);
		for (Offer offer : offers) {
			if (offer != null) {
				if (offer.getTitleImage() != null) {
					if (!DPDoctorUtils.anyStringEmpty(offer.getTitleImage().getImageUrl()))
						offer.getTitleImage().setImageUrl(getFinalImageURL(offer.getTitleImage().getImageUrl()));

					if (!DPDoctorUtils.anyStringEmpty(offer.getTitleImage().getThumbnailUrl()))
						offer.getTitleImage()
								.setThumbnailUrl(getFinalImageURL(offer.getTitleImage().getThumbnailUrl()));

				}
			}
		}
		response.setDataList(offers);
		return response;
	}

	@PostMapping(value = PathProxy.OfferUrls.UPLOAD_IMAGE)
	@ApiOperation(value = PathProxy.OfferUrls.UPLOAD_IMAGE, notes = PathProxy.OfferUrls.UPLOAD_IMAGE)
	public Response<ImageURLResponse> uploadImages(@RequestBody FileDetails request,
			@RequestParam(required = false, value ="module", defaultValue = "offer") String module) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ImageURLResponse image = offerService.uploadImages(request, module);
		Response<ImageURLResponse> response = new Response<ImageURLResponse>();
		response.setData(image);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}

}
