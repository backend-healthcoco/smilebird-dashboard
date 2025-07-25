package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.UserDevice;
import com.dpdocter.collections.UserDeviceCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.DeviceType;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.UserDeviceRepository;
import com.dpdocter.services.PushNotificationServices;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.util.web.DPDoctorUtils;

@Service
public class PushNotificationServicesImpl implements PushNotificationServices {

	private static Logger logger = LogManager.getLogger(PushNotificationServicesImpl.class.getName());

	@Value("${doctor.android.google.services.api.key}")
	private String DOCTOR_GEOCODING_SERVICES_API_KEY;

	@Value("${pharmist.android.google.services.api.key}")
	private String PHARMIST_GEOCODING_SERVICES_API_KEY;

	@Value("${patient.android.google.services.api.key}")
	private String PATIENT_GEOCODING_SERVICES_API_KEY;

	@Value("${doctor.pad.android.google.services.api.key}")
	private String DOCTOR_PAD_GEOCODING_SERVICES_API_KEY;

	@Value("${ios.certificate.password.doctorApp}")
	private String iosCertificatePasswordDoctorApp;

	@Value("${ios.certificate.filename.doctorApp}")
	private String iosCertificateFileNameDoctorApp;

	@Value("${ios.certificate.password.patientApp}")
	private String iosCertificatePasswordPatientApp;

	@Value("${ios.certificate.filename.patientApp}")
	private String iosCertificateFileNamePatientApp;

	@Value("${ipad.certificate.password.doctorApp}")
	private String ipadCertificatePasswordDoctorApp;

	@Value("${ipad.certificate.filename.doctorApp}")
	private String ipadCertificateFileNameDoctorApp;

	@Value("${ipad.certificate.password.patientApp}")
	private String ipadCertificatePasswordPatientApp;

	@Value("${ipad.certificate.filename.patientApp}")
	private String ipadCertificateFileNamePatientApp;

	@Value("${ios.notification.sound.filepath}")
	private String iosNotificationSoundFilepath;

	@Value("${doctor.ios.google.services.api.key}")
	private String DOCTOR_IOS_SERVICES_API_KEY;

	@Value("${patient.ios.google.services.api.key}")
	private String PATIENT_IOS_SERVICES_API_KEY;

	@Value("${is.env.production}")
	private Boolean isEnvProduction;

	@Autowired
	private UserDeviceRepository userDeviceRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value("${doctor.web.google.services.api.key}")
	private String DOCTOR_WEB_SERVICES_API_KEY;

	@Value("${admin.web.google.services.api.key}")
	private String ADMIN_WEB_SERVICES_API_KEY;

	@Override
	@Transactional
	public UserDevice addDevice(UserDevice request) {
		UserDevice response = null;
		System.out.println(request);
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getDeviceId())) {
				UserDeviceCollection userDeviceCollection = userDeviceRepository.findByDeviceId(request.getDeviceId());
				if (userDeviceCollection == null) {
					userDeviceCollection = new UserDeviceCollection();
					BeanUtil.map(request, userDeviceCollection);
					userDeviceCollection.setCreatedTime(new Date());
				} else {
					if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
						List<ObjectId> userIds = new ArrayList<ObjectId>();
						for (String userId : request.getUserIds()) {
							userIds.add(new ObjectId(userId));
						}
						userDeviceCollection.setUserIds(userIds);
					} else {
						userDeviceCollection.setUserIds(null);
					}
					userDeviceCollection.setDeviceId(request.getDeviceId());
					userDeviceCollection.setPushToken(request.getPushToken());
					userDeviceCollection.setDeviceType(request.getDeviceType());
					userDeviceCollection.setRole(request.getRole());
					userDeviceCollection.setUpdatedTime(new Date());
				}

				userDeviceRepository.save(userDeviceCollection);
				response = new UserDevice();
				BeanUtil.map(userDeviceCollection, response);
			} else {
				logger.error("Device ID cannot be null");
				throw new BusinessException(ServiceError.InvalidInput, "Device ID cannot be null");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while adding device : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while adding device : " + e.getCause().getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public void notifyUser(String userId, String message, String componentType, String componentTypeId,
			List<UserDeviceCollection> userDevices) {
		List<UserDeviceCollection> userDeviceCollections = null;

	}

	public void pushNotificationOnWebDevices(String deviceId, String pushToken, String message, String componentType,
			String componentTypeId, String deviceType, String role, String userId) {
		try {

//		FCMSender sender = new FCMSender(DOCTOR_WEB_SERVICES_API_KEY);
			JSONObject data = new JSONObject();
			JSONObject info = new JSONObject();
			ObjectMapper mapper = new ObjectMapper();
			Boolean isSilent = false;
			Map<String, Object> customValues = new HashMap<String, Object>();
			if (!DPDoctorUtils.anyStringEmpty(componentType)) {
				if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType())) {
					// customValues.put("XI", componentTypeId);
					// customValues.put("T", "X");
					// customValues.put("PI", userId);

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("XI", componentTypeId);
					info.put("PI", userId);// Notification body
					info.put("sound", "default");
					info.put("priority", "high");
					data.put("notification", info);

				} else if (componentType.equalsIgnoreCase(ComponentType.REPORTS.getType())) {
//						customValues.put("RI", componentTypeId);
//						customValues.put("T", "R");
//						customValues.put("PI", userId);

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("RI", componentTypeId);
					info.put("PI", userId);// Notification body
					info.put("sound", "default");
					data.put("notification", info);

				} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT.getType())) {
//						customValues.put("PI", componentTypeId);
//						customValues.put("T", "P");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("RI", componentTypeId);
					info.put("PI", userId);// Notification body
					info.put("sound", "default");
					data.put("notification", info);

				} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR.getType())) {
					customValues.put("DI", componentTypeId);
					customValues.put("T", "D");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("DI", componentTypeId);
					// info.put("PI",userId);// Notification body
					info.put("sound", "default");
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT.getType())) {
					customValues.put("AI", componentTypeId);
					customValues.put("T", "A");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("AI", componentTypeId);
					info.put("sound", "default");
					info.put("priority", "high");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.CALENDAR_REMINDER.getType())) {
					// customValues.put("T", "C");
					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("sound", "default");
					// info.put("body", message);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR_LAB_REPORTS.getType())) {
					// customValues.put("RI", componentTypeId);
					// customValues.put("T", "DLR");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("RI", componentTypeId);
					info.put("sound", "default");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.USER_RECORD.getType())) {
					// customValues.put("RI", componentTypeId);
					// customValues.put("T", "UR");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("RI", componentTypeId);
					info.put("sound", "default");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);

				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DOCTOR_LAB_REPORTS.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "SI");
					isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "SI");
					info.put("content_available", true);
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("RI", componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);

				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORKS.getType())) {

					customValues.put("RI", componentTypeId);
					customValues.put("T", "DW");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("RI", componentTypeId);
					info.put("sound", "default");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DI");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("sound", "default");
					info.put("RI", componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					customValues.put("EI", componentTypeId);
					customValues.put("T", "E");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("EI", componentTypeId);
					info.put("sound", "default");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "PR");

					data.put("to", pushToken.trim());
					info.put("T", "PR");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "RDI");
					isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RDI");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
//						customValues.put("RI", "SILENT");
//						customValues.put("T", "DWR");
//						isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "DWR");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTION_REFRESH.getType())) {
//						customValues.put("PI",componentTypeId);
//						customValues.put("T", "RX");
//						isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RX");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);

					info.put("content_available", true);
					info.put("PI", componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_VISIT_REFRESH.getType())) {
					// customValues.put("PI",componentTypeId);
					// customValues.put("T", "RPV");
					// isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RPV");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);

					info.put("content_available", true);
					info.put("PI", componentTypeId);

					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.CLINICAL_NOTES_REFRESH.getType())) {
					customValues.put("PI", componentTypeId);
					customValues.put("T", "RCN");
					isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RCN");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);

					info.put("content_available", true);
					info.put("PI", componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.TREATMENTS_REFRESH.getType())) {
//						customValues.put("PI",componentTypeId);
//						customValues.put("T", "RT");
//						isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RT");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);

					info.put("content_available", true);
					info.put("PI", componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.RECORDS_REFRESH.getType())) {
//						customValues.put("PI",componentTypeId);
//						customValues.put("T", "RR");
//						isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RR");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);

					info.put("content_available", true);
					info.put("PI", componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DISCHARGE_SUMMARY_REFRESH.getType())) {
//						customValues.put("PI",componentTypeId);
//						customValues.put("T", "RDS");
//						isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RDS");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					info.put("PI", componentTypeId);

					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.INVOICE_REFRESH.getType())) {
					customValues.put("PI", componentTypeId);
					customValues.put("T", "RBI");
					isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RBI");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					info.put("PI", componentTypeId);

					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.RECEIPT_REFRESH.getType())) {
					customValues.put("PI", componentTypeId);
					customValues.put("T", "RBR");
					isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RBR");
//			            info.put("title",componentType ); // Notification title
//			            info.put("body", message);
					info.put("content_available", true);
					info.put("PI", componentTypeId);

					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT_REFRESH.getType())) {
					// customValues.put("AI",componentTypeId);
					// customValues.put("T", "AR");

					isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "AR");
					// info.put("title",componentType); // Notification title
					// info.put("body", message);
					info.put("content_available", true);

					info.put("PI", componentTypeId);

					// info.put("PI",userId);// Notification body
					data.put("notification", info);

				} else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT_STATUS_CHANGE.getType())) {
					customValues.put("AI", componentTypeId);
					customValues.put("T", "ASC");
					isSilent = true;

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("sound", "default");
					info.put("AI", componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DI");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("RI", componentTypeId);
					info.put("sound", "default");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					customValues.put("EI", componentTypeId);
					customValues.put("T", "E");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("EI", componentTypeId);
					info.put("sound", "default");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "PR");

					data.put("to", pushToken.trim());
					info.put("T", "PR");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DI");

					data.put("to", pushToken.trim());
					info.put("T", "RDI");
					info.put("content_available", true);
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DW");

					data.put("to", pushToken.trim());
					info.put("T", "RDW");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DI");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("RI", componentTypeId);
					info.put("sound", "default");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					customValues.put("EI", componentTypeId);
					customValues.put("T", "E");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("EI", componentTypeId);
					info.put("sound", "default");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "PR");

					data.put("to", pushToken.trim());
					info.put("T", "PR");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DI");

					data.put("to", pushToken.trim());
					info.put("T", "RDI");
					info.put("content_available", true);
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "RDW");

					data.put("to", pushToken.trim());
					info.put("T", "RDW");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_BABY_ACHIEVEMENTS.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "BA");

					data.put("to", pushToken.trim());
					info.put("T", "RBA");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_GROWTH_CHART.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "GC");

					data.put("to", pushToken.trim());
					info.put("T", "RGC");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_VACCINATION.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "VN");

					data.put("to", pushToken.trim());
					info.put("T", "RVN");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.SIGNED_UP.getType())) {
					customValues.put("SI", componentTypeId);

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("SI", componentTypeId);
					info.put("sound", "default");
					info.put("priority", "high");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.EMAIL_VERIFICATION.getType())) {
					customValues.put("EVI", componentTypeId);

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("EVI", componentTypeId);
					info.put("sound", "default");
					info.put("priority", "high");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DEACTIVATED.getType())) {
					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("RI", componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else {
					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				}
			}

			String url = "https://fcm.googleapis.com/fcm/send";
//				 
//				// String authStringEnc = Base64.getEncoder().encodeToString(authStr.getBytes());
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			//
//				
			con.setDoOutput(true);
//				
//				System.out.println(con.getErrorStream());
			con.setDoInput(true);
//				// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "key=" + DOCTOR_WEB_SERVICES_API_KEY);

			System.out.println(data.toString());
			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(data.toString());
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			System.out.println("Resonse: " + response);

			System.out.println("Response" + response);
			System.out.println("pushToken" + pushToken);

		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pushNotificationOnAdminWebDevices(String deviceId, String pushToken, String message,
			String componentType, String componentTypeId, String deviceType, String role, String userId) {
		try {

//		FCMSender sender = new FCMSender(DOCTOR_WEB_SERVICES_API_KEY);
			JSONObject data = new JSONObject();
			JSONObject info = new JSONObject();
			ObjectMapper mapper = new ObjectMapper();
			Boolean isSilent = false;
			Map<String, Object> customValues = new HashMap<String, Object>();
			if (!DPDoctorUtils.anyStringEmpty(componentType)) {
				if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType())) {
					// customValues.put("XI", componentTypeId);
					// customValues.put("T", "X");
					// customValues.put("PI", userId);

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("XI", componentTypeId);
					info.put("PI", userId);// Notification body
					info.put("sound", "default");
					info.put("priority", "high");
					data.put("notification", info);

				} else if (componentType.equalsIgnoreCase(ComponentType.REPORTS.getType())) {
//						customValues.put("RI", componentTypeId);
//						customValues.put("T", "R");
//						customValues.put("PI", userId);

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("RI", componentTypeId);
					info.put("PI", userId);// Notification body
					info.put("sound", "default");
					data.put("notification", info);

				} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT.getType())) {
//						customValues.put("PI", componentTypeId);
//						customValues.put("T", "P");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("RI", componentTypeId);
					info.put("PI", userId);// Notification body
					info.put("sound", "default");
					data.put("notification", info);

				} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR.getType())) {
					customValues.put("DI", componentTypeId);
					customValues.put("T", "D");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("DI", componentTypeId);
					// info.put("PI",userId);// Notification body
					info.put("sound", "default");
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT.getType())) {
					customValues.put("AI", componentTypeId);
					customValues.put("T", "A");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("AI", componentTypeId);
					info.put("sound", "default");
					info.put("priority", "high");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.CALENDAR_REMINDER.getType())) {
					// customValues.put("T", "C");
					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("sound", "default");
					// info.put("body", message);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR_LAB_REPORTS.getType())) {
					// customValues.put("RI", componentTypeId);
					// customValues.put("T", "DLR");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("RI", componentTypeId);
					info.put("sound", "default");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.USER_RECORD.getType())) {
					// customValues.put("RI", componentTypeId);
					// customValues.put("T", "UR");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("RI", componentTypeId);
					info.put("sound", "default");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);

				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DOCTOR_LAB_REPORTS.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "SI");
					isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "SI");
					info.put("content_available", true);
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("RI", componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);

				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORKS.getType())) {

					customValues.put("RI", componentTypeId);
					customValues.put("T", "DW");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("RI", componentTypeId);
					info.put("sound", "default");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DI");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("sound", "default");
					info.put("RI", componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					customValues.put("EI", componentTypeId);
					customValues.put("T", "E");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("EI", componentTypeId);
					info.put("sound", "default");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "PR");

					data.put("to", pushToken.trim());
					info.put("T", "PR");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "RDI");
					isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RDI");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
//						customValues.put("RI", "SILENT");
//						customValues.put("T", "DWR");
//						isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "DWR");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTION_REFRESH.getType())) {
//						customValues.put("PI",componentTypeId);
//						customValues.put("T", "RX");
//						isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RX");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);

					info.put("content_available", true);
					info.put("PI", componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_VISIT_REFRESH.getType())) {
					// customValues.put("PI",componentTypeId);
					// customValues.put("T", "RPV");
					// isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RPV");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);

					info.put("content_available", true);
					info.put("PI", componentTypeId);

					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.CLINICAL_NOTES_REFRESH.getType())) {
					customValues.put("PI", componentTypeId);
					customValues.put("T", "RCN");
					isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RCN");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);

					info.put("content_available", true);
					info.put("PI", componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.TREATMENTS_REFRESH.getType())) {
//						customValues.put("PI",componentTypeId);
//						customValues.put("T", "RT");
//						isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RT");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);

					info.put("content_available", true);
					info.put("PI", componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.RECORDS_REFRESH.getType())) {
//						customValues.put("PI",componentTypeId);
//						customValues.put("T", "RR");
//						isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RR");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);

					info.put("content_available", true);
					info.put("PI", componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DISCHARGE_SUMMARY_REFRESH.getType())) {
//						customValues.put("PI",componentTypeId);
//						customValues.put("T", "RDS");
//						isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RDS");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					info.put("PI", componentTypeId);

					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.INVOICE_REFRESH.getType())) {
					customValues.put("PI", componentTypeId);
					customValues.put("T", "RBI");
					isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RBI");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					info.put("PI", componentTypeId);

					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.RECEIPT_REFRESH.getType())) {
					customValues.put("PI", componentTypeId);
					customValues.put("T", "RBR");
					isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "RBR");
//			            info.put("title",componentType ); // Notification title
//			            info.put("body", message);
					info.put("content_available", true);
					info.put("PI", componentTypeId);

					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT_REFRESH.getType())) {
					// customValues.put("AI",componentTypeId);
					// customValues.put("T", "AR");

					isSilent = true;

					data.put("to", pushToken.trim());
					info.put("T", "AR");
					// info.put("title",componentType); // Notification title
					// info.put("body", message);
					info.put("content_available", true);

					info.put("PI", componentTypeId);

					// info.put("PI",userId);// Notification body
					data.put("notification", info);

				} else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT_STATUS_CHANGE.getType())) {
					customValues.put("AI", componentTypeId);
					customValues.put("T", "ASC");
					isSilent = true;

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("sound", "default");
					info.put("AI", componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DI");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("RI", componentTypeId);
					info.put("sound", "default");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					customValues.put("EI", componentTypeId);
					customValues.put("T", "E");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("EI", componentTypeId);
					info.put("sound", "default");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "PR");

					data.put("to", pushToken.trim());
					info.put("T", "PR");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DI");

					data.put("to", pushToken.trim());
					info.put("T", "RDI");
					info.put("content_available", true);
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DW");

					data.put("to", pushToken.trim());
					info.put("T", "RDW");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DI");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("RI", componentTypeId);
					info.put("sound", "default");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					customValues.put("EI", componentTypeId);
					customValues.put("T", "E");

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("EI", componentTypeId);
					info.put("sound", "default");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "PR");

					data.put("to", pushToken.trim());
					info.put("T", "PR");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DI");

					data.put("to", pushToken.trim());
					info.put("T", "RDI");
					info.put("content_available", true);
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "RDW");

					data.put("to", pushToken.trim());
					info.put("T", "RDW");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_BABY_ACHIEVEMENTS.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "BA");

					data.put("to", pushToken.trim());
					info.put("T", "RBA");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_GROWTH_CHART.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "GC");

					data.put("to", pushToken.trim());
					info.put("T", "RGC");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_VACCINATION.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "VN");

					data.put("to", pushToken.trim());
					info.put("T", "RVN");
					// info.put("title",componentType ); // Notification title
					// info.put("body", message);
					info.put("content_available", true);
					// info.put("RI",componentTypeId);
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.SIGNED_UP.getType())) {
					customValues.put("SI", componentTypeId);

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("SI", componentTypeId);
					info.put("sound", "default");
					info.put("priority", "high");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.EMAIL_VERIFICATION.getType())) {
					customValues.put("EVI", componentTypeId);

					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					info.put("EVI", componentTypeId);
					info.put("sound", "default");
					info.put("priority", "high");
					// info.put("PI",userId);// Notification body
					data.put("notification", info);
				} else {
					data.put("to", pushToken.trim());
					info.put("title", componentType); // Notification title
					info.put("body", message);
					// info.put("RI",componentTypeId);
					info.put("PI", userId);// Notification body
					data.put("notification", info);
				}
			}

			String url = "https://fcm.googleapis.com/fcm/send";
//				 
//				// String authStringEnc = Base64.getEncoder().encodeToString(authStr.getBytes());
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			//
//				
			con.setDoOutput(true);
//				
//				System.out.println(con.getErrorStream());
			con.setDoInput(true);
//				// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "key=" + ADMIN_WEB_SERVICES_API_KEY);

			System.out.println(data.toString());
			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(data.toString());
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			System.out.println("Resonse: " + response);

			System.out.println("Response" + response);
			System.out.println("pushToken" + pushToken);

		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	@Transactional
	public void readNotification(String deviceId, Integer count) {
		// Boolean response = false;
		try {
			UserDeviceCollection userDeviceCollection = userDeviceRepository.findByDeviceId(deviceId);
			if (userDeviceCollection != null) {
				if (count > 0 && userDeviceCollection.getBatchCount() > 0)
					userDeviceCollection.setBatchCount(userDeviceCollection.getBatchCount() - count);
				else if (count == 0)
					userDeviceCollection.setBatchCount(0);
				userDeviceRepository.save(userDeviceCollection);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return response;

	}

	@Override
	public Boolean notifyRefresh(String id, String requestId, String responseId, RoleEnum role, String message,
			ComponentType componentType) {
		Boolean response = false;
		List<UserDeviceCollection> userDeviceCollections = null;

		try {

			userDeviceCollections = userDeviceRepository.findByUserIds(new ObjectId(id));

			if (userDeviceCollections != null && !userDeviceCollections.isEmpty()) {
				for (UserDeviceCollection userDeviceCollection : userDeviceCollections) {
					if (userDeviceCollection.getDeviceType() != null) {
						if (userDeviceCollection.getDeviceType().getType()
								.equalsIgnoreCase(DeviceType.ANDROID.getType())) {

							response = true;
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while pushing notification: " + e.getCause().getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean notifyAll(RoleEnum role, List<ObjectId> userIds, String message) {
		Boolean response = false;
		return response;
	}

}
