package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.FooterSetup;
import com.dpdocter.beans.PrintSettings;
import com.dpdocter.beans.PrintSettingsDefaultData;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.PrintSettingsDefaultDataCollection;
import com.dpdocter.enums.PrintFilter;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PrintSettingsDefaultDataRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.services.PrintSettingsService;

import common.util.web.DPDoctorUtils;

@Service
public class PrintSettingsServiceImpl implements PrintSettingsService {

	private static Logger logger = LogManager.getLogger(PrintSettingsServiceImpl.class.getName());

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private PrintSettingsDefaultDataRepository printSettingsDefaultDataRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	@Transactional
	public PrintSettingsDefaultData saveDefaultSettings(PrintSettingsDefaultData request) {
		PrintSettingsDefaultData response = null;
		PrintSettingsDefaultDataCollection printSettingsDefaultDataCollection = new PrintSettingsDefaultDataCollection();
		try {
			BeanUtil.map(request, printSettingsDefaultDataCollection);
			printSettingsDefaultDataCollection = printSettingsDefaultDataRepository
					.save(printSettingsDefaultDataCollection);
			response = new PrintSettingsDefaultData();
			BeanUtil.map(printSettingsDefaultDataCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while saving default settings");
			throw new BusinessException(ServiceError.Unknown, "Error occured while saving default settings");
		}
		return response;
	}

	@Override
	@Transactional
	public List<PrintSettingsDefaultData> getDefaultSettings() {
		List<PrintSettingsDefaultData> response = new ArrayList<PrintSettingsDefaultData>();
		List<PrintSettingsDefaultDataCollection> printSettingsDefaultDataCollection = null;
		try {
			printSettingsDefaultDataCollection = printSettingsDefaultDataRepository.findAll();
			BeanUtil.map(printSettingsDefaultDataCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while Getting default settings");
			throw new BusinessException(ServiceError.Unknown, "Error occured while Getting default settings");
		}
		return response;
	}

	@Override
	@Transactional
	public PrintSettings saveSettings(PrintSettings request) {
		PrintSettings response = new PrintSettings();
		PrintSettingsCollection printSettingsCollection = new PrintSettingsCollection();
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				doctorObjectId = new ObjectId(request.getDoctorId());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				locationObjectId = new ObjectId(request.getLocationId());
			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))
				hospitalObjectId = new ObjectId(request.getHospitalId());

			if (request.getId() == null) {
				PrintSettingsCollection collection = printSettingsRepository.findByDoctorIdAndLocationIdAndHospitalId(doctorObjectId,
						locationObjectId, hospitalObjectId);
				if (collection != null && !collection.getDiscarded()
						&& request.getComponentType().equals(collection.getComponentType()))
					request.setId(collection.getId().toString());
			}
			BeanUtil.map(request, printSettingsCollection);
			if (request.getId() == null) {
				printSettingsCollection.setCreatedTime(new Date());
			} else {
				PrintSettingsCollection oldPrintSettingsCollection = printSettingsRepository
						.findById(new ObjectId(request.getId())).orElse(null);
				if (oldPrintSettingsCollection != null) {
					printSettingsCollection.setCreatedTime(oldPrintSettingsCollection.getCreatedTime());
					printSettingsCollection.setCreatedBy(oldPrintSettingsCollection.getCreatedBy());
					printSettingsCollection.setDiscarded(oldPrintSettingsCollection.getDiscarded());

					if (request.getPageSetup() == null)
						printSettingsCollection.setPageSetup(oldPrintSettingsCollection.getPageSetup());

					if (request.getHeaderSetup() == null)
						printSettingsCollection.setHeaderSetup(oldPrintSettingsCollection.getHeaderSetup());

					if (request.getFooterSetup() == null)
						printSettingsCollection.setFooterSetup(oldPrintSettingsCollection.getFooterSetup());
				}

			}

			LocationCollection locationCollection = locationRepository.findById(new ObjectId(request.getLocationId())).orElse(null);
			if (locationCollection != null) {
				printSettingsCollection.setClinicLogoUrl(locationCollection.getLogoUrl());
			}
			printSettingsCollection = printSettingsRepository.save(printSettingsCollection);
			BeanUtil.map(printSettingsCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while saving settings");
			throw new BusinessException(ServiceError.Unknown, "Error occured while saving settings");
		}
		return response;
	}

	@Override
	@Transactional
	public List<PrintSettings> getSettings(String printFilter, String doctorId, String locationId, String hospitalId,
			int page, int size, String updatedTime, Boolean discarded) {
		List<PrintSettings> response = null;
		List<PrintSettingsCollection> printSettingsCollections = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;
		try {
			if (discarded)
				discards[1] = true;

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			long createdTimeStamp = Long.parseLong(updatedTime);
			if (doctorObjectId == null) {
				if (size > 0)
					printSettingsCollections = printSettingsRepository.findByUpdatedTimeGreaterThanAndDiscardedIn(new Date(createdTimeStamp), discards,
							PageRequest.of(page, size, Direction.DESC, "createdTime"));
				else
					printSettingsCollections = printSettingsRepository.findByUpdatedTimeGreaterThanAndDiscardedIn(new Date(createdTimeStamp), discards,
							new Sort(Sort.Direction.DESC, "createdTime"));

			} else {
				if (locationObjectId == null && hospitalObjectId == null) {
					if (size > 0)
						printSettingsCollections = printSettingsRepository.findByDoctorIdAndUpdatedTimeGreaterThanAndDiscardedIn(doctorObjectId,
								new Date(createdTimeStamp), discards,
								PageRequest.of(page, size, Direction.DESC, "createdTime"));
					else
						printSettingsCollections = printSettingsRepository.findByDoctorIdAndUpdatedTimeGreaterThanAndDiscardedIn(doctorObjectId,
								new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "createdTime"));
				} else {
					if (size > 0)
						printSettingsCollections = printSettingsRepository.findByDoctorIdAndLocationIdAndHospitalIdAndUpdatedTimeGreaterThanAndDiscardedIn(doctorObjectId, locationObjectId,
								hospitalObjectId, new Date(createdTimeStamp), discards,
								PageRequest.of(page, size, Direction.DESC, "createdTime"));
					else
						printSettingsCollections = printSettingsRepository.findByDoctorIdAndLocationIdAndHospitalIdAndUpdatedTimeGreaterThanAndDiscardedIn(doctorObjectId, locationObjectId,
								hospitalObjectId, new Date(createdTimeStamp), discards,
								new Sort(Sort.Direction.DESC, "createdTime"));
				}
			}
			if (printSettingsCollections != null) {
				response = new ArrayList<PrintSettings>();

				for (PrintSettingsCollection collection : printSettingsCollections) {
					PrintSettings printSettings = new PrintSettings();
					if (PrintFilter.PAGESETUP.getFilter().equalsIgnoreCase(printFilter)) {
						collection.setFooterSetup(null);
						collection.setHeaderSetup(null);
					}
					if (PrintFilter.HEADERSETUP.getFilter().equalsIgnoreCase(printFilter)) {
						collection.setFooterSetup(null);
						collection.setPageSetup(null);
					}
					if (PrintFilter.FOOTERSETUP.getFilter().equalsIgnoreCase(printFilter)) {
						collection.setPageSetup(null);
						collection.setHeaderSetup(null);
					}
					BeanUtil.map(collection, printSettings);
					response.add(printSettings);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Print Settings");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Print Settings");
		}
		return response;

	}

	@Override
	@Transactional
	public PrintSettings deletePrintSettings(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		PrintSettings response = null;
		try {
			PrintSettingsCollection printSettingsCollection = printSettingsRepository.findById(new ObjectId(id)).orElse(null);
			if (printSettingsCollection != null) {
				if (printSettingsCollection.getDoctorId() != null && printSettingsCollection.getHospitalId() != null
						&& printSettingsCollection.getLocationId() != null) {
					if (printSettingsCollection.getDoctorId().equals(doctorId)
							&& printSettingsCollection.getHospitalId().equals(hospitalId)
							&& printSettingsCollection.getLocationId().equals(locationId)) {
						printSettingsCollection.setDiscarded(discarded);
						printSettingsCollection.setUpdatedTime(new Date());
						printSettingsRepository.save(printSettingsCollection);
						response = new PrintSettings();
						BeanUtil.map(printSettingsCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
					throw new BusinessException(ServiceError.InvalidInput,
							"Invalid Doctor Id, Hospital Id, Or Location Id");
				}

			} else {
				logger.warn("Print Settings not found!");
				throw new BusinessException(ServiceError.NoRecord, "Print Settings not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean enablePoweredBy(String doctorId, String locationId, String hospitalId, Boolean showPoweredBy) {
		Boolean response = false;
		try {

			PrintSettingsCollection printSettingsCollection = printSettingsRepository
					.findByDoctorIdAndLocationIdAndHospitalId(new ObjectId(doctorId), new ObjectId(locationId), new ObjectId(hospitalId));
			if (printSettingsCollection != null) {
				if (printSettingsCollection.getFooterSetup() != null) {
					printSettingsCollection.getFooterSetup().setShowPoweredBy(showPoweredBy);
				} else {
					printSettingsCollection.setFooterSetup(new FooterSetup());
					printSettingsCollection.getFooterSetup().setShowPoweredBy(showPoweredBy);
				}
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while update Powered By settings");
			throw new BusinessException(ServiceError.Unknown, "Error occured while update PoweredBy settings");
		}
		return response;
	}
}
