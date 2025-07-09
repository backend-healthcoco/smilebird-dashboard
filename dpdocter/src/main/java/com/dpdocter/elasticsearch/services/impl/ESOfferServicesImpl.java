package com.dpdocter.elasticsearch.services.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.elasticsearch.document.ESOfferDocument;
import com.dpdocter.elasticsearch.document.ESTrendingDocument;
import com.dpdocter.elasticsearch.repository.ESOfferRepository;
import com.dpdocter.elasticsearch.repository.ESTrendingRepository;
import com.dpdocter.elasticsearch.services.ESOfferServices;
import com.dpdocter.enums.Resource;
import com.dpdocter.services.TransactionalManagementService;

@Service
public class ESOfferServicesImpl implements ESOfferServices {

	private static Logger logger = LogManager.getLogger(ESOfferServicesImpl.class.getName());

	@Autowired
	private ESOfferRepository esOfferRepsitory;

	@Autowired
	private ESTrendingRepository esTrendingRepository;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Override
	public boolean addOffer(ESOfferDocument request) {
		boolean response = false;
		try {
			esOfferRepsitory.save(request);
			response = true;
			transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.OFFER, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Offer in ES");
		}
		return response;
	}

	@Override
	public boolean addTrending(ESTrendingDocument request) {
		boolean response = false;
		try {
			esTrendingRepository.save(request);
			response = true;
			transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.TRENDING, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Offer in ES");
		}
		return response;
	}

}
