package com.dpdocter.elasticsearch.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.LocaleImage;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;
import com.dpdocter.elasticsearch.repository.ESUserLocaleRepository;
import com.dpdocter.elasticsearch.services.ESLocaleService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.services.TransactionalManagementService;

@Service
public class ESLocaleServiceImpl implements ESLocaleService {
	private static Logger logger = LogManager.getLogger(ESLocaleServiceImpl.class.getName());

	@Autowired
	private ESUserLocaleRepository esUserLocaleRepository;

	@Autowired
	private TransactionalManagementService transactionalService;

	@Override
	public Boolean updateGeoPoint(String localeId, Address address) {
		Boolean response = false;
		ESUserLocaleDocument esUserLocale = esUserLocaleRepository.findByLocaleId(localeId);
		if (esUserLocale == null) {
			throw new BusinessException(ServiceError.NoRecord, "Record for id not found");
		} else {
			response = true;

			esUserLocale.setGeoPoint(new GeoPoint(address.getLatitude(), address.getLongitude()));
			esUserLocale.setAddress(address);
			esUserLocale = esUserLocaleRepository.save(esUserLocale);
		}
		return response;
	}

	@Override
	public Boolean updateLocale(String localeId, LocaleCollection localeCollection) {
		Boolean response = false;
		ESUserLocaleDocument esUserLocale = esUserLocaleRepository.findById(localeId).orElse(null);
		;
		if (esUserLocale == null) {
			throw new BusinessException(ServiceError.NoRecord, "Record for id not found");
		} else {
			response = true;

			BeanUtil.map(localeCollection, esUserLocale);
			if (localeCollection.getLocaleImages() != null && !localeCollection.getLocaleImages().isEmpty()) {
				List<LocaleImage> images = new ArrayList<LocaleImage>();
				for (LocaleImage Image : localeCollection.getLocaleImages()) {
					images.add(Image);
				}
				esUserLocale.setLocaleImages(images);

			}

			BeanUtil.map(localeCollection, esUserLocale);
			if (localeCollection.getLocaleWorkingSchedules() != null && !localeCollection.getLocaleImages().isEmpty()) {
				List<LocaleImage> images = new ArrayList<LocaleImage>();
				for (LocaleImage Image : localeCollection.getLocaleImages()) {
					images.add(Image);
				}
				esUserLocale.setLocaleImages(images);

			}

			esUserLocale = esUserLocaleRepository.save(esUserLocale);
		}
		return response;
	}

	@Override
	public boolean addLocale(ESUserLocaleDocument request) {
		boolean response = false;
		try {
			if (request.getAddress() != null && request.getAddress().getLatitude() != null
					&& request.getAddress().getLongitude() != null)
				request.setGeoPoint(
						new GeoPoint(request.getAddress().getLatitude(), request.getAddress().getLongitude()));
			esUserLocaleRepository.save(request);
			response = true;
			transactionalService.addResource(new ObjectId(request.getId()), Resource.PHARMACY, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving User Locale");
		}
		return response;
	}
}
