package com.dpdocter.services.impl;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.VerifiedDocuments;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.VerifiedDocumentsCollection;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.VerifiedDocumentsRepository;
import com.dpdocter.request.VerifiedDocumentRequest;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.VerifiedDocumentsService;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
public class VerifiedDocumentsServiceImpl implements VerifiedDocumentsService {

	private static Logger logger = LogManager.getLogger(VerifiedDocumentsServiceImpl.class.getName());

	@Autowired
	private VerifiedDocumentsRepository verifiedDocumentsRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private FileManager fileManager;

	@Value(value = "${image.path}")
	private String imagePath;

	@Transactional
	@Override
	public VerifiedDocuments addDocumentsMultipart(FormDataBodyPart file, VerifiedDocumentRequest request) {
		VerifiedDocuments documents = null;
		try {
			UserCollection userCollection = null;
			LocationCollection locationCollection = null;
			if (request.getDoctorId() != null)
				userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			if (request.getLocationId() != null)
				locationCollection = locationRepository.findById(new ObjectId(request.getLocationId())).orElse(null);
			if (locationCollection != null || userCollection != null) {
				String path = null;
				Date createdTime = new Date();

				VerifiedDocumentsCollection documentsCollection = null, oldRecord = null;
				if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
					documentsCollection = verifiedDocumentsRepository.findById(new ObjectId(request.getId())).orElse(null);
					oldRecord = documentsCollection;
				}

				if (documentsCollection == null)
					documentsCollection = new VerifiedDocumentsCollection();
				BeanUtil.map(request, documentsCollection);
				if (!DPDoctorUtils.anyStringEmpty(request.getDocumentsUrl())) {
					String documentsURL = request.getDocumentsUrl().replaceAll(imagePath, "");
					documentsCollection.setDocumentsUrl(documentsURL);
					documentsCollection.setDocumentsPath(documentsURL);
					documentsCollection.setDocumentsLabel(
							FilenameUtils.getBaseName(documentsURL).substring(0, documentsURL.length() - 13));
				}
				if (file != null) {

					if (request.getDoctorId() != null)

						path = "documents" + File.separator + request.getDoctorId();
					else
						path = "documents" + File.separator + request.getLocationId();
					FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
					String documentPath = path + File.separator + fileDetail.getFileName().split("[.]")[0]
							+ createdTime.getTime() + fileDetail.getFileName().split("[.]")[1];
					String documentLabel = fileDetail.getFileName();
					fileManager.saveRecord(file, documentPath);
					documentsCollection.setDocumentsUrl(documentPath);
					documentsCollection.setDocumentsPath(documentPath);
					documentsCollection.setDocumentsLabel(documentLabel);
				}

				if (oldRecord != null) {
					documentsCollection.setCreatedTime(oldRecord.getCreatedTime());
					documentsCollection.setCreatedBy(oldRecord.getCreatedBy());
					documentsCollection.setDiscarded(oldRecord.getDiscarded());
					documentsCollection.setUniqueEmrId(oldRecord.getUniqueEmrId());

				} else {
					documentsCollection
							.setUniqueEmrId(UniqueIdInitial.DOCUMENTS.getInitial() + DPDoctorUtils.generateRandomId());
					documentsCollection.setCreatedTime(createdTime);

				}

				documentsCollection = verifiedDocumentsRepository.save(documentsCollection);
				documents = new VerifiedDocuments();
				BeanUtil.map(documentsCollection, documents);

			} else
				throw new BusinessException(ServiceError.Unknown, " illegal location or doctor");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return documents;
	}

	@Override
	public Boolean updateVerified(String documentId, Boolean isVerified) {
		Boolean response = false;
		try {
			VerifiedDocumentsCollection documentsCollection = verifiedDocumentsRepository
					.findById(new ObjectId(documentId)).orElse(null);
			if (documentsCollection != null) {
				documentsCollection.setIsVerified(isVerified);
				documentsCollection = verifiedDocumentsRepository.save(documentsCollection);
				response = true;
			}

			else
				throw new BusinessException(ServiceError.Unknown, "Invalid documentId");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return response;
	}
}
