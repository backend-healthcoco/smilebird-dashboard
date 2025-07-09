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

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Notification;
import com.dpdocter.beans.UserDevice;
import com.dpdocter.collections.PushNotificationCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserDeviceCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.DeviceType;
import com.dpdocter.enums.PushNotificationType;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PushNotificationRepository;
import com.dpdocter.repository.UserDeviceRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.BroadcastNotificationRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.PushNotificationServices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

import common.util.web.DPDoctorUtils;
import common.util.web.FCMSender;

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
	
//	@Value("${doctor.android.google.services.server.key}")
//	private String DOCTOR_ANDROID_SERVICES_SERVER_KEY;
//	
//	@Value("${patient.android.google.services.server.key}")
//	private String PATIENT_ANDROID_SERVICES_SERVER_KEY;

	@Value("${is.env.production}")
	private Boolean isEnvProduction;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private UserDeviceRepository userDeviceRepository;

	@Autowired
	private PushNotificationRepository pushNotificationRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

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
				if (request.getRole().getRole().equalsIgnoreCase(RoleEnum.PATIENT.getRole())
						&& !DPDoctorUtils.anyStringEmpty(request.getMobileNumber())) {
					List<UserCollection> userCollections = userRepository.findByMobileNumber(request.getMobileNumber());
					if (userCollections != null) {
						List<ObjectId> userIds = new ArrayList<ObjectId>();
						for (UserCollection userCollection : userCollections) {
							if (userCollection.getEmailAddress() == null)
								userIds.add(userCollection.getId());
							else if (!userCollection.getEmailAddress().equalsIgnoreCase(userCollection.getUserName())) {
								userIds.add(userCollection.getId());
							}
						}
						userDeviceCollection.setUserIds(userIds);
					}
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
		try {
			if (!DPDoctorUtils.anyStringEmpty(userId)) {
				ObjectId userObjectId = new ObjectId(userId);
				userDeviceCollections = userDeviceRepository.findByUserIds(userObjectId);
			} else {
				userDeviceCollections = userDevices;
			}

			if (userDeviceCollections != null && !userDeviceCollections.isEmpty()) {
				for (UserDeviceCollection userDeviceCollection : userDeviceCollections) {
					if (userDeviceCollection.getDeviceType() != null) {
						if (userDeviceCollection.getDeviceType().getType()
								.equalsIgnoreCase(DeviceType.ANDROID.getType())
								|| userDeviceCollection.getDeviceType().getType()
										.equalsIgnoreCase(DeviceType.ANDROID_PAD.getType()))
							pushNotificationOnAndroidDevices(userDeviceCollection.getDeviceId(),
									userDeviceCollection.getPushToken(), message, componentType, componentTypeId,
									userDeviceCollection.getRole().getRole(), userId,
									userDeviceCollection.getDeviceType().getType());
						else if (userDeviceCollection.getDeviceType().getType()
								.equalsIgnoreCase(DeviceType.IOS.getType())
								|| userDeviceCollection.getDeviceType().getType()
										.equalsIgnoreCase(DeviceType.IPAD.getType())) {
							pushNotificationOnIosDevices(userDeviceCollection.getDeviceId(),
									userDeviceCollection.getPushToken(), message, componentType, componentTypeId,
									userDeviceCollection.getDeviceType().getType(),
									userDeviceCollection.getRole().getRole(), userId);
							userDeviceCollection.setBatchCount(userDeviceCollection.getBatchCount() + 1);
							userDeviceRepository.save(userDeviceCollection);
						}
							else if (userDeviceCollection.getDeviceType().getType()
									.equalsIgnoreCase(DeviceType.WEB.getType())
									|| userDeviceCollection.getDeviceType().getType()
											.equalsIgnoreCase(DeviceType.WEB.getType())) {
								pushNotificationOnWebDevices(userDeviceCollection.getDeviceId(),
										userDeviceCollection.getPushToken(), message, componentType, componentTypeId,
										userDeviceCollection.getDeviceType().getType(),
										userDeviceCollection.getRole().getRole(), userId);
								userDeviceCollection.setBatchCount(userDeviceCollection.getBatchCount() + 1);
								userDeviceRepository.save(userDeviceCollection);
	
						}
//							else if (userDeviceCollection.getDeviceType().getType()
//									.equalsIgnoreCase(DeviceType.WEB_ADMIN.getType())
//									|| userDeviceCollection.getDeviceType().getType()
//											.equalsIgnoreCase(DeviceType.WEB_ADMIN.getType())) {
//								pushNotificationOnAdminWebDevices(userDeviceCollection.getDeviceId(),
//										userDeviceCollection.getPushToken(), message, componentType, componentTypeId,
//										userDeviceCollection.getDeviceType().getType(),
//										userDeviceCollection.getRole().getRole(), userId);
//								userDeviceCollection.setBatchCount(userDeviceCollection.getBatchCount() + 1);
//								userDeviceRepository.save(userDeviceCollection);
//	
//						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while pushing notification: " + e.getCause().getMessage());
		}
		// return response;
	}

	public void pushNotificationOnAndroidDevices(String deviceId, String pushToken, String message,
			String componentType, String componentTypeId, String role, String userId, String deviceType) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Sender sender = null;

			if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
				// sender = new Sender(DOCTOR_GEOCODING_SERVICES_API_KEY);
				if (deviceType.equalsIgnoreCase(DeviceType.ANDROID.getType())) {
					sender = new FCMSender(DOCTOR_GEOCODING_SERVICES_API_KEY);
				}
				else {
					sender = new FCMSender(DOCTOR_PAD_GEOCODING_SERVICES_API_KEY);
				}

			} else {
				// sender = new Sender(PATIENT_GEOCODING_SERVICES_API_KEY);
				sender = new FCMSender(PATIENT_GEOCODING_SERVICES_API_KEY);
			}
			Notification notification = new Notification();
			// notification.setTitle("Healthcoco");
			notification.setText(message);
			if (!DPDoctorUtils.anyStringEmpty(componentType)) {
				if (componentType.equalsIgnoreCase(ComponentType.PATIENT.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR.getType())) {
					notification.setDi(componentTypeId);
					notification.setNotificationType(componentType);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.ORDER_CONFIRMED.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.ORDER_DELIVERED.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.ORDER_DISPATCHED.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.ORDER_OUT_FOR_DELIVERY.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.ORDER_PACKED.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.ORDER_PICKED_UP.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.ORDER_PLACED.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}
				else{
					notification.setNotificationType(componentType);
				}
			}
			String jsonOutput = mapper.writeValueAsString(notification);
			Message messageObj = new Message.Builder().delayWhileIdle(true).addData("message", jsonOutput).build();

			Result result = sender.send(messageObj, pushToken, 1);
			List<String> deviceIds = new ArrayList<String>();
			deviceIds.add(deviceId);
			PushNotificationCollection pushNotificationCollection = new PushNotificationCollection(null, deviceIds,
					message, DeviceType.ANDROID, null, PushNotificationType.INDIVIDUAL);
			pushNotificationRepository.save(pushNotificationCollection);
			logger.info("Message Result: " + result.toString());
		} catch (JsonProcessingException jpe) {
			jpe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void broadcastPushNotificationOnAndroidDevices(List<String> deviceIds, List<String> pushTokens,
			String message, String imageURL,String str, String role, String deviceType) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Sender sender = null;
			if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
				// sender = new Sender(DOCTOR_GEOCODING_SERVICES_API_KEY);
				if (deviceType.equalsIgnoreCase(DeviceType.ANDROID.getType())) {
					sender = new FCMSender(DOCTOR_GEOCODING_SERVICES_API_KEY);
				} else {
					sender = new FCMSender(DOCTOR_PAD_GEOCODING_SERVICES_API_KEY);
				}

			} else if (role.equalsIgnoreCase(RoleEnum.PHARMIST.getRole())) {
				// sender = new Sender(DOCTOR_GEOCODING_SERVICES_API_KEY);
				sender = new FCMSender(PHARMIST_GEOCODING_SERVICES_API_KEY);
			} else {
				// sender = new Sender(PATIENT_GEOCODING_SERVICES_API_KEY);
				sender = new FCMSender(PATIENT_GEOCODING_SERVICES_API_KEY);
			}

			Notification notification = new Notification();

			// notification.setTitle("Healthcoco");
			notification.setText(message);
			
			notification.setSounds("default");

			if (!DPDoctorUtils.anyStringEmpty(imageURL))
				notification.setImg(imageURL);

			String jsonOutput = mapper.writeValueAsString(notification);
			Message messageObj = new Message.Builder().delayWhileIdle(true).addData("message", jsonOutput).addData("sound", "default").build();

			MulticastResult result = sender.send(messageObj, pushTokens, 1);
			PushNotificationCollection pushNotificationCollection = new PushNotificationCollection(null, deviceIds,
					message, DeviceType.ANDROID, imageURL, PushNotificationType.BROADCAST);
			pushNotificationRepository.save(pushNotificationCollection);
			logger.info("Message Result: " + result.toString());
		} catch (JsonProcessingException jpe) {
			jpe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


//	public void pushNotificationOnIosDevices(String deviceId, String pushToken, String message, String componentType,
//			String componentTypeId, String deviceType, String role, String userId) {
//		try {
//			ApnsService service = null;
//			if (isEnvProduction) {
//				if (deviceType.equalsIgnoreCase(DeviceType.IOS.getType())) {
//					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
//						service = APNS.newService()
//								.withCert(iosCertificateFileNameDoctorApp, iosCertificatePasswordDoctorApp)
//								.withProductionDestination().build();
//					} else {
//						service = APNS.newService()
//								.withCert(iosCertificateFileNamePatientApp, iosCertificatePasswordPatientApp)
//								.withProductionDestination().build();
//					}
//				} else {
//					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
//						service = APNS.newService()
//								.withCert(ipadCertificateFileNameDoctorApp, ipadCertificatePasswordDoctorApp)
//								.withProductionDestination().build();
//					} else {
//						service = APNS.newService()
//								.withCert(ipadCertificateFileNamePatientApp, ipadCertificatePasswordPatientApp)
//								.withProductionDestination().build();
//					}
//				}
//			} else {
//				if (deviceType.equalsIgnoreCase(DeviceType.IOS.getType())) {
//					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
//						service = APNS.newService()
//								.withCert(iosCertificateFileNameDoctorApp, iosCertificatePasswordDoctorApp)
//								.withSandboxDestination().build();
//					} else {
//						service = APNS.newService()
//								.withCert(iosCertificateFileNamePatientApp, iosCertificatePasswordPatientApp)
//								.withSandboxDestination().build();
//					}
//				} else {
//					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
//						service = APNS.newService()
//								.withCert(ipadCertificateFileNameDoctorApp, ipadCertificatePasswordDoctorApp)
//								.withSandboxDestination().build();
//					} else {
//						service = APNS.newService()
//								.withCert(ipadCertificateFileNamePatientApp, ipadCertificatePasswordPatientApp)
//								.withSandboxDestination().build();
//					}
//				}
//			}
//
//			Map<String, Object> customValues = new HashMap<String, Object>();
//			if (!DPDoctorUtils.anyStringEmpty(componentType)) {
//				if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType())) {
//					customValues.put("XI", componentTypeId);
//					customValues.put("T", "X");
//					customValues.put("PI", userId);
//				} else if (componentType.equalsIgnoreCase(ComponentType.REPORTS.getType())) {
//					customValues.put("RI", componentTypeId);
//					customValues.put("T", "R");
//					customValues.put("PI", userId);
//				} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT.getType())) {
//					customValues.put("PI", componentTypeId);
//					customValues.put("T", "P");
//				} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR.getType())) {
//					customValues.put("DI", componentTypeId);
//					customValues.put("T", "D");
//				}
//			}
//			String payload = APNS.newPayload().alertBody(message).sound("default").customFields(customValues).build();
//			service.push(pushToken, payload);
//			List<String> deviceIds = new ArrayList<String>();
//			deviceIds.add(deviceId);
//			PushNotificationCollection pushNotificationCollection = new PushNotificationCollection(null, deviceIds,
//					message, DeviceType.valueOf(deviceType.toUpperCase()), null, PushNotificationType.INDIVIDUAL);
//			pushNotificationRepository.save(pushNotificationCollection);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	
	public void pushNotificationOnWebDevices(String deviceId, String pushToken, String message, String componentType,
			String componentTypeId, String deviceType, String role, String userId)  {
		try {
			
//		FCMSender sender = new FCMSender(DOCTOR_WEB_SERVICES_API_KEY);
			 JSONObject data = new JSONObject();
	            JSONObject info = new JSONObject();
	            ObjectMapper mapper = new ObjectMapper();
	            Boolean isSilent = false;
	            Map<String, Object> customValues = new HashMap<String, Object>();
				if (!DPDoctorUtils.anyStringEmpty(componentType)) {
					if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType())) {
					//	customValues.put("XI", componentTypeId);
					//	customValues.put("T", "X");
					//	customValues.put("PI", userId);
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("XI",componentTypeId);
			            info.put("PI",userId);// Notification body
			            info.put("sound","default");
			            info.put("priority","high");
			            data.put("notification", info);
						
					} else if (componentType.equalsIgnoreCase(ComponentType.REPORTS.getType())) {
//						customValues.put("RI", componentTypeId);
//						customValues.put("T", "R");
//						customValues.put("PI", userId);
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("RI",componentTypeId);
			            info.put("PI",userId);// Notification body
			            info.put("sound","default");
			            data.put("notification", info);
						
					} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT.getType())) {
//						customValues.put("PI", componentTypeId);
//						customValues.put("T", "P");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("RI",componentTypeId);
			            info.put("PI",userId);// Notification body
			            info.put("sound","default");
			            data.put("notification", info);
						
						
					} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR.getType())) {
						customValues.put("DI", componentTypeId);
						customValues.put("T", "D");

						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("DI",componentTypeId);
			          //  info.put("PI",userId);// Notification body
			            info.put("sound","default");
			            data.put("notification", info);
					} else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT.getType())) {
						customValues.put("AI", componentTypeId);
						customValues.put("T", "A");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("AI",componentTypeId);
			            info.put("sound","default");
			            info.put("priority","high");
			            //info.put("PI",userId);// Notification body
			            data.put("notification", info);
					} else if (componentType.equalsIgnoreCase(ComponentType.CALENDAR_REMINDER.getType())) {
						//customValues.put("T", "C");
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("sound","default");
			        //    info.put("body", message);
			         //   info.put("RI",componentTypeId);
			          //  info.put("PI",userId);// Notification body
			            data.put("notification", info);
					} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR_LAB_REPORTS.getType())) {
					//	customValues.put("RI", componentTypeId);
					//	customValues.put("T", "DLR");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("RI",componentTypeId);
			            info.put("sound","default");
			         //   info.put("PI",userId);// Notification body
			            data.put("notification", info);
					} else if (componentType.equalsIgnoreCase(ComponentType.USER_RECORD.getType())) {
					//	customValues.put("RI", componentTypeId);
					//	customValues.put("T", "UR");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("RI",componentTypeId);
			            info.put("sound","default");
			     //       info.put("PI",userId);// Notification body
			            data.put("notification", info);
						
					} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DOCTOR_LAB_REPORTS.getType())) {
						customValues.put("RI", componentTypeId);
						customValues.put("T", "SI");
						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "SI");
						info.put( "content_available", true);
			     //       info.put("title",componentType ); // Notification title
			     //       info.put("body", message);
			            info.put("RI",componentTypeId);
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
						
					} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORKS.getType())) {

						customValues.put("RI", componentTypeId);
						customValues.put("T", "DW");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("RI",componentTypeId);
			            info.put("sound","default");
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
						customValues.put("RI", componentTypeId);
						customValues.put("T", "DI");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("sound","default");
			            info.put("RI",componentTypeId);
			       //     info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
						customValues.put("EI", componentTypeId);
						customValues.put("T", "E");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("EI",componentTypeId);
			            info.put("sound","default");
			          //  info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "PR");
						
						 
						data.put("to",pushToken.trim());
						info.put("T", "PR");
			       //     info.put("title",componentType ); // Notification title
			      //      info.put("body", message);
			            info.put( "content_available", true);
			         //   info.put("RI",componentTypeId);
			         //   info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "RDI");
						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RDI");
			     //       info.put("title",componentType ); // Notification title
			    //        info.put("body", message);
			            info.put( "content_available", true);
			        //    info.put("RI",componentTypeId);
			       //     info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
//						customValues.put("RI", "SILENT");
//						customValues.put("T", "DWR");
//						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "DWR");
			       //     info.put("title",componentType ); // Notification title
			       //     info.put("body", message);
			            info.put( "content_available", true);
			           // info.put("RI",componentTypeId);
			          //  info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTION_REFRESH.getType())) {
//						customValues.put("PI",componentTypeId);
//						customValues.put("T", "RX");
//						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RX");
			     //       info.put("title",componentType ); // Notification title
			      //      info.put("body", message);
			           
			            info.put( "content_available", true);
			            info.put("PI",componentTypeId);
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_VISIT_REFRESH.getType())) {
				//		customValues.put("PI",componentTypeId);
				//		customValues.put("T", "RPV");
				//		isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RPV");
			       //     info.put("title",componentType ); // Notification title
			        //    info.put("body", message);
						
						 info.put( "content_available", true);
			            info.put("PI",componentTypeId);
			          
			         //   info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.CLINICAL_NOTES_REFRESH.getType())) {
						customValues.put("PI",componentTypeId);
						customValues.put("T", "RCN");
						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RCN");
			        //    info.put("title",componentType ); // Notification title
			        //    info.put("body", message);
			           
			            info.put( "content_available", true);
			            info.put("PI",componentTypeId);
			           // info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.TREATMENTS_REFRESH.getType())) {
//						customValues.put("PI",componentTypeId);
//						customValues.put("T", "RT");
//						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RT");
			       //     info.put("title",componentType ); // Notification title
			        //    info.put("body", message);
			           
			            info.put( "content_available", true);
			            info.put("PI",componentTypeId);
			       //     info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.RECORDS_REFRESH.getType())) {
//						customValues.put("PI",componentTypeId);
//						customValues.put("T", "RR");
//						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RR");
			       //     info.put("title",componentType ); // Notification title
			        //    info.put("body", message);
			            
			            info.put( "content_available", true);
			            info.put("PI",componentTypeId);
			         //   info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.DISCHARGE_SUMMARY_REFRESH.getType())) {
//						customValues.put("PI",componentTypeId);
//						customValues.put("T", "RDS");
//						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RDS");
			         //   info.put("title",componentType ); // Notification title
			          //  info.put("body", message);
						 info.put( "content_available", true);
			            info.put("PI",componentTypeId);
			           
			       //     info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.INVOICE_REFRESH.getType())) {
						customValues.put("PI",componentTypeId);
						customValues.put("T", "RBI");
						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RBI");
			         //   info.put("title",componentType ); // Notification title
			          //  info.put("body", message);
						 info.put( "content_available", true);
			            info.put("PI",componentTypeId);
			           
			       //     info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.RECEIPT_REFRESH.getType())) {
						customValues.put("PI",componentTypeId);
						customValues.put("T", "RBR");
						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RBR");
//			            info.put("title",componentType ); // Notification title
//			            info.put("body", message);
						 info.put( "content_available", true);
			            info.put("PI",componentTypeId);
			           
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT_REFRESH.getType())) {
					//	customValues.put("AI",componentTypeId);
					//	customValues.put("T", "AR");

						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "AR");
			        //    info.put("title",componentType); // Notification title
			        //    info.put("body", message);
			            info.put( "content_available", true);
						
			            info.put("PI",componentTypeId);
			           
			         //   info.put("PI",userId);// Notification body
			            data.put("notification", info);
			
					}else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT_STATUS_CHANGE.getType())) {
						customValues.put("AI",componentTypeId);
						customValues.put("T", "ASC");
						isSilent = true;
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("sound","default");
			            info.put("AI",componentTypeId);
			       //     info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
						customValues.put("RI", componentTypeId);
						customValues.put("T", "DI");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("RI",componentTypeId);
			            info.put("sound","default");
			       //     info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
						customValues.put("EI", componentTypeId);
						customValues.put("T", "E");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("EI",componentTypeId);
			            info.put("sound","default");
			         //   info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "PR");
						
						data.put("to",pushToken.trim());
						info.put("T", "PR");
			   //         info.put("title",componentType ); // Notification title
			   //         info.put("body", message);
			            info.put( "content_available", true);
			        //    info.put("RI",componentTypeId);
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "DI");
						
						data.put("to",pushToken.trim());
						info.put("T", "RDI");
						info.put( "content_available", true);
			     //       info.put("title",componentType ); // Notification title
			      //      info.put("body", message);
			        //    info.put("RI",componentTypeId);
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "DW");
						
						data.put("to",pushToken.trim());
						info.put("T", "RDW");
			    //        info.put("title",componentType ); // Notification title
			    //        info.put("body", message);
			            info.put( "content_available", true);
			        //    info.put("RI",componentTypeId);
			         //   info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
						customValues.put("RI", componentTypeId);
						customValues.put("T", "DI");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("RI",componentTypeId);
			            info.put("sound","default");
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
						customValues.put("EI", componentTypeId);
						customValues.put("T", "E");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("EI",componentTypeId);
			            info.put("sound","default");
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "PR");
						
						data.put("to",pushToken.trim());
						info.put("T", "PR");
			   //         info.put("title",componentType ); // Notification title
			   //         info.put("body", message);
			            info.put( "content_available", true);
			        //    info.put("RI",componentTypeId);
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "DI");
						
						data.put("to",pushToken.trim());
						info.put("T", "RDI");
						 info.put( "content_available", true);
			        //    info.put("title",componentType ); // Notification title
			        //    info.put("body", message);
			       //     info.put("RI",componentTypeId);
			       //     info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "RDW");
						
						data.put("to",pushToken.trim());
						info.put("T", "RDW");
			      //      info.put("title",componentType ); // Notification title
			      //      info.put("body", message);
			            info.put( "content_available", true);
			      //      info.put("RI",componentTypeId);
			      //      info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_BABY_ACHIEVEMENTS.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "BA");
						
						data.put("to",pushToken.trim());
						info.put("T", "RBA");
			       //     info.put("title",componentType ); // Notification title
			       //     info.put("body", message);
			            info.put( "content_available", true);
			      //      info.put("RI",componentTypeId);
			      //      info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_GROWTH_CHART.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "GC");
						
						data.put("to",pushToken.trim());
						info.put("T", "RGC");
			   //         info.put("title",componentType ); // Notification title
			    //        info.put("body", message);
			            info.put( "content_available", true);
			       //     info.put("RI",componentTypeId);
			      //      info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_VACCINATION.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "VN");
						
						data.put("to",pushToken.trim());
						info.put("T", "RVN");
			   //         info.put("title",componentType ); // Notification title
			   //         info.put("body", message);
			            info.put( "content_available", true);
			         //   info.put("RI",componentTypeId);
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.SIGNED_UP.getType())) {
						customValues.put("SI", componentTypeId);
					
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("SI",componentTypeId);
			            info.put("sound","default");
			            info.put("priority","high");
			            //info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.EMAIL_VERIFICATION.getType())) {
						customValues.put("EVI", componentTypeId);
					
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("EVI",componentTypeId);
			            info.put("sound","default");
			            info.put("priority","high");
			            //info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.DEACTIVATED.getType())) {
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("RI",componentTypeId);
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else {
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			         //   info.put("RI",componentTypeId);
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
				}

	            
				String url="https://fcm.googleapis.com/fcm/send";
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
				con.setRequestProperty("Content-Type","application/json");
				con.setRequestProperty("Authorization","key="+DOCTOR_WEB_SERVICES_API_KEY);

				
				
	            
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

	            
	            System.out.println("Response"+response);
			System.out.println("pushToken"+pushToken);

		} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public void pushNotificationOnAdminWebDevices(String deviceId, String pushToken, String message, String componentType,
			String componentTypeId, String deviceType, String role, String userId)  {
		try {
			
//		FCMSender sender = new FCMSender(DOCTOR_WEB_SERVICES_API_KEY);
			 JSONObject data = new JSONObject();
	            JSONObject info = new JSONObject();
	            ObjectMapper mapper = new ObjectMapper();
	            Boolean isSilent = false;
	            Map<String, Object> customValues = new HashMap<String, Object>();
				if (!DPDoctorUtils.anyStringEmpty(componentType)) {
					if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType())) {
					//	customValues.put("XI", componentTypeId);
					//	customValues.put("T", "X");
					//	customValues.put("PI", userId);
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("XI",componentTypeId);
			            info.put("PI",userId);// Notification body
			            info.put("sound","default");
			            info.put("priority","high");
			            data.put("notification", info);
						
					} else if (componentType.equalsIgnoreCase(ComponentType.REPORTS.getType())) {
//						customValues.put("RI", componentTypeId);
//						customValues.put("T", "R");
//						customValues.put("PI", userId);
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("RI",componentTypeId);
			            info.put("PI",userId);// Notification body
			            info.put("sound","default");
			            data.put("notification", info);
						
					} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT.getType())) {
//						customValues.put("PI", componentTypeId);
//						customValues.put("T", "P");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("RI",componentTypeId);
			            info.put("PI",userId);// Notification body
			            info.put("sound","default");
			            data.put("notification", info);
						
						
					} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR.getType())) {
						customValues.put("DI", componentTypeId);
						customValues.put("T", "D");

						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("DI",componentTypeId);
			          //  info.put("PI",userId);// Notification body
			            info.put("sound","default");
			            data.put("notification", info);
					} else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT.getType())) {
						customValues.put("AI", componentTypeId);
						customValues.put("T", "A");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("AI",componentTypeId);
			            info.put("sound","default");
			            info.put("priority","high");
			            //info.put("PI",userId);// Notification body
			            data.put("notification", info);
					} else if (componentType.equalsIgnoreCase(ComponentType.CALENDAR_REMINDER.getType())) {
						//customValues.put("T", "C");
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("sound","default");
			        //    info.put("body", message);
			         //   info.put("RI",componentTypeId);
			          //  info.put("PI",userId);// Notification body
			            data.put("notification", info);
					} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR_LAB_REPORTS.getType())) {
					//	customValues.put("RI", componentTypeId);
					//	customValues.put("T", "DLR");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("RI",componentTypeId);
			            info.put("sound","default");
			         //   info.put("PI",userId);// Notification body
			            data.put("notification", info);
					} else if (componentType.equalsIgnoreCase(ComponentType.USER_RECORD.getType())) {
					//	customValues.put("RI", componentTypeId);
					//	customValues.put("T", "UR");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("RI",componentTypeId);
			            info.put("sound","default");
			     //       info.put("PI",userId);// Notification body
			            data.put("notification", info);
						
					} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DOCTOR_LAB_REPORTS.getType())) {
						customValues.put("RI", componentTypeId);
						customValues.put("T", "SI");
						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "SI");
						info.put( "content_available", true);
			     //       info.put("title",componentType ); // Notification title
			     //       info.put("body", message);
			            info.put("RI",componentTypeId);
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
						
					} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORKS.getType())) {

						customValues.put("RI", componentTypeId);
						customValues.put("T", "DW");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("RI",componentTypeId);
			            info.put("sound","default");
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
						customValues.put("RI", componentTypeId);
						customValues.put("T", "DI");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("sound","default");
			            info.put("RI",componentTypeId);
			       //     info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
						customValues.put("EI", componentTypeId);
						customValues.put("T", "E");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("EI",componentTypeId);
			            info.put("sound","default");
			          //  info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "PR");
						
						 
						data.put("to",pushToken.trim());
						info.put("T", "PR");
			       //     info.put("title",componentType ); // Notification title
			      //      info.put("body", message);
			            info.put( "content_available", true);
			         //   info.put("RI",componentTypeId);
			         //   info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "RDI");
						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RDI");
			     //       info.put("title",componentType ); // Notification title
			    //        info.put("body", message);
			            info.put( "content_available", true);
			        //    info.put("RI",componentTypeId);
			       //     info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
//						customValues.put("RI", "SILENT");
//						customValues.put("T", "DWR");
//						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "DWR");
			       //     info.put("title",componentType ); // Notification title
			       //     info.put("body", message);
			            info.put( "content_available", true);
			           // info.put("RI",componentTypeId);
			          //  info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTION_REFRESH.getType())) {
//						customValues.put("PI",componentTypeId);
//						customValues.put("T", "RX");
//						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RX");
			     //       info.put("title",componentType ); // Notification title
			      //      info.put("body", message);
			           
			            info.put( "content_available", true);
			            info.put("PI",componentTypeId);
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_VISIT_REFRESH.getType())) {
				//		customValues.put("PI",componentTypeId);
				//		customValues.put("T", "RPV");
				//		isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RPV");
			       //     info.put("title",componentType ); // Notification title
			        //    info.put("body", message);
						
						 info.put( "content_available", true);
			            info.put("PI",componentTypeId);
			          
			         //   info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.CLINICAL_NOTES_REFRESH.getType())) {
						customValues.put("PI",componentTypeId);
						customValues.put("T", "RCN");
						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RCN");
			        //    info.put("title",componentType ); // Notification title
			        //    info.put("body", message);
			           
			            info.put( "content_available", true);
			            info.put("PI",componentTypeId);
			           // info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.TREATMENTS_REFRESH.getType())) {
//						customValues.put("PI",componentTypeId);
//						customValues.put("T", "RT");
//						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RT");
			       //     info.put("title",componentType ); // Notification title
			        //    info.put("body", message);
			           
			            info.put( "content_available", true);
			            info.put("PI",componentTypeId);
			       //     info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.RECORDS_REFRESH.getType())) {
//						customValues.put("PI",componentTypeId);
//						customValues.put("T", "RR");
//						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RR");
			       //     info.put("title",componentType ); // Notification title
			        //    info.put("body", message);
			            
			            info.put( "content_available", true);
			            info.put("PI",componentTypeId);
			         //   info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.DISCHARGE_SUMMARY_REFRESH.getType())) {
//						customValues.put("PI",componentTypeId);
//						customValues.put("T", "RDS");
//						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RDS");
			         //   info.put("title",componentType ); // Notification title
			          //  info.put("body", message);
						 info.put( "content_available", true);
			            info.put("PI",componentTypeId);
			           
			       //     info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.INVOICE_REFRESH.getType())) {
						customValues.put("PI",componentTypeId);
						customValues.put("T", "RBI");
						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RBI");
			         //   info.put("title",componentType ); // Notification title
			          //  info.put("body", message);
						 info.put( "content_available", true);
			            info.put("PI",componentTypeId);
			           
			       //     info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.RECEIPT_REFRESH.getType())) {
						customValues.put("PI",componentTypeId);
						customValues.put("T", "RBR");
						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "RBR");
//			            info.put("title",componentType ); // Notification title
//			            info.put("body", message);
						 info.put( "content_available", true);
			            info.put("PI",componentTypeId);
			           
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT_REFRESH.getType())) {
					//	customValues.put("AI",componentTypeId);
					//	customValues.put("T", "AR");

						isSilent = true;
						
						data.put("to",pushToken.trim());
						info.put("T", "AR");
			        //    info.put("title",componentType); // Notification title
			        //    info.put("body", message);
			            info.put( "content_available", true);
						
			            info.put("PI",componentTypeId);
			           
			         //   info.put("PI",userId);// Notification body
			            data.put("notification", info);
			
					}else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT_STATUS_CHANGE.getType())) {
						customValues.put("AI",componentTypeId);
						customValues.put("T", "ASC");
						isSilent = true;
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("sound","default");
			            info.put("AI",componentTypeId);
			       //     info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
						customValues.put("RI", componentTypeId);
						customValues.put("T", "DI");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("RI",componentTypeId);
			            info.put("sound","default");
			       //     info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
						customValues.put("EI", componentTypeId);
						customValues.put("T", "E");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("EI",componentTypeId);
			            info.put("sound","default");
			         //   info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "PR");
						
						data.put("to",pushToken.trim());
						info.put("T", "PR");
			   //         info.put("title",componentType ); // Notification title
			   //         info.put("body", message);
			            info.put( "content_available", true);
			        //    info.put("RI",componentTypeId);
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "DI");
						
						data.put("to",pushToken.trim());
						info.put("T", "RDI");
						info.put( "content_available", true);
			     //       info.put("title",componentType ); // Notification title
			      //      info.put("body", message);
			        //    info.put("RI",componentTypeId);
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "DW");
						
						data.put("to",pushToken.trim());
						info.put("T", "RDW");
			    //        info.put("title",componentType ); // Notification title
			    //        info.put("body", message);
			            info.put( "content_available", true);
			        //    info.put("RI",componentTypeId);
			         //   info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
						customValues.put("RI", componentTypeId);
						customValues.put("T", "DI");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("RI",componentTypeId);
			            info.put("sound","default");
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
						customValues.put("EI", componentTypeId);
						customValues.put("T", "E");
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("EI",componentTypeId);
			            info.put("sound","default");
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "PR");
						
						data.put("to",pushToken.trim());
						info.put("T", "PR");
			   //         info.put("title",componentType ); // Notification title
			   //         info.put("body", message);
			            info.put( "content_available", true);
			        //    info.put("RI",componentTypeId);
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "DI");
						
						data.put("to",pushToken.trim());
						info.put("T", "RDI");
						 info.put( "content_available", true);
			        //    info.put("title",componentType ); // Notification title
			        //    info.put("body", message);
			       //     info.put("RI",componentTypeId);
			       //     info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "RDW");
						
						data.put("to",pushToken.trim());
						info.put("T", "RDW");
			      //      info.put("title",componentType ); // Notification title
			      //      info.put("body", message);
			            info.put( "content_available", true);
			      //      info.put("RI",componentTypeId);
			      //      info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_BABY_ACHIEVEMENTS.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "BA");
						
						data.put("to",pushToken.trim());
						info.put("T", "RBA");
			       //     info.put("title",componentType ); // Notification title
			       //     info.put("body", message);
			            info.put( "content_available", true);
			      //      info.put("RI",componentTypeId);
			      //      info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_GROWTH_CHART.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "GC");
						
						data.put("to",pushToken.trim());
						info.put("T", "RGC");
			   //         info.put("title",componentType ); // Notification title
			    //        info.put("body", message);
			            info.put( "content_available", true);
			       //     info.put("RI",componentTypeId);
			      //      info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_VACCINATION.getType())) {
						customValues.put("RI", "SILENT");
						customValues.put("T", "VN");
						
						data.put("to",pushToken.trim());
						info.put("T", "RVN");
			   //         info.put("title",componentType ); // Notification title
			   //         info.put("body", message);
			            info.put( "content_available", true);
			         //   info.put("RI",componentTypeId);
			        //    info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.SIGNED_UP.getType())) {
						customValues.put("SI", componentTypeId);
					
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("SI",componentTypeId);
			            info.put("sound","default");
			            info.put("priority","high");
			            //info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else if (componentType.equalsIgnoreCase(ComponentType.EMAIL_VERIFICATION.getType())) {
						customValues.put("EVI", componentTypeId);
					
						
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			            info.put("EVI",componentTypeId);
			            info.put("sound","default");
			            info.put("priority","high");
			            //info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
					else {
						data.put("to",pushToken.trim());
			            info.put("title",componentType ); // Notification title
			            info.put("body", message);
			         //   info.put("RI",componentTypeId);
			            info.put("PI",userId);// Notification body
			            data.put("notification", info);
					}
				}

	            
				String url="https://fcm.googleapis.com/fcm/send";
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
				con.setRequestProperty("Content-Type","application/json");
				con.setRequestProperty("Authorization","key="+ADMIN_WEB_SERVICES_API_KEY);

				
				
	            
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

	            
	            System.out.println("Response"+response);
			System.out.println("pushToken"+pushToken);

		} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	
	public void pushNotificationOnIosDevices(String deviceId, String pushToken, String message, String componentType,
			String componentTypeId, String deviceType, String role, String userId)  {
		try {
			ObjectMapper mapper = new ObjectMapper();
			
			 JSONObject data = new JSONObject();
	            JSONObject info = new JSONObject();
			
			
			JSONObject notification = new JSONObject();
			JSONObject send = new JSONObject();
	
			Boolean isSilent = false;
			Map<String, Object> customValues = new HashMap<String, Object>();
			if (!DPDoctorUtils.anyStringEmpty(componentType)) {
				if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType())) {
				//	customValues.put("XI", componentTypeId);
				//	customValues.put("T", "X");
				//	customValues.put("PI", userId);
					
					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("body", message);
		            info.put("XI",componentTypeId);
		            info.put("PI",userId);// Notification body
		            info.put("sound","default");
		            info.put("priority","high");
		            data.put("notification", info);
					
				} else if (componentType.equalsIgnoreCase(ComponentType.REPORTS.getType())) {
//					customValues.put("RI", componentTypeId);
//					customValues.put("T", "R");
//					customValues.put("PI", userId);
					
					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("body", message);
		            info.put("RI",componentTypeId);
		            info.put("PI",userId);// Notification body
		            info.put("sound","default");
		            data.put("notification", info);
					
				} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT.getType())) {
//					customValues.put("PI", componentTypeId);
//					customValues.put("T", "P");
					
					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("body", message);
		            info.put("RI",componentTypeId);
		            info.put("PI",userId);// Notification body
		            info.put("sound","default");
		            data.put("notification", info);
					
					
				} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR.getType())) {
					customValues.put("DI", componentTypeId);
					customValues.put("T", "D");

					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("body", message);
		            info.put("DI",componentTypeId);
		          //  info.put("PI",userId);// Notification body
		            info.put("sound","default");
		            data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT.getType())) {
					customValues.put("AI", componentTypeId);
					customValues.put("T", "A");
					
					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("body", message);
		            info.put("AI",componentTypeId);
		            info.put("sound","default");
		            info.put("priority","high");
		            //info.put("PI",userId);// Notification body
		            data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.CALENDAR_REMINDER.getType())) {
					//customValues.put("T", "C");
					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("sound","default");
		        //    info.put("body", message);
		         //   info.put("RI",componentTypeId);
		          //  info.put("PI",userId);// Notification body
		            data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR_LAB_REPORTS.getType())) {
				//	customValues.put("RI", componentTypeId);
				//	customValues.put("T", "DLR");
					
					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("body", message);
		            info.put("RI",componentTypeId);
		            info.put("sound","default");
		         //   info.put("PI",userId);// Notification body
		            data.put("notification", info);
				} else if (componentType.equalsIgnoreCase(ComponentType.USER_RECORD.getType())) {
				//	customValues.put("RI", componentTypeId);
				//	customValues.put("T", "UR");
					
					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("body", message);
		            info.put("RI",componentTypeId);
		            info.put("sound","default");
		     //       info.put("PI",userId);// Notification body
		            data.put("notification", info);
					
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DOCTOR_LAB_REPORTS.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "SI");
					isSilent = true;
					
					data.put("to",pushToken.trim());
					info.put("T", "SI");
					info.put( "content_available", true);
		     //       info.put("title",componentType ); // Notification title
		     //       info.put("body", message);
		            info.put("RI",componentTypeId);
		        //    info.put("PI",userId);// Notification body
		            data.put("notification", info);
					
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORKS.getType())) {

					customValues.put("RI", componentTypeId);
					customValues.put("T", "DW");
					
					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("body", message);
		            info.put("RI",componentTypeId);
		            info.put("sound","default");
		        //    info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DI");
					
					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("body", message);
		            info.put("sound","default");
		            info.put("RI",componentTypeId);
		       //     info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					customValues.put("EI", componentTypeId);
					customValues.put("T", "E");
					
					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("body", message);
		            info.put("EI",componentTypeId);
		            info.put("sound","default");
		          //  info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "PR");
					
					 
					data.put("to",pushToken.trim());
					info.put("T", "PR");
		       //     info.put("title",componentType ); // Notification title
		      //      info.put("body", message);
		            info.put( "content_available", true);
		         //   info.put("RI",componentTypeId);
		         //   info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "RDI");
					isSilent = true;
					
					data.put("to",pushToken.trim());
					info.put("T", "RDI");
		     //       info.put("title",componentType ); // Notification title
		    //        info.put("body", message);
		            info.put( "content_available", true);
		        //    info.put("RI",componentTypeId);
		       //     info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
//					customValues.put("RI", "SILENT");
//					customValues.put("T", "DWR");
//					isSilent = true;
					
					data.put("to",pushToken.trim());
					info.put("T", "DWR");
		       //     info.put("title",componentType ); // Notification title
		       //     info.put("body", message);
		            info.put( "content_available", true);
		           // info.put("RI",componentTypeId);
		          //  info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}else if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTION_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RX");
//					isSilent = true;
					
					data.put("to",pushToken.trim());
					info.put("T", "RX");
		     //       info.put("title",componentType ); // Notification title
		      //      info.put("body", message);
		           
		            info.put( "content_available", true);
		            info.put("PI",componentTypeId);
		        //    info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_VISIT_REFRESH.getType())) {
			//		customValues.put("PI",componentTypeId);
			//		customValues.put("T", "RPV");
			//		isSilent = true;
					
					data.put("to",pushToken.trim());
					info.put("T", "RPV");
		       //     info.put("title",componentType ); // Notification title
		        //    info.put("body", message);
					
					 info.put( "content_available", true);
		            info.put("PI",componentTypeId);
		          
		         //   info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}else if (componentType.equalsIgnoreCase(ComponentType.CLINICAL_NOTES_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RCN");
					isSilent = true;
					
					data.put("to",pushToken.trim());
					info.put("T", "RCN");
		        //    info.put("title",componentType ); // Notification title
		        //    info.put("body", message);
		           
		            info.put( "content_available", true);
		            info.put("PI",componentTypeId);
		           // info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}else if (componentType.equalsIgnoreCase(ComponentType.TREATMENTS_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RT");
//					isSilent = true;
					
					data.put("to",pushToken.trim());
					info.put("T", "RT");
		       //     info.put("title",componentType ); // Notification title
		        //    info.put("body", message);
		           
		            info.put( "content_available", true);
		            info.put("PI",componentTypeId);
		       //     info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}else if (componentType.equalsIgnoreCase(ComponentType.RECORDS_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RR");
//					isSilent = true;
					
					data.put("to",pushToken.trim());
					info.put("T", "RR");
		       //     info.put("title",componentType ); // Notification title
		        //    info.put("body", message);
		            
		            info.put( "content_available", true);
		            info.put("PI",componentTypeId);
		         //   info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}else if (componentType.equalsIgnoreCase(ComponentType.DISCHARGE_SUMMARY_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RDS");
//					isSilent = true;
					
					data.put("to",pushToken.trim());
					info.put("T", "RDS");
		         //   info.put("title",componentType ); // Notification title
		          //  info.put("body", message);
					 info.put( "content_available", true);
		            info.put("PI",componentTypeId);
		           
		       //     info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}else if (componentType.equalsIgnoreCase(ComponentType.INVOICE_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RBI");
					isSilent = true;
					
					data.put("to",pushToken.trim());
					info.put("T", "RBI");
		         //   info.put("title",componentType ); // Notification title
		          //  info.put("body", message);
					 info.put( "content_available", true);
		            info.put("PI",componentTypeId);
		           
		       //     info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}else if (componentType.equalsIgnoreCase(ComponentType.RECEIPT_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RBR");
					isSilent = true;
					
					data.put("to",pushToken.trim());
					info.put("T", "RBR");
//		            info.put("title",componentType ); // Notification title
//		            info.put("body", message);
					 info.put( "content_available", true);
		            info.put("PI",componentTypeId);
		           
		        //    info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT_REFRESH.getType())) {
				//	customValues.put("AI",componentTypeId);
				//	customValues.put("T", "AR");

					isSilent = true;
					
					data.put("to",pushToken.trim());
					info.put("T", "AR");
		        //    info.put("title",componentType); // Notification title
		        //    info.put("body", message);
		            info.put( "content_available", true);
					
		            info.put("PI",componentTypeId);
		           
		         //   info.put("PI",userId);// Notification body
		            data.put("notification", info);
		
				}else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT_STATUS_CHANGE.getType())) {
					customValues.put("AI",componentTypeId);
					customValues.put("T", "ASC");
					isSilent = true;
					
					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("body", message);
		            info.put("sound","default");
		            info.put("AI",componentTypeId);
		       //     info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DI");
					
					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("body", message);
		            info.put("RI",componentTypeId);
		            info.put("sound","default");
		       //     info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					customValues.put("EI", componentTypeId);
					customValues.put("T", "E");
					
					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("body", message);
		            info.put("EI",componentTypeId);
		            info.put("sound","default");
		         //   info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "PR");
					
					data.put("to",pushToken.trim());
					info.put("T", "PR");
		   //         info.put("title",componentType ); // Notification title
		   //         info.put("body", message);
		            info.put( "content_available", true);
		        //    info.put("RI",componentTypeId);
		        //    info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DI");
					
					data.put("to",pushToken.trim());
					info.put("T", "RDI");
					info.put( "content_available", true);
		     //       info.put("title",componentType ); // Notification title
		      //      info.put("body", message);
		        //    info.put("RI",componentTypeId);
		        //    info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DW");
					
					data.put("to",pushToken.trim());
					info.put("T", "RDW");
		    //        info.put("title",componentType ); // Notification title
		    //        info.put("body", message);
		            info.put( "content_available", true);
		        //    info.put("RI",componentTypeId);
		         //   info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DI");
					
					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("body", message);
		            info.put("RI",componentTypeId);
		            info.put("sound","default");
		        //    info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					customValues.put("EI", componentTypeId);
					customValues.put("T", "E");
					
					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("body", message);
		            info.put("EI",componentTypeId);
		            info.put("sound","default");
		        //    info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "PR");
					
					data.put("to",pushToken.trim());
					info.put("T", "PR");
		   //         info.put("title",componentType ); // Notification title
		   //         info.put("body", message);
		            info.put( "content_available", true);
		        //    info.put("RI",componentTypeId);
		        //    info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DI");
					
					data.put("to",pushToken.trim());
					info.put("T", "RDI");
					 info.put( "content_available", true);
		        //    info.put("title",componentType ); // Notification title
		        //    info.put("body", message);
		       //     info.put("RI",componentTypeId);
		       //     info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "RDW");
					
					data.put("to",pushToken.trim());
					info.put("T", "RDW");
		      //      info.put("title",componentType ); // Notification title
		      //      info.put("body", message);
		            info.put( "content_available", true);
		      //      info.put("RI",componentTypeId);
		      //      info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_BABY_ACHIEVEMENTS.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "BA");
					
					data.put("to",pushToken.trim());
					info.put("T", "RBA");
		       //     info.put("title",componentType ); // Notification title
		       //     info.put("body", message);
		            info.put( "content_available", true);
		      //      info.put("RI",componentTypeId);
		      //      info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_GROWTH_CHART.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "GC");
					
					data.put("to",pushToken.trim());
					info.put("T", "RGC");
		   //         info.put("title",componentType ); // Notification title
		    //        info.put("body", message);
		            info.put( "content_available", true);
		       //     info.put("RI",componentTypeId);
		      //      info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_VACCINATION.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "VN");
					
					data.put("to",pushToken.trim());
					info.put("T", "RVN");
		   //         info.put("title",componentType ); // Notification title
		   //         info.put("body", message);
		            info.put( "content_available", true);
		         //   info.put("RI",componentTypeId);
		        //    info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}
				else {
					data.put("to",pushToken.trim());
		            info.put("title",componentType ); // Notification title
		            info.put("body", message);
		         //   info.put("RI",componentTypeId);
		        //    info.put("PI",userId);// Notification body
		            data.put("notification", info);
				}
			}
		//	String jsonOutput = mapper.writeValueAsString(notification);
	//1st
//			com.google.firebase.messaging.Message messageObj=com.google.firebase.messaging.Message.builder().putData("Message",customValues.toString()).setToken(pushToken).build();
//			System.out.println("messageObj"+messageObj);
//			System.out.println("pushToken"+pushToken);
//	
//			
//			System.out.println("send"+send);
			
		//	account = rayzorpayClient.VirtualAccounts.create(orderRequest);
			
			String url="https://fcm.googleapis.com/fcm/send";
//			 
//			// String authStringEnc = Base64.getEncoder().encodeToString(authStr.getBytes());
			URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//			
			con.setDoOutput(true);
//			
//			System.out.println(con.getErrorStream());
			con.setDoInput(true);
//			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type","application/json");
			con.setRequestProperty("Authorization","key="+DOCTOR_IOS_SERVICES_API_KEY  );

			if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
				// sender = new Sender(DOCTOR_GEOCODING_SERVICES_API_KEY);
				if (deviceType.equalsIgnoreCase(DeviceType.IOS.getType())) {
					con.setRequestProperty("Authorization","key="+DOCTOR_IOS_SERVICES_API_KEY);
				}
				//	else {
//					sender = new FCMSender(DOCTOR_PAD_GEOCODING_SERVICES_API_KEY);
//				}

			} else {
				// sender = new Sender(PATIENT_GEOCODING_SERVICES_API_KEY);
				con.setRequestProperty("Authorization","key="+PATIENT_IOS_SERVICES_API_KEY);
			}
			
            
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
			
			//2nd
//			  ApsAlert alert =
//	                    ApsAlert.builder()
//	                        .setTitle("title")
//	                        .setBody(message)
//	                        .build();
//			  System.out.println("alert"+alert);
//	                 Aps aps =
//	                    Aps.builder()
//	                        .setAlert(alert)
//	                        .setContentAvailable(false)
//	                        .setMutableContent(true)
//	                        .setSound("default")
//	                        .build();
//	                 
//	                 System.out.println("Aps"+aps);
//	                 ApnsConfig apnsConfig =
//	                    ApnsConfig.builder()
//	                        .setAps(aps).putAllCustomData(customValues)
//	                        
//	                        .build();
//	                 System.out.println("apnsConfig"+apnsConfig);
//	   com.google.firebase.messaging.Message         messageObj =
//	                    com.google.firebase.messaging.Message.builder()
//	                        .setToken(pushToken)
//	                        .setApnsConfig(apnsConfig)
//	                        .build();
//	
//		//	sender.send(messageObj,pushToken, 1);
//	   System.out.println("pushToken"+pushToken);
//	   
//	           //     System.out.println("messageObj"+messageObj);
//	   FileInputStream serviceAccount =
//               new FileInputStream(DOCTOR_FIREBASE_JSON);
//	   FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount))
//	              
//               .setDatabaseUrl("https://healthcocoplus-1383.firebaseio.com")
//     
//               .build();
//	   
//	 
//	  // FirebaseApp fire=FirebaseApp.initializeApp(options);
//	   
//	   FirebaseApp fire = null;
//	    List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
//	    if(firebaseApps!=null && !firebaseApps.isEmpty()){
//	        for(FirebaseApp app : firebaseApps){
//	            if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
//	                fire = app;
//	        }
//	    }
//	    else
//	        fire = FirebaseApp.initializeApp(options); 
//	   
//	   System.out.println(""+pushToken);
//	   String response=FirebaseMessaging.getInstance(fire).send(messageObj);
			List<String> deviceIds = new ArrayList<String>();
			deviceIds.add(deviceId);
			PushNotificationCollection pushNotificationCollection = new PushNotificationCollection(null, deviceIds,
					message, DeviceType.IOS, null, PushNotificationType.INDIVIDUAL);
			pushNotificationRepository.save(pushNotificationCollection);
			logger.info("Message Result: " + response.toString());
		} 
//		catch (FirebaseMessagingException jpe) {
//			jpe.printStackTrace();
//		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	
//	public void broadcastPushNotificationOnIosDevices(List<String> deviceIds, List<String> pushToken, String message,
//			String imageURL, String deviceType, String role) {
//		try {
//			ApnsService service = null;
//			if (isEnvProduction) {
//				if (deviceType.equalsIgnoreCase(DeviceType.IOS.getType())) {
//					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
//						service = APNS.newService()
//								.withCert(iosCertificateFileNameDoctorApp, iosCertificatePasswordDoctorApp)
//								.withProductionDestination().build();
//					} else {
//						service = APNS.newService()
//								.withCert(iosCertificateFileNamePatientApp, iosCertificatePasswordPatientApp)
//								.withProductionDestination().build();
//					}
//				} else {
//					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
//						service = APNS.newService()
//								.withCert(ipadCertificateFileNameDoctorApp, ipadCertificatePasswordDoctorApp)
//								.withProductionDestination().build();
//					} else {
//						service = APNS.newService()
//								.withCert(ipadCertificateFileNamePatientApp, ipadCertificatePasswordPatientApp)
//								.withProductionDestination().build();
//					}
//				}
//			} else {
//				if (deviceType.equalsIgnoreCase(DeviceType.IOS.getType())) {
//					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
//						service = APNS.newService()
//								.withCert(iosCertificateFileNameDoctorApp, iosCertificatePasswordDoctorApp)
//								.withSandboxDestination().build();
//					} else {
//						service = APNS.newService()
//								.withCert(iosCertificateFileNamePatientApp, iosCertificatePasswordPatientApp)
//								.withSandboxDestination().build();
//					}
//				} else {
//					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
//						service = APNS.newService()
//								.withCert(ipadCertificateFileNameDoctorApp, ipadCertificatePasswordDoctorApp)
//								.withSandboxDestination().build();
//					} else {
//						service = APNS.newService()
//								.withCert(ipadCertificateFileNamePatientApp, ipadCertificatePasswordPatientApp)
//								.withSandboxDestination().build();
//					}
//				}
//			}
//
//			Map<String, Object> customValues = new HashMap<String, Object>();
//			if (!DPDoctorUtils.anyStringEmpty(imageURL)) {
//				customValues.put("img", imageURL);
//			}
//			customValues.put("img", imageURL);
//
//			String payload = APNS.newPayload().alertBody(message).sound(iosNotificationSoundFilepath)
//					.customFields(customValues).build();
//			service.push(pushToken, payload);
//
//			PushNotificationCollection pushNotificationCollection = new PushNotificationCollection(null, deviceIds,
//					message, DeviceType.valueOf(deviceType.toUpperCase()), null, PushNotificationType.BROADCAST);
//			pushNotificationRepository.save(pushNotificationCollection);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	public void broadcastPushNotificationOnAndroidDevices(List<String> deviceIds, List<String> pushTokens, String message,
//			String imageURL, String deviceType, RoleEnum role) {
//		
//		try {
//			ObjectMapper mapper = new ObjectMapper();
//			
//			 JSONObject data = new JSONObject();
//	            JSONObject info = new JSONObject();
//			
//			
//			JSONObject notification = new JSONObject();
//			JSONObject send = new JSONObject();
//			JSONArray devices = new JSONArray();
//		//	pushToken.add("d486pxyUVUbxizOkUSPOqc:APA91bEJ8U8DlLsBZass7rQBNcY7M1HjOZWY77g9JmgAiBkK-wwO6lVSNTtTKm7IiExX0jxc6YefDFhcB_3eE2T3zAE2U7SpXk7o8YsHsk433FacK7v78Bda817Y3FcPqeo2GOUrwxKL");
//			devices.put(pushTokens);
//			
//			String device="eTMlPD-bTKumC7W7p8n1QH:APA91bGMFGUFQz71ie_ltWvoTipViOHUj02MaKbu_e6-ZFuGiknvoqI1nuIV7INjpHItgB86riwLAB6PwGE4tvQlxVdft_FPEvL70xfBNxHyP__t0gNpIDHqMZ7xz_8COnv8LMNNKFYS";
//			System.out.println("pushToken"+devices.length()); 
//			Boolean isSilent = false;
//			Map<String, Object> customValues = new HashMap<String, Object>();
//							
//					data.put("to",device);
//		            info.put("title","Broadcast Notification" ); // Notification title
//		            info.put("body", message);
//		         //   info.put("RI",componentTypeId);
//		        //    info.put("PI",userId);// Notification body
//		            data.put("notification", info);
//			
//			
//		//	String jsonOutput = mapper.writeValueAsString(notification);
//	//1st
////			com.google.firebase.messaging.Message messageObj=com.google.firebase.messaging.Message.builder().putData("Message",customValues.toString()).setToken(pushToken).build();
////			System.out.println("messageObj"+messageObj);
////			System.out.println("pushToken"+pushToken);
////	
////			
////			System.out.println("send"+send);
//			
//		//	account = rayzorpayClient.VirtualAccounts.create(orderRequest);
//			
//			String url="https://fcm.googleapis.com/fcm/send";
////			 
////			// String authStringEnc = Base64.getEncoder().encodeToString(authStr.getBytes());
//			URL obj = new URL(url);
//		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
////
////			
//			con.setDoOutput(true);
////			
////			System.out.println(con.getErrorStream());
//			con.setDoInput(true);
////			// optional default is POST
//			con.setRequestMethod("POST");
//			con.setRequestProperty("Content-Type","application/json");
//			con.setRequestProperty("Authorization","key="+DOCTOR_IOS_SERVICES_API_KEY  );
//
//			if (role.equals(RoleEnum.DOCTOR)) {
//				// sender = new Sender(DOCTOR_GEOCODING_SERVICES_API_KEY);
//				if (deviceType.equalsIgnoreCase(DeviceType.IOS.getType())) {
//					con.setRequestProperty("Authorization","key="+DOCTOR_ANDROID_SERVICES_SERVER_KEY);
//				}
//				//	else {
////					sender = new FCMSender(DOCTOR_PAD_GEOCODING_SERVICES_API_KEY);
////				}
//
//			} else {
//				// sender = new Sender(PATIENT_GEOCODING_SERVICES_API_KEY);
//				con.setRequestProperty("Authorization","key="+PATIENT_ANDROID_SERVICES_SERVER_KEY);
//			}
//			
//            
//            System.out.println(data.toString());
//            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
//            wr.write(data.toString());
//            wr.flush();
//            wr.close();
//
//            int responseCode = con.getResponseCode();
//            System.out.println("Response Code : " + responseCode);
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            String inputLine;
//            StringBuffer response = new StringBuffer();
//
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//			
//            System.out.println("Resonse: " + response);
//			
//			
//            PushNotificationCollection pushNotificationCollection = new PushNotificationCollection(null, deviceIds,
//					message, DeviceType.ANDROID, imageURL, PushNotificationType.BROADCAST);
//			pushNotificationRepository.save(pushNotificationCollection);
//			logger.info("Message Result: " + response.toString());
//		} 
//
//		catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//
//	}

	
	
	
	
	public void broadcastPushNotificationOnIosDevices(List<String> deviceIds, List<String> pushToken, String message,
			String imageURL, String deviceType, String role) {
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			
			 JSONObject data = new JSONObject();
	            JSONObject info = new JSONObject();
			
			
			JSONObject notification = new JSONObject();
			JSONObject send = new JSONObject();
			JSONArray devices = new JSONArray();
		//	pushToken.add("d486pxyUVUbxizOkUSPOqc:APA91bEJ8U8DlLsBZass7rQBNcY7M1HjOZWY77g9JmgAiBkK-wwO6lVSNTtTKm7IiExX0jxc6YefDFhcB_3eE2T3zAE2U7SpXk7o8YsHsk433FacK7v78Bda817Y3FcPqeo2GOUrwxKL");
			devices.put(pushToken);
			for (int i = 0; i < pushToken.size(); i++) {
				devices.put(pushToken.get(i));
            }
			
			System.out.println("pushToken"+devices.length()); 
			Boolean isSilent = false;
			Map<String, Object> customValues = new HashMap<String, Object>();
							
					data.put("registration_ids",devices);
		            info.put("title","Broadcast Notification" ); // Notification title
		            info.put("body", message);
		         //   info.put("RI",componentTypeId);
		        //    info.put("PI",userId);// Notification body
		            data.put("notification", info);
			
			
		//	String jsonOutput = mapper.writeValueAsString(notification);
	//1st
//			com.google.firebase.messaging.Message messageObj=com.google.firebase.messaging.Message.builder().putData("Message",customValues.toString()).setToken(pushToken).build();
//			System.out.println("messageObj"+messageObj);
//			System.out.println("pushToken"+pushToken);
//	
//			
//			System.out.println("send"+send);
			
		//	account = rayzorpayClient.VirtualAccounts.create(orderRequest);
			
			String url="https://fcm.googleapis.com/fcm/send";
//			 
//			// String authStringEnc = Base64.getEncoder().encodeToString(authStr.getBytes());
			URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//			
			con.setDoOutput(true);
//			
//			System.out.println(con.getErrorStream());
			con.setDoInput(true);
//			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type","application/json");
			con.setRequestProperty("Authorization","key="+DOCTOR_IOS_SERVICES_API_KEY  );

			if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
				// sender = new Sender(DOCTOR_GEOCODING_SERVICES_API_KEY);
				if (deviceType.equalsIgnoreCase(DeviceType.IOS.getType())) {
					con.setRequestProperty("Authorization","key="+DOCTOR_IOS_SERVICES_API_KEY);
				}
				//	else {
//					sender = new FCMSender(DOCTOR_PAD_GEOCODING_SERVICES_API_KEY);
//				}

			} else {
				// sender = new Sender(PATIENT_GEOCODING_SERVICES_API_KEY);
				con.setRequestProperty("Authorization","key="+PATIENT_IOS_SERVICES_API_KEY);
			}
			
            
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
			
			//2nd
//			  ApsAlert alert =
//	                    ApsAlert.builder()
//	                        .setTitle("title")
//	                        .setBody(message)
//	                        .build();
//			  System.out.println("alert"+alert);
//	                 Aps aps =
//	                    Aps.builder()
//	                        .setAlert(alert)
//	                        .setContentAvailable(false)
//	                        .setMutableContent(true)
//	                        .setSound("default")
//	                        .build();
//	                 
//	                 System.out.println("Aps"+aps);
//	                 ApnsConfig apnsConfig =
//	                    ApnsConfig.builder()
//	                        .setAps(aps).putAllCustomData(customValues)
//	                        
//	                        .build();
//	                 System.out.println("apnsConfig"+apnsConfig);
//	   com.google.firebase.messaging.Message         messageObj =
//	                    com.google.firebase.messaging.Message.builder()
//	                        .setToken(pushToken)
//	                        .setApnsConfig(apnsConfig)
//	                        .build();
//	
//		//	sender.send(messageObj,pushToken, 1);
//	   System.out.println("pushToken"+pushToken);
//	   
//	           //     System.out.println("messageObj"+messageObj);
//	   FileInputStream serviceAccount =
//               new FileInputStream(DOCTOR_FIREBASE_JSON);
//	   FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount))
//	              
//               .setDatabaseUrl("https://healthcocoplus-1383.firebaseio.com")
//     
//               .build();
//	   
//	 
//	  // FirebaseApp fire=FirebaseApp.initializeApp(options);
//	   
//	   FirebaseApp fire = null;
//	    List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
//	    if(firebaseApps!=null && !firebaseApps.isEmpty()){
//	        for(FirebaseApp app : firebaseApps){
//	            if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
//	                fire = app;
//	        }
//	    }
//	    else
//	        fire = FirebaseApp.initializeApp(options); 
//	   
//	   System.out.println(""+pushToken);
//	   String response=FirebaseMessaging.getInstance(fire).send(messageObj);
			
			PushNotificationCollection pushNotificationCollection = new PushNotificationCollection(null, deviceIds,
					message, DeviceType.IOS, null, PushNotificationType.INDIVIDUAL);
			pushNotificationRepository.save(pushNotificationCollection);
			logger.info("Message Result: " + response.toString());
		} 
//		catch (FirebaseMessagingException jpe) {
//			jpe.printStackTrace();
//		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void broadcastNotification(BroadcastNotificationRequest request) {
		// Boolean response = false;
		try {
			String imageUrl = null;
			if (request.getImage() != null) {
				String path = "broadcastImages";
				// save image
				request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
						true);
				imageUrl = getFinalImageURL(imageURLResponse.getImageUrl());
			}
			Collection<String> pushTokens = null;
			Collection<String> deviceIds = null;
			List<UserDeviceCollection> deviceCollections = null;
			if (request.getUserType().equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.DOCTOR.getRole(),
						DeviceType.ANDROID.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, null,
							RoleEnum.DOCTOR.getRole(), DeviceType.ANDROID.getType());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.DOCTOR.getRole(),
						DeviceType.ANDROID_PAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, null,
							RoleEnum.DOCTOR.getRole(), DeviceType.ANDROID_PAD.getType());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.DOCTOR.getRole(),
						DeviceType.IOS.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, DeviceType.IOS.getType(),
							RoleEnum.DOCTOR.getRole());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.DOCTOR.getRole(),
						DeviceType.IPAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl,
							DeviceType.IPAD.getType(), RoleEnum.DOCTOR.getRole());
				}
			} else if (request.getUserType().equalsIgnoreCase(RoleEnum.PATIENT.getRole())) {
				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.PATIENT.getRole(),
						DeviceType.ANDROID.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, null,
							RoleEnum.PATIENT.getRole(), DeviceType.ANDROID.getType());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.PATIENT.getRole(),
						DeviceType.ANDROID_PAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, null,
							RoleEnum.PATIENT.getRole(), DeviceType.ANDROID_PAD.getType());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.PATIENT.getRole(),
						DeviceType.IOS.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, DeviceType.IOS.getType(),
							RoleEnum.PATIENT.getRole());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.PATIENT.getRole(),
						DeviceType.IPAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl,
							DeviceType.IPAD.getType(), RoleEnum.PATIENT.getRole());
				}
			} else {
				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.DOCTOR.getRole(),
						DeviceType.ANDROID.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, null,
							RoleEnum.DOCTOR.getRole(), DeviceType.ANDROID.getType());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.DOCTOR.getRole(),
						DeviceType.ANDROID_PAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, null,
							RoleEnum.DOCTOR.getRole(), DeviceType.ANDROID_PAD.getType());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.DOCTOR.getRole(),
						DeviceType.IOS.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, DeviceType.IOS.getType(),
							RoleEnum.DOCTOR.getRole());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.DOCTOR.getRole(),
						DeviceType.IPAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl,
							DeviceType.IPAD.getType(), RoleEnum.DOCTOR.getRole());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.PATIENT.getRole(),
						DeviceType.ANDROID.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, null,
							RoleEnum.PATIENT.getRole(), DeviceType.ANDROID.getType());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.PATIENT.getRole(),
						DeviceType.ANDROID_PAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, null,
							RoleEnum.PATIENT.getRole(), DeviceType.ANDROID_PAD.getType());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.PATIENT.getRole(),
						DeviceType.IOS.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, DeviceType.IOS.getType(),
							RoleEnum.PATIENT.getRole());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.PATIENT.getRole(),
						DeviceType.IPAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl,
							DeviceType.IPAD.getType(), RoleEnum.PATIENT.getRole());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return response;
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

	public void pushNotificationOnAndroidDevices(String deviceId, String pushToken, String message,
			String componentType, String requestId, String responseId, String role) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Sender sender = null;

			if (role.toString().equalsIgnoreCase(RoleEnum.PHARMIST.getRole().toString())) {
				// sender = new Sender(DOCTOR_GEOCODING_SERVICES_API_KEY);
				sender = new FCMSender(PHARMIST_GEOCODING_SERVICES_API_KEY);
			} else {
				// sender = new Sender(PATIENT_GEOCODING_SERVICES_API_KEY);
				sender = new FCMSender(PATIENT_GEOCODING_SERVICES_API_KEY);
			}

			Notification notification = new Notification();
			notification.setTitle("Healthcoco");
			notification.setText(message);

			if (requestId != null) {
				notification.setReq(requestId);
			}

			if (responseId != null) {
				notification.setRes(responseId);
			}

			notification.setNotificationType(componentType);
			String jsonOutput = mapper.writeValueAsString(notification);
			System.out.println(jsonOutput);
			Message messageObj = new Message.Builder().delayWhileIdle(true).addData("message", jsonOutput).build();
			Result result = sender.send(messageObj, pushToken, 1);
			List<String> deviceIds = new ArrayList<String>();
			deviceIds.add(deviceId);
			PushNotificationCollection pushNotificationCollection = new PushNotificationCollection(null, deviceIds,
					message, DeviceType.ANDROID, null, PushNotificationType.INDIVIDUAL);
			pushNotificationRepository.save(pushNotificationCollection);
			logger.info("Message Result: " + result.toString());
		} catch (JsonProcessingException jpe) {
			jpe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	public Boolean notifyRefresh(String id, String requestId, String responseId, RoleEnum role, String message,
			ComponentType componentType) {
		Boolean response = false;
		List<UserDeviceCollection> userDeviceCollections = null;

		try {
			if (role.equals(RoleEnum.PHARMIST)) {
				userDeviceCollections = userDeviceRepository.findByLocaleId(new ObjectId(id));
			} else {
				userDeviceCollections = userDeviceRepository.findByUserIds(new ObjectId(id));
			}
			if (userDeviceCollections != null && !userDeviceCollections.isEmpty()) {
				for (UserDeviceCollection userDeviceCollection : userDeviceCollections) {
					if (userDeviceCollection.getDeviceType() != null) {
						if (userDeviceCollection.getDeviceType().getType()
								.equalsIgnoreCase(DeviceType.ANDROID.getType())) {

							pushNotificationOnAndroidDevices(userDeviceCollection.getDeviceId(),
									userDeviceCollection.getPushToken(), message, componentType.getType(), requestId,
									responseId, role.getRole(), null);
							response = true;
						} else if (userDeviceCollection.getDeviceType().getType()
								.equalsIgnoreCase(DeviceType.IOS.getType()) && role.equals(RoleEnum.PATIENT)) {
							pushNotificationOnIosDevices(userDeviceCollection.getDeviceId(),
									userDeviceCollection.getPushToken(), message, componentType.getType(), requestId,
									responseId, userDeviceCollection.getRole().getRole(), null);
							userDeviceCollection.setBatchCount(userDeviceCollection.getBatchCount() + 1);
							userDeviceRepository.save(userDeviceCollection);
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
	public Boolean notifyAll(RoleEnum role, List<ObjectId>userIds, String message) {
		Boolean response = false;
		System.out.println("userIds"+userIds.size());
		List<UserDeviceCollection> deviceCollections = null;
		Collection<String> pushTokens = null;
		Collection<String> deviceIds = null;
		if (role.getRole().equals(RoleEnum.PHARMIST.getRole())) {
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria().and("role").is(role)
					.and("deviceType").is(DeviceType.ANDROID.getType()).and("localeId").in(userIds)));
			deviceCollections = mongoTemplate
					.aggregate(aggregation, UserDeviceCollection.class, UserDeviceCollection.class).getMappedResults();
			pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));
			deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
			if (pushTokens != null && !pushTokens.isEmpty() && deviceIds != null && !deviceIds.isEmpty()) {
				broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
						new ArrayList<String>(pushTokens), message, null, null, role.toString(),
						DeviceType.ANDROID.getType());
			}
		} else if(role.getRole().equals(RoleEnum.PATIENT.getRole())) {
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria().and("role").is(role)
					.and("deviceType").is(DeviceType.ANDROID.getType()).and("userIds").in(userIds)));
			deviceCollections = mongoTemplate
					.aggregate(aggregation, UserDeviceCollection.class, UserDeviceCollection.class).getMappedResults();
			pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));
			deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
			if (pushTokens != null && !pushTokens.isEmpty() && deviceIds != null && !deviceIds.isEmpty()) {
				broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
						new ArrayList<String>(pushTokens), message,null, null, role.toString(),
						DeviceType.ANDROID.getType());
//				broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
//						new ArrayList<String>(pushTokens), message, null, DeviceType.ANDROID.getType(), role);
			}

			aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria().and("role").is(role)
					.and("deviceType").is(DeviceType.ANDROID_PAD.getType()).and("userIds").in(userIds)));
			deviceCollections = mongoTemplate
					.aggregate(aggregation, UserDeviceCollection.class, UserDeviceCollection.class).getMappedResults();
			pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));
			deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
			if (pushTokens != null && !pushTokens.isEmpty() && deviceIds != null && !deviceIds.isEmpty()) {
				broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
						new ArrayList<String>(pushTokens), message, null, null, role.toString(),
						DeviceType.ANDROID_PAD.getType());
			}
			aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria().and("role").is(role)
					.and("deviceType").is(DeviceType.IOS.getType()).and("userIds").in(userIds)));
			deviceCollections = mongoTemplate
					.aggregate(aggregation, UserDeviceCollection.class, UserDeviceCollection.class).getMappedResults();
			pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));
			deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
			if (pushTokens != null && !pushTokens.isEmpty()) {
				broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
						new ArrayList<String>(pushTokens), message, null, DeviceType.IOS.getType(), role.toString());
			}
			aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria().and("role").is(role)
					.and("deviceType").is(DeviceType.IPAD.getType()).and("userIds").in(userIds)));
			deviceCollections = mongoTemplate
					.aggregate(aggregation, UserDeviceCollection.class, UserDeviceCollection.class).getMappedResults();
			pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));
			deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
			if (pushTokens != null && !pushTokens.isEmpty()) {
				broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
						new ArrayList<String>(pushTokens), message, null, DeviceType.IPAD.getType(), role.toString());
			}
		}

		else if(role.getRole().equals(RoleEnum.DOCTOR.getRole())) {
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria().and("role").is(role)
					.and("deviceType").is(DeviceType.ANDROID.getType()).and("userIds").in(userIds)));
			deviceCollections = mongoTemplate
					.aggregate(aggregation, UserDeviceCollection.class, UserDeviceCollection.class).getMappedResults();
			pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));
			deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
			if (pushTokens != null && !pushTokens.isEmpty() && deviceIds != null && !deviceIds.isEmpty()) {
				broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
						new ArrayList<String>(pushTokens), message, null, null, role.toString(),
						DeviceType.ANDROID.getType());
//				broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
//						new ArrayList<String>(pushTokens), message, null, DeviceType.ANDROID.getType(), role);

			}

			aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria().and("role").is(role)
					.and("deviceType").is(DeviceType.ANDROID_PAD.getType()).and("userIds").in(userIds)));
			deviceCollections = mongoTemplate
					.aggregate(aggregation, UserDeviceCollection.class, UserDeviceCollection.class).getMappedResults();
			pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));
			deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
			if (pushTokens != null && !pushTokens.isEmpty() && deviceIds != null && !deviceIds.isEmpty()) {
				broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
						new ArrayList<String>(pushTokens), message, null, null, role.toString(),
						DeviceType.ANDROID_PAD.getType());
			}
			aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria().and("role").is(role)
					.and("deviceType").is(DeviceType.IOS.getType()).and("userIds").in(userIds)));
			deviceCollections = mongoTemplate
					.aggregate(aggregation, UserDeviceCollection.class, UserDeviceCollection.class).getMappedResults();
			pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));
			deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
			if (pushTokens != null && !pushTokens.isEmpty()) {
				broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
						new ArrayList<String>(pushTokens), message, null, DeviceType.IOS.getType(), role.toString());
			}
			aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria().and("role").is(role)
					.and("deviceType").is(DeviceType.IPAD.getType()).and("userIds").in(userIds)));
			deviceCollections = mongoTemplate
					.aggregate(aggregation, UserDeviceCollection.class, UserDeviceCollection.class).getMappedResults();
			pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));
			deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
			if (pushTokens != null && !pushTokens.isEmpty()) {
				broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
						new ArrayList<String>(pushTokens), message, null, DeviceType.IPAD.getType(), role.toString());
			}
		}

		
		response = true;

		return response;
	}

}
