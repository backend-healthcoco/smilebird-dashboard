package com.dpdocter.services.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.Achievement;
import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.DoctorExperienceDetail;
import com.dpdocter.beans.DoctorRegistrationDetail;
import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.DoctorTrainingAddEditRequest;
import com.dpdocter.beans.Education;
import com.dpdocter.beans.PatientDownloadData;
import com.dpdocter.beans.UploadFile;
import com.dpdocter.beans.UploadFilePath;
import com.dpdocter.beans.User;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.collections.DoctorSignupRequestCollection;
import com.dpdocter.collections.UploadFileCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.AchievementType;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.Day;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorSignupRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.UploadFileRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DoctorAchievementAddEditRequest;
import com.dpdocter.request.DoctorConsultationFeeAddEditRequest;
import com.dpdocter.request.DoctorEducationAddEditRequest;
import com.dpdocter.request.DoctorExperienceAddEditRequest;
import com.dpdocter.request.DoctorExperienceDetailAddEditRequest;
import com.dpdocter.request.DoctorProfessionalAddEditRequest;
import com.dpdocter.request.DoctorRegistrationAddEditRequest;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.DoctorVisitingTimeAddEditRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.DoctorProfileService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.ImageUploadService;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.SignUpService;
import com.mongodb.BasicDBObject;
import com.opencsv.CSVWriter;

import common.util.web.CSVUtils;
import common.util.web.DPDoctorUtils;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {

	private static Logger logger = LogManager.getLogger(ImageUploadServiceImpl.class.getName());

	@Autowired
	private FileManager fileManager;

	@Autowired
	private UploadFileRepository uploadFileRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${list.doctors.not.registered.file}")
	private String LIST_DOCTORS_NOT_UPLOADED_FILE;

	private static final String NEW_LINE_SEPARATOR = "\n";

	private static final String FILE_HEADER = "FirstName";

	private static final String FILE_HEADER_CLINIC = "EmailAddress";

	@Value(value = "${upload.doctors.data.file}")
	private String UPLOAD_DOCTORS_DATA_FILE;

	@Value(value = "${upload.clinic.data.file}")
	private String UPLOAD_CLINIC_DATA_FILE;

	@Value(value = "${list.clinic.not.registered.file}")
	private String LIST_CLINIC_NOT_UPLOADED_FILE;

	@Autowired
	private UserRepository userRepository;

//	@Value(value = "${doctor.count}")
//	private String doctorCount;
	@Autowired
	private DoctorSignupRepository doctorSignupRepository;

	@Autowired
	private SignUpService signUpService;

	@Autowired
	private DoctorProfileService doctorProfileService;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private RegistrationService registrationService;

	@Override
	public ImageURLResponse uploadFile(MultipartFile file, String filepath, Boolean createThumbnail, String type) {
		String recordPath = null;
		ImageURLResponse response = null;

		try {

			Date createdTime = new Date();
			if (file != null) {

				String path = filepath + File.separator;
				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String fileName = file.getOriginalFilename().replaceFirst("." + fileExtension, "");

				recordPath = path + fileName + createdTime.getTime() + "." + fileExtension;
				response = fileManager.saveImage(file, recordPath, createThumbnail);

				UploadFileCollection uploadFile = new UploadFileCollection();

				uploadFile.setImageUrl(response.getImageUrl());
				if (response.getThumbnailUrl() != null)
					uploadFile.setThumbnailUrl(response.getThumbnailUrl());

				uploadFile.setType(type);
				uploadFile.setPath(filepath);
				uploadFile.setCreatedTime(new Date());
				uploadFile.setUpdatedTime(new Date());
				uploadFileRepository.save(uploadFile);
				// exeriseImage.setImageUrl(exeriseImage.getImageUrl());
				// exeriseImage.setThumbnailUrl(exeriseImage.getThumbnailUrl());

//				if(imageURLResponse != null)
//				{
//					imageURLResponse.setImageUrl(imagePath + imageURLResponse.getImageUrl());
//					imageURLResponse.setThumbnailUrl(imagePath + imageURLResponse.getThumbnailUrl()); 
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public List<UploadFile> getFiles(String searchTerm, String type, int page, int size) {
		List<UploadFile> response = null;
		try {

			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (searchTerm != null)
				criteria = criteria.orOperator(new Criteria("path").regex("^" + searchTerm, "i"),
						new Criteria("path").regex("^" + searchTerm));

			if (type != null)
				criteria.and("type").is(type);

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			response = mongoTemplate.aggregate(aggregation, UploadFileCollection.class, UploadFile.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Upload Files");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Upload Files");
		}
		return response;

	}

	@Override
	public List<UploadFilePath> getPath(String searchTerm, int page, int size) {
		List<UploadFilePath> response = null;
		try {

			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (searchTerm != null)
				criteria = criteria.orOperator(new Criteria("path").regex("^" + searchTerm, "i"),
						new Criteria("path").regex("^" + searchTerm));

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			response = mongoTemplate.aggregate(aggregation, UploadFileCollection.class, UploadFilePath.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Upload Files Path");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Upload Files Path");
		}
		return response;

	}

	@Override
	public Boolean uploadDoctorData() {
		Boolean response = false;
		FileWriter fileWriter = null;
		Scanner scanner = null;
		int lineCount = 0;
		String csvLine = null;
		try {
			fileWriter = new FileWriter(LIST_DOCTORS_NOT_UPLOADED_FILE);
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;

			DoctorSignupRequest request = null;

			DoctorRegistrationAddEditRequest registration = new DoctorRegistrationAddEditRequest();

			scanner = new Scanner(new File(UPLOAD_DOCTORS_DATA_FILE));

			Integer pNUMIndex = null, firstNameIndex = null, mobileNumberIndex = null, contactNumberIndex = null,
					emailAddressIndex = null, alternateMobileNumberIndex = null, genderIndex = null,
					localityIndex = null, specialityIndex = null, streetAddressIndex = null, locationIndex = null,
					cityIndex = null, pincodeIndex = null, latlongIndex = null, stateIndex = null,
					educationDetailsIndex = null, nationalIdIndex = null, dobIndex = null, ageIndex = null,
					bloodGroupIndex = null, clinicFeesIndex = null, countryIndex = null, remarksIndex = null,
					medicalHistoryIndex = null, referredByIndex = null, groupsIndex = null, experienceIndex = null,
					patientNotesIndex = null, patientRegistrationDateIndex = null, registrationDetailsIndex = null,
					clinicTimingIndex = null, monIndex = null, tueIndex = null, wedIndex = null, thuIndex = null,
					friIndex = null, satIndex = null, sunIndex = null, awardIndex = null, membershipIndex = null,
					experienceInDetailsIndex = null, trainingIndex = null;

			while (scanner.hasNext()) {
				csvLine = scanner.nextLine();
				List<String> line = CSVUtils.parseLine(csvLine);

				if (lineCount == 0) {
					if (line != null && !line.isEmpty()) {
						for (int i = 0; i < line.size(); i++) {

							System.out.println("line.size()" + line.size());

							String key = line.get(i).trim().replaceAll("[^a-zA-Z]", "").toUpperCase();

							switch (key) {

							case "FIRSTNAME":
								firstNameIndex = i;
								break;
							case "EMAILADDRESS":
								emailAddressIndex = i;
								break;

							case "MOBILENUMBER":
								mobileNumberIndex = i;
								break;
							case "GENDER":
								genderIndex = i;
								break;

							case "LOCATIONNAME":
								locationIndex = i;
								break;

							case "COUNTRY":
								countryIndex = i;
								break;

							case "STATE":
								stateIndex = i;
								break;

							case "CITY":
								cityIndex = i;
								break;

							case "STREETADDRESS":
								streetAddressIndex = i;
								break;

							case "LOCALITY":
								localityIndex = i;
								break;

							case "LATLONG":
								latlongIndex = i;
								break;

							case "SPECIALITY":
								specialityIndex = i;
								break;

							case "REGISTRATIONDETAILS":
								registrationDetailsIndex = i;
								break;

							case "CLINICFEES":
								clinicFeesIndex = i;
								break;

							case "EXPERIENCE":
								experienceIndex = i;
								break;

							case "MON":
								monIndex = i;
								break;

							case "TUE":
								tueIndex = i;
								break;

							case "WED":
								wedIndex = i;
								break;

							case "THU":
								thuIndex = i;
								break;

							case "FRI":
								friIndex = i;
								break;

							case "SAT":
								satIndex = i;
								break;

							case "SUN":
								sunIndex = i;
								break;

							case "EDUCATIONDETAILS":
								educationDetailsIndex = i;
								break;

							case "AWARDS":
								awardIndex = i;
								break;

							case "MEMBERSHIPS":
								membershipIndex = i;
								break;

							case "EXPERIENCEINDETAILS":
								experienceInDetailsIndex = i;
								break;

							case "TRANNINGANDCERTIFICATION":
								trainingIndex = i;
								break;

							default:
								break;
							}
						}
					}
				} else {
					int count = 0;
					request = new DoctorSignupRequest();

					DoctorRegistrationDetail detail = new DoctorRegistrationDetail();
					List<DoctorRegistrationDetail> details = new ArrayList<DoctorRegistrationDetail>();
					if (!DPDoctorUtils.anyStringEmpty(line.get(emailAddressIndex))) {
						String email = line.get(emailAddressIndex).replaceAll("'", "").replaceAll("\"", "");

						System.out.println("emailAddressIndex" + email);

						request.setEmailAddress(email);
					}
					if (!DPDoctorUtils.anyStringEmpty(line.get(mobileNumberIndex))) {
						String mobileNumberValue = line.get(mobileNumberIndex).replaceAll("'", "").replaceAll("\"", "");
						if (!mobileNumberValue.equalsIgnoreCase("NONE")) {
							if (mobileNumberValue.startsWith("+91"))
								mobileNumberValue = mobileNumberValue.replace("+91", "");
							request.setMobileNumber(mobileNumberValue);

							List<UserCollection> userCollections = userRepository.findByEmailAddressAndUserState(
									mobileNumberValue, UserState.USERSTATECOMPLETE.getState());
							if (userCollections != null && !userCollections.isEmpty()) {
								for (UserCollection userCollection : userCollections) {
									if (!userCollection.getUserName()
											.equalsIgnoreCase(userCollection.getEmailAddress()))
										count++;
								}
							}

						}

					}

					if (count < 1) {
						if (firstNameIndex != null) {
							String patientName = line.get(firstNameIndex).replaceAll("'", "").replaceAll("\"", "");
							if (checkIfNotNullOrNone(patientName)) {
								String separator = ".";
								int sepPos = patientName.indexOf(separator);
								patientName = patientName.substring(sepPos + 1);

								request.setFirstName(patientName);
								// request.setLocalPatientName(patientName);
							}
						}

						if (genderIndex != null) {
							String gender = line.get(genderIndex).replaceAll("'", "").replaceAll("\"", "");
							if (checkIfNotNullOrNone(gender)) {
								if (gender.equalsIgnoreCase("F"))
									gender = "FEMALE";
								else if (gender.equalsIgnoreCase("M"))
									gender = "MALE";
								request.setGender(gender);
							}
						}

						if (dobIndex != null) {
							String dateOfBirth = line.get(dobIndex).replaceAll("'", "").replaceAll("\"", "");
							if (checkIfNotNullOrNone(dateOfBirth)) {
								String[] dob = dateOfBirth.split("-");

								DOB dobObject = new DOB(Integer.parseInt(dob[2]), Integer.parseInt(dob[1]),
										Integer.parseInt(dob[0]));
								request.setDob(dobObject);
							}
						}

						if (ageIndex != null
								&& checkIfNotNullOrNone(line.get(ageIndex).replaceAll("'", "").replaceAll("\"", ""))) {
							String ageValue = line.get(ageIndex).replaceAll("'", "").replaceAll("\"", "");
							String regex = "^\\d*\\.\\d+|\\d+\\.\\d*$";
							// check string contain decimal point or not
							if (ageValue.matches(regex)) {
								System.out.println(true);
								int indexOfDecimal = ageValue.indexOf(".");
								String ageStr = ageValue.substring(0, indexOfDecimal) + "Y "
										+ ageValue.substring(indexOfDecimal).replace(".", "") + "M";
								System.out.print(ageStr);
								String[] age = ageStr.split(" ");
								int year = 0, month = 0, day = 0;
								for (String str : age) {
									if (str.contains("Y"))
										year = Integer.parseInt(str.replace("Y", ""));
									else if (str.contains("M"))
										month = Integer.parseInt(str.replace("M", ""));
									// else if(str.contains("D"))day = Integer.parseInt(str.replace("D", ""));
								}

								Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
								int currentDay = localCalendar.get(Calendar.DATE) - day;
								int currentMonth = localCalendar.get(Calendar.MONTH) + 1 - month;
								int currentYear = localCalendar.get(Calendar.YEAR) - year;
								if (currentMonth < 0) {
									currentYear = currentYear - 1;
									currentMonth = 12 + currentMonth;
								}

								request.setDob(new DOB(currentDay, currentMonth, currentYear));

							} else {
								System.out.println(false);

								String[] age = ageValue.split(" ");
								int year = 0, month = 0, day = 0;
								for (String str : age) {
									if (str.contains("Y"))
										year = Integer.parseInt(str.replace("Y", ""));
									else if (str.contains("M"))
										month = Integer.parseInt(str.replace("M", ""));
									else if (str.contains("D"))
										day = Integer.parseInt(str.replace("D", ""));
								}

								Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
								int currentDay = localCalendar.get(Calendar.DATE) - day;
								int currentMonth = localCalendar.get(Calendar.MONTH) + 1 - month;
								int currentYear = localCalendar.get(Calendar.YEAR) - year;
								if (currentMonth < 0) {
									currentYear = currentYear - 1;
									currentMonth = 12 + currentMonth;
								}

							}
						}

						String speciality = null;
						if (specialityIndex != null && checkIfNotNullOrNone(
								line.get(specialityIndex).replaceAll("'", "").replaceAll("\"", ""))) {
							System.out.println(line.get(specialityIndex));
							speciality = line.get(specialityIndex);
							String specialitys[] = speciality.split("\\+");
							String[] trimmedArray = new String[specialitys.length];
							for (int i = 0; i < specialitys.length; i++)
								trimmedArray[i] = specialitys[i].trim();

							request.setSpecialities(Arrays.asList(trimmedArray));
							System.out.println(request.getSpecialities());
						}

						if (locationIndex != null && checkIfNotNullOrNone(
								line.get(locationIndex).replaceAll("'", "").replaceAll("\"", "")))
							request.setLocationName(line.get(locationIndex).replaceAll("'", ""));

						String country = null, city = null, state = null, postalCode = null, locality = null,
								streetAddress = null, lat = null, lon = null;

						if (streetAddressIndex != null && checkIfNotNullOrNone(
								line.get(streetAddressIndex).replaceAll("'", "").replaceAll("\"", "")))
							streetAddress = line.get(streetAddressIndex).replaceAll("\\+", " ");
						request.setStreetAddress(streetAddress);

						if (localityIndex != null && checkIfNotNullOrNone(
								line.get(localityIndex).replaceAll("'", "").replaceAll("\"", ""))) {
							locality = line.get(localityIndex).replaceAll("'", "");
							String localitys[] = locality.split("\\+");
							request.setLocality(localitys[0]);
							System.out.println(localitys[1]);
							if (localitys[1] != null) {
								request.setCity(localitys[1]);
							}
						}

						if (cityIndex != null
								&& checkIfNotNullOrNone(line.get(cityIndex).replaceAll("'", "").replaceAll("\"", ""))) {
							city = line.get(cityIndex).replaceAll("'", "");
							request.setCity(city);
						}

						if (stateIndex != null && checkIfNotNullOrNone(
								line.get(stateIndex).replaceAll("'", "").replaceAll("\"", ""))) {
							state = line.get(stateIndex).replaceAll("'", "");
							request.setState(state);
						}

						country = "India";
						request.setCountry(country);

						if (latlongIndex != null && checkIfNotNullOrNone(
								line.get(latlongIndex).replaceAll("'", "").replaceAll("\"", ""))) {
							lat = line.get(latlongIndex).replaceAll("'", "");

							String latt[] = lat.split("\\+");
							request.setLatitude(Double.parseDouble(latt[0]));
							request.setLongitude(Double.parseDouble(latt[1]));
						}
						if (pincodeIndex != null && checkIfNotNullOrNone(
								line.get(pincodeIndex).replaceAll("'", "").replaceAll("\"", "")))
							postalCode = line.get(pincodeIndex).replaceAll("'", "");

						// 05-12-2019 14:51string expi = null;

						String expi = null;
						SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/y HH:mm");
						// String dateSTri = line.get(patientRegistrationDateIndex).replace("\"", "");
						dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
						// Date date = dateFormat.parse(dateSTri);

						DoctorSignupRequestCollection collection = new DoctorSignupRequestCollection();
						BeanUtil.map(request, collection);
						doctorSignupRepository.save(collection);

						DoctorSignUp signup = null;
						if (collection != null) {
							signup = signUpService.doctorSignUp(request);
							System.out.println(signup);

//							System.out.println("locationId " + signup.getUser().getLocationId());
							String reg = null;
							if (registrationDetailsIndex != null && checkIfNotNullOrNone(
									line.get(registrationDetailsIndex).replaceAll("'", "").replaceAll("\"", " "))) {
								// reg = line.get(registrationDetailsIndex).replaceAll("|", " ");
								System.out.println(line.get(registrationDetailsIndex));
								String regs[] = line.get(registrationDetailsIndex).split("\\+");
								System.out.println(regs[0]);
								detail.setRegistrationId(regs[0]);
								detail.setMedicalCouncil(regs[1]);
								detail.setYearOfPassing(Integer.parseInt(regs[2].strip()));
								details.add(detail);
								registration.setRegistrationDetails(details);
								registration.setDoctorId(signup.getUser().getId());
								System.out.println(registration);
								if (signup.getUser() != null) {
									registration.setDoctorId(signup.getUser().getId());
									doctorProfileService.addEditRegistrationDetail(registration);
								}
							}

								DoctorExperienceAddEditRequest exp = new DoctorExperienceAddEditRequest();
								if (experienceIndex != null && checkIfNotNullOrNone(
										line.get(experienceIndex).replaceAll("'", "").replaceAll("\"", ""))) {
									expi = line.get(experienceIndex).replaceAll("'", "");
									String expr[] = expi.split(" ");
									exp.setDoctorId(signup.getUser().getId());
									exp.setExperience(Integer.parseInt(expr[0]));
									doctorProfileService.addEditExperience(exp);
								}

								if (clinicFeesIndex != null && checkIfNotNullOrNone(
										line.get(clinicFeesIndex).replaceAll("'", "").replaceAll("\"", ""))) {
									String fees = line.get(clinicFeesIndex).replaceAll("'", "");

									ConsultationFee fee = new ConsultationFee();
									String separat = " ";
									int sepPos = fees.indexOf(separat);
									String str = fees.substring(sepPos + 1);
									fee.setAmount(Integer.parseInt(str));
									DoctorConsultationFeeAddEditRequest consultation = new DoctorConsultationFeeAddEditRequest();
									consultation.setConsultationFee(fee);
									consultation.setDoctorId(signup.getUser().getId());
									consultation.setLocationId(
											signup.getHospital().getLocationsAndAccessControl().get(0).getId());
									System.out.println(consultation);
									doctorProfileService.addEditConsultationFee(consultation);
								}

								if (educationDetailsIndex != null && checkIfNotNullOrNone(
										line.get(educationDetailsIndex).replaceAll("'", "").replaceAll("\"", ""))) {
									String ed = line.get(educationDetailsIndex).replaceAll("'", "");
									if (!ed.contains("|")) {
										String edn[] = ed.split("\\+");
										String stringArr = Arrays.toString(edn);
										System.out.println("edn" + stringArr);
										DoctorEducationAddEditRequest doctoredu = new DoctorEducationAddEditRequest();

										List<Education> eduList = new ArrayList<Education>();
										Education edu = new Education();

										for (int i = 0; i < edn.length; i++) {
											System.out.println("main " + 1 + edn[i]);
											if (i == 0) {
												edu.setQualification(edn[i]);
											} else if (i == 1) {
												edu.setCollegeUniversity(edn[i]);
											} else if (i == 2) {
												edu.setYearOfPassing(Integer.parseInt(edn[i].replaceAll("\\s", "")));
											}
										}
										eduList.add(edu);

										doctoredu.setEducation(eduList);
										doctoredu.setDoctorId(signup.getUser().getId());
										doctorProfileService.addEditEducation(doctoredu);
									} else {
										System.out.println("list");
										String ednList[] = ed.split("\\|");
										String stringArrList = Arrays.toString(ednList);
										System.out.println("ednL" + stringArrList);
										System.out.println("ednL" + stringArrList.length());
										System.out.println("ednList.length" + ednList.length);
										DoctorEducationAddEditRequest doctoredu = new DoctorEducationAddEditRequest();
										List<Education> eduList = new ArrayList<Education>();

										for (int i = 0; i < ednList.length; i++) {
											String edn[] = ednList[i].split("\\+");
											String stringArr = Arrays.toString(edn);
											System.out.println("edn" + stringArr);
											System.out.println("edn" + stringArr.length());

											System.out.println("edn.length" + edn.length);
											Education edu = new Education();
											for (int j = 0; j <= edn.length - 1; j++) {
												System.out.println("main " + j + edn[j]);
												if (j == 0) {
													edu.setQualification(edn[j]);
												} else if (j == 1) {
													edu.setCollegeUniversity(edn[j]);
												} else if (j == 2) {
													edu.setYearOfPassing(
															Integer.parseInt(edn[j].replaceAll("\\s", "")));
												}

											}
											System.out.println("obj" + edu);
											eduList.add(edu);

										}
										System.out.println(eduList);
										doctoredu.setEducation(eduList);
										doctoredu.setDoctorId(signup.getUser().getId());
										doctorProfileService.addEditEducation(doctoredu);

									}
								}
							
							String timing[] = null;

							String time = null;

							List<WorkingSchedule> schedules = new ArrayList<WorkingSchedule>();

							if (monIndex != null && checkIfNotNullOrNone(
									line.get(monIndex).replaceAll("'", "").replaceAll("\"", " "))) {
								WorkingSchedule schedule = new WorkingSchedule();
								List<WorkingHours> hour = new ArrayList<WorkingHours>();
								schedule.setWorkingDay(Day.MONDAY);
								time = line.get(monIndex);
								timing = time.split("\\+");
								if (timing[0].contains("ON-CALL"))
									schedule.setIsByAppointment(true);

								else
									for (int i = 0; i < timing.length; i++) {
										WorkingHours hours = new WorkingHours();
										String trimmedArray[] = timing[i].split("\\-");
										String[] temp = new String[trimmedArray.length];
										for (int j = 0; j < trimmedArray.length; j++) {
											temp[j] = trimmedArray[j].trim();
											System.out.println("temp " + temp[j].toString());
										}
										if (temp[0].contains("AM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											System.out.println("separator" + index);
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											System.out.println("from time" + from);
											from = from * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											System.out.println("minutes" + minutes);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[0].contains("PM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[1].contains("AM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index - 1);
											Integer from = Integer.parseInt(temp2);
											from = from * 60;
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										System.out.println("hours" + hours);
										if (temp[1].contains("PM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											System.out.println(temp[1]);
											System.out.println("to" + from);
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										hour.add(hours);
										schedule.setWorkingHours(hour);

									}
								schedules.add(schedule);

							}

							if (tueIndex != null && checkIfNotNullOrNone(
									line.get(tueIndex).replaceAll("'", "").replaceAll("\"", " "))) {
								WorkingSchedule schedule = new WorkingSchedule();
								List<WorkingHours> hour = new ArrayList<WorkingHours>();
								schedule.setWorkingDay(Day.TUESDAY);
								time = line.get(tueIndex);
								timing = time.split("\\+");

								if (timing[0].contains("ON-CALL"))
									schedule.setIsByAppointment(true);
								else
									for (int i = 0; i < timing.length; i++) {
										WorkingHours hours = new WorkingHours();
										String trimmedArray[] = timing[i].split("\\-");
										String[] temp = new String[trimmedArray.length];
										for (int j = 0; j < trimmedArray.length; j++)
											temp[j] = trimmedArray[j].trim();
										System.out.println("temp " + temp);
										if (temp[0].contains("AM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											System.out.println("separator" + index);
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											System.out.println("from time" + from);
											from = from * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											System.out.println("minutes" + minutes);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[0].contains("PM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[1].contains("AM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index - 1);
											Integer from = Integer.parseInt(temp2);
											from = from * 60;
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										System.out.println("hours" + hours);
										if (temp[1].contains("PM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											System.out.println(temp[1]);
											System.out.println("to" + from);
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										hour.add(hours);
										schedule.setWorkingHours(hour);
									}
								schedules.add(schedule);

							}

							if (wedIndex != null && checkIfNotNullOrNone(
									line.get(wedIndex).replaceAll("'", "").replaceAll("\"", " "))) {
								WorkingSchedule schedule = new WorkingSchedule();
								List<WorkingHours> hour = new ArrayList<WorkingHours>();
								schedule.setWorkingDay(Day.WEDNESDAY);
								time = line.get(wedIndex);
								timing = time.split("\\+");

								if (timing[0].contains("ON-CALL"))
									schedule.setIsByAppointment(true);
								else
									for (int i = 0; i < timing.length; i++) {
										WorkingHours hours = new WorkingHours();
										String trimmedArray[] = timing[i].split("\\-");
										String[] temp = new String[trimmedArray.length];
										for (int j = 0; j < trimmedArray.length; j++)
											temp[j] = trimmedArray[j].trim();
										System.out.println("temp " + temp);
										if (temp[0].contains("AM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											System.out.println("separator" + index);
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											System.out.println("from time" + from);
											from = from * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											System.out.println("minutes" + minutes);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[0].contains("PM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[1].contains("AM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index - 1);
											Integer from = Integer.parseInt(temp2);
											from = from * 60;
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										System.out.println("hours" + hours);
										if (temp[1].contains("PM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											System.out.println(temp[1]);
											System.out.println("to" + from);
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										hour.add(hours);
										schedule.setWorkingHours(hour);
									}
								schedules.add(schedule);

							}

							if (thuIndex != null && checkIfNotNullOrNone(
									line.get(thuIndex).replaceAll("'", "").replaceAll("\"", " "))) {
								WorkingSchedule schedule = new WorkingSchedule();
								List<WorkingHours> hour = new ArrayList<WorkingHours>();
								schedule.setWorkingDay(Day.THURSDAY);
								time = line.get(thuIndex);
								timing = time.split("\\+");

								if (timing[0].contains("ON-CALL"))
									schedule.setIsByAppointment(true);
								else
									for (int i = 0; i < timing.length; i++) {
										WorkingHours hours = new WorkingHours();
										String trimmedArray[] = timing[i].split("\\-");
										String[] temp = new String[trimmedArray.length];
										for (int j = 0; j < trimmedArray.length; j++)
											temp[j] = trimmedArray[j].trim();
										System.out.println("temp " + temp);
										if (temp[0].contains("AM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											System.out.println("separator" + index);
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											System.out.println("from time" + from);
											from = from * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											System.out.println("minutes" + minutes);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[0].contains("PM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[1].contains("AM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index - 1);
											Integer from = Integer.parseInt(temp2);
											from = from * 60;
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										System.out.println("hours" + hours);
										if (temp[1].contains("PM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											System.out.println(temp[1]);
											System.out.println("to" + from);
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										hour.add(hours);
										schedule.setWorkingHours(hour);
									}
								schedules.add(schedule);

							}

							if (friIndex != null && checkIfNotNullOrNone(
									line.get(friIndex).replaceAll("'", "").replaceAll("\"", " "))) {
								WorkingSchedule schedule = new WorkingSchedule();
								List<WorkingHours> hour = new ArrayList<WorkingHours>();
								schedule.setWorkingDay(Day.FRIDAY);
								time = line.get(friIndex);
								timing = time.split("\\+");

								if (timing[0].contains("ON-CALL"))
									schedule.setIsByAppointment(true);
								else
									for (int i = 0; i < timing.length; i++) {
										WorkingHours hours = new WorkingHours();
										String trimmedArray[] = timing[i].split("\\-");
										String[] temp = new String[trimmedArray.length];
										for (int j = 0; j < trimmedArray.length; j++)
											temp[j] = trimmedArray[j].trim();
										System.out.println("temp " + temp);
										if (temp[0].contains("AM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											System.out.println("separator" + index);
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											System.out.println("from time" + from);
											from = from * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											System.out.println("minutes" + minutes);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[0].contains("PM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[1].contains("AM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index - 1);
											Integer from = Integer.parseInt(temp2);
											from = from * 60;
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										System.out.println("hours" + hours);
										if (temp[1].contains("PM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											System.out.println(temp[1]);
											System.out.println("to" + from);
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										hour.add(hours);
										schedule.setWorkingHours(hour);
									}
								schedules.add(schedule);

							}
							if (satIndex != null && checkIfNotNullOrNone(
									line.get(satIndex).replaceAll("'", "").replaceAll("\"", " "))) {
								WorkingSchedule schedule = new WorkingSchedule();
								List<WorkingHours> hour = new ArrayList<WorkingHours>();
								schedule.setWorkingDay(Day.SATURDAY);
								time = line.get(satIndex);
								timing = time.split("\\+");

								if (timing[0].contains("ON-CALL"))
									schedule.setIsByAppointment(true);
								else
									for (int i = 0; i < timing.length; i++) {
										WorkingHours hours = new WorkingHours();
										String trimmedArray[] = timing[i].split("\\-");
										String[] temp = new String[trimmedArray.length];
										for (int j = 0; j < trimmedArray.length; j++)
											temp[j] = trimmedArray[j].trim();
										System.out.println("temp " + temp);
										if (temp[0].contains("AM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											System.out.println("separator" + index);
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											System.out.println("from time" + from);
											from = from * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											System.out.println("minutes" + minutes);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[0].contains("PM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[1].contains("AM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index - 1);
											Integer from = Integer.parseInt(temp2);
											from = from * 60;
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										System.out.println("hours" + hours);
										if (temp[1].contains("PM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											System.out.println(temp[1]);
											System.out.println("to" + from);
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										hour.add(hours);
										schedule.setWorkingHours(hour);
									}
								schedules.add(schedule);

							}

							if (sunIndex != null && checkIfNotNullOrNone(
									line.get(sunIndex).replaceAll("'", "").replaceAll("\"", " "))) {
								WorkingSchedule schedule = new WorkingSchedule();
								List<WorkingHours> hour = new ArrayList<WorkingHours>();
								schedule.setWorkingDay(Day.SUNDAY);
								time = line.get(sunIndex);
								timing = time.split("\\+");
								if (timing[0].contains("ON-CALL"))
									schedule.setIsByAppointment(true);
								else
									for (int i = 0; i < timing.length; i++) {
										WorkingHours hours = new WorkingHours();
										String trimmedArray[] = timing[i].split("\\-");
										String[] temp = new String[trimmedArray.length];
										for (int j = 0; j < trimmedArray.length; j++)
											temp[j] = trimmedArray[j].trim();
										System.out.println("temp " + temp);
										if (temp[0].contains("AM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											System.out.println("separator" + index);
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											System.out.println("from time" + from);
											from = from * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											System.out.println("minutes" + minutes);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[0].contains("PM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[1].contains("AM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index - 1);
											Integer from = Integer.parseInt(temp2);
											from = from * 60;
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										System.out.println("hours" + hours);
										if (temp[1].contains("PM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											System.out.println(temp[1]);
											System.out.println("to" + from);
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										hour.add(hours);
										schedule.setWorkingHours(hour);
									}
								schedules.add(schedule);

							}

							DoctorVisitingTimeAddEditRequest visitingTime = new DoctorVisitingTimeAddEditRequest();
							visitingTime.setWorkingSchedules(schedules);
							visitingTime.setDoctorId(signup.getUser().getId());
							visitingTime
									.setLocationId(signup.getHospital().getLocationsAndAccessControl().get(0).getId());
							doctorProfileService.addEditVisitingTime(visitingTime);

						}

						// Membership In Details
						System.out.println("membershipIndex" + membershipIndex);
						if (membershipIndex != null && checkIfNotNullOrNone(
								line.get(membershipIndex).replaceAll("'", "").replaceAll("\"", ""))) {
							String membership = line.get(membershipIndex).replaceAll("'", "");

							DoctorProfessionalAddEditRequest doctorexpd = new DoctorProfessionalAddEditRequest();
							String memberships[] = membership.split("\\|");
							String[] trimmedArray = new String[memberships.length];
							for (int i = 0; i < memberships.length; i++)
								trimmedArray[i] = memberships[i].trim();

							doctorexpd.setMembership(Arrays.asList(trimmedArray));
							doctorexpd.setDoctorId(signup.getUser().getId());
							doctorProfileService.addEditProfessionalMembership(doctorexpd);
						}

						// training
						if (trainingIndex != null && checkIfNotNullOrNone(
								line.get(trainingIndex).replaceAll("'", "").replaceAll("\"", ""))) {
							String training = line.get(trainingIndex).replaceAll("'", "");

							DoctorTrainingAddEditRequest doctorexpd = new DoctorTrainingAddEditRequest();
							String memberships[] = training.split("\\|");
							String[] trimmedArray = new String[memberships.length];
							for (int i = 0; i < memberships.length; i++)
								trimmedArray[i] = memberships[i].trim();

							doctorexpd.setTrainingsCertifications(Arrays.asList(trimmedArray));
							doctorexpd.setDoctorId(signup.getUser().getId());
							doctorProfileService.addEditTraining(doctorexpd);
						}

						// awads In Details
						System.out.println("awardIndex" + awardIndex);

						if (awardIndex != null && checkIfNotNullOrNone(
								line.get(awardIndex).replaceAll("'", "").replaceAll("\"", ""))) {
							String award = line.get(awardIndex).replaceAll("'", "");
//								if (!award.contains("|")) {
							String edn[] = award.split("\\|");
							String stringArr = Arrays.toString(edn);
							System.out.println("award" + stringArr);
							DoctorAchievementAddEditRequest doctorexpd = new DoctorAchievementAddEditRequest();

							List<Achievement> eduList = new ArrayList<Achievement>();
							Achievement exd = new Achievement();

							for (int i = 0; i < edn.length; i++) {
								System.out.println("main " + 1 + edn[i]);
//										if (i == 0) {
								System.out.println("acnam" + i + edn[i]);
								exd.setAchievementName(edn[i]);
								exd.setAchievementType(AchievementType.OTHER);
//										} else if (i == 1) {
//											exd.setYear(Integer.parseInt(edn[1]));
//										}
							}
							eduList.add(exd);
							doctorexpd.setAchievements(eduList);
							doctorexpd.setDoctorId(signup.getUser().getId());
							doctorProfileService.addEditAchievement(doctorexpd);
//								} else {
//									System.out.println("list");
//									String ednList[] = award.split("\\|");
//									String stringArrList = Arrays.toString(ednList);
//									System.out.println("award" + stringArrList);
//									System.out.println("award" + stringArrList.length());
//									System.out.println("awardList.length" + ednList.length);
//									DoctorAchievementAddEditRequest doctoredu = new DoctorAchievementAddEditRequest();
//									List<Achievement> eduList = new ArrayList<Achievement>();
//
//									for (int i = 0; i < ednList.length; i++) {
//										String edn[] = ednList[i].split("\\+");
//										String stringArr = Arrays.toString(edn);
//										System.out.println("edn" + stringArr);
//										System.out.println("edn" + stringArr.length());
//
//										System.out.println("edn.length" + edn.length);
//										Achievement exd = new Achievement();
//										for (int j = 0; j < edn.length; j++) {
//											System.out.println("main " + 1 + edn[i]);
//											System.out.println("main " + 1 + edn[i]);
//											if (i == 0) {
//												System.out.println("acnam" + i + edn[i]);
//												exd.setAchievementName(edn[i]);
//												;
//											} else if (i == 1) {
//												exd.setYear(Integer.parseInt(edn[1]));
//											}
//										}
//										System.out.println("obj" + exd);
//										eduList.add(exd);
//									}
//									System.out.println(eduList);
//									doctoredu.setAchievements(eduList);
//									doctoredu.setDoctorId(signup.getUser().getId());
//									doctorProfileService.addEditAchievement(doctoredu);
//								}
						}

						// Experience In Details
						System.out.println("expdetail" + experienceInDetailsIndex);

						if (experienceInDetailsIndex != null && checkIfNotNullOrNone(
								line.get(experienceInDetailsIndex).replaceAll("'", "").replaceAll("\"", ""))) {
							String expd = line.get(experienceInDetailsIndex).replaceAll("'", "");
//								if (!expd.contains("|")) {
							String edn[] = expd.split("\\|");
							String stringArr = Arrays.toString(edn);
							System.out.println("edn" + stringArr);
							DoctorExperienceDetailAddEditRequest doctorexpd = new DoctorExperienceDetailAddEditRequest();

							List<DoctorExperienceDetail> eduList = new ArrayList<DoctorExperienceDetail>();
							DoctorExperienceDetail exd = new DoctorExperienceDetail();

							for (int i = 0; i < edn.length; i++) {
								System.out.println("main " + 1 + edn[i]);
//										if (i == 0 && edn[i].contains("-")) {
//											System.out.println("Year" + i + edn[i]);
//											String[] arrOfStr = edn[i].split("-", 2);
//											exd.setFrom(Integer.parseInt(arrOfStr[0]));
//											if (arrOfStr[1].equalsIgnoreCase("Present")) {
//												Date d = new Date();
//												exd.setTo(d.getYear());
//											} else {
//												exd.setTo(Integer.parseInt(arrOfStr[1]));
//											}
////											        (edn[i]);
//										} else if (i == 1) {
//											exd.setOrganization(edn[i]);
//										} else if (i == 2) {
//											exd.setCity(edn[i]);
//										} else {
								exd.setOrganization(edn[i]);
//										}
							}
							eduList.add(exd);

							doctorexpd.setExperienceDetails(eduList);
							doctorexpd.setDoctorId(signup.getUser().getId());
							doctorProfileService.addEditExperienceDetail(doctorexpd);
//								} 
//								else {
//									System.out.println("list");
//									String ednList[] = expd.split("\\|");
//									String stringArrList = Arrays.toString(ednList);
//									System.out.println("ednL" + stringArrList);
//									System.out.println("ednL" + stringArrList.length());
//									System.out.println("ednList.length" + ednList.length);
//									DoctorExperienceDetailAddEditRequest doctoredu = new DoctorExperienceDetailAddEditRequest();
//									List<DoctorExperienceDetail> eduList = new ArrayList<DoctorExperienceDetail>();
//
//									for (int i = 0; i < ednList.length; i++) {
//										String edn[] = ednList[i].split("\\+");
//										String stringArr = Arrays.toString(edn);
//										System.out.println("edn" + stringArr);
//										System.out.println("edn" + stringArr.length());
//
//										System.out.println("edn.length" + edn.length);
//										DoctorExperienceDetail exd = new DoctorExperienceDetail();
//										for (int j = 0; j < edn.length; j++) {
//											System.out.println("main " + 1 + edn[i]);
//											if (i == 0 && edn[i].contains("-")) {
//												System.out.println("Year" + i + edn[i]);
//												String[] arrOfStr = edn[i].split("-", 2);
//												exd.setFrom(Integer.parseInt(arrOfStr[0]));
//												if (arrOfStr[1].equalsIgnoreCase("Present")) {
//													Date d = new Date();
//													exd.setTo(d.getYear());
//												} else {
//													exd.setTo(Integer.parseInt(arrOfStr[1]));
//												}
////												        (edn[i]);
//											} else if (i == 1) {
//												exd.setOrganization(edn[i]);
//											} else if (i == 2) {
//												exd.setCity(edn[i]);
//											} else {
//												System.out.println("ed" + i + edn[i]);
//												exd.setOrganization(edn[i]);
//											}
//										}
//										System.out.println("obj" + exd);
//										eduList.add(exd);
//									}
//									System.out.println(eduList);
//									doctoredu.setExperienceDetails(eduList);
//									doctoredu.setDoctorId(signup.getUser().getId());
//									doctorProfileService.addEditExperienceDetail(doctoredu);
//								}

						}

						System.out.println(line.get(mobileNumberIndex));
						response = true;
					} else {
						System.out.println(emailAddressIndex + " doctor already exist with this email address "
								+ request.getMobileNumber());
						fileWriter.append(csvLine);
						fileWriter.append(NEW_LINE_SEPARATOR);
					}
				}
				lineCount++;
			}

		} catch (Exception e) {
			response = false;
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				try {
					scanner.close();
					if (fileWriter != null) {
						fileWriter.flush();
						fileWriter.close();
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	private Boolean checkIfNotNullOrNone(String value) {
		if (value.equalsIgnoreCase("NONE'") || value.equalsIgnoreCase("NONE") || DPDoctorUtils.allStringsEmpty(value))
			return false;
		else
			return true;

	}

	@Override
	public Boolean uploadClinicData() {

		Boolean response = false;
		FileWriter fileWriter = null;
		Scanner scanner = null;
		int lineCount = 0;
		String csvLine = null;
		try {
			fileWriter = new FileWriter(LIST_DOCTORS_NOT_UPLOADED_FILE);
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;

			DoctorSignupRequest request = null;

			DoctorRegistrationAddEditRequest registration = new DoctorRegistrationAddEditRequest();

			scanner = new Scanner(new File(UPLOAD_DOCTORS_DATA_FILE));

			Integer pNUMIndex = null, firstNameIndex = null, mobileNumberIndex = null, contactNumberIndex = null,
					emailAddressIndex = null, alternateMobileNumberIndex = null, genderIndex = null,
					localityIndex = null, specialityIndex = null, streetAddressIndex = null, locationIndex = null,
					cityIndex = null, pincodeIndex = null, latlongIndex = null, stateIndex = null,
					educationDetailsIndex = null, nationalIdIndex = null, dobIndex = null, ageIndex = null,
					bloodGroupIndex = null, clinicFeesIndex = null, countryIndex = null, remarksIndex = null,
					medicalHistoryIndex = null, referredByIndex = null, groupsIndex = null, experienceIndex = null,
					patientNotesIndex = null, patientRegistrationDateIndex = null, registrationDetailsIndex = null,
					clinicTimingIndex = null, monIndex = null, tueIndex = null, wedIndex = null, thuIndex = null,
					friIndex = null, satIndex = null, sunIndex = null;

			while (scanner.hasNext()) {
				csvLine = scanner.nextLine();
				List<String> line = CSVUtils.parseLine(csvLine);

				if (lineCount == 0) {
					if (line != null && !line.isEmpty()) {
						for (int i = 0; i < line.size(); i++) {

							String key = line.get(i).trim().replaceAll("[^a-zA-Z]", "").toUpperCase();

							switch (key) {

							case "FIRSTNAME":
								firstNameIndex = i;
								break;
							case "EMAILADDRESS":
								emailAddressIndex = i;
								break;

							case "MOBILENUMBER":
								mobileNumberIndex = i;
								break;

							case "GENDER":
								genderIndex = i;
								break;

							case "LOCATIONNAME":
								locationIndex = i;
								break;

							case "COUNTRY":
								countryIndex = i;
								break;

							case "STATE":
								stateIndex = i;
								break;

							case "CITY":
								cityIndex = i;
								break;
							case "STREETADDRESS":
								streetAddressIndex = i;
								break;

							case "LOCALITY":
								localityIndex = i;
								break;

							case "LATLONG":
								latlongIndex = i;
								break;

							case "SPECIALITY":
								specialityIndex = i;
								break;

							case "REGISTRATIONDETAILS":
								registrationDetailsIndex = i;
								break;

							case "CLINICFEES":
								clinicFeesIndex = i;
								break;

							case "EXPERIENCE":
								experienceIndex = i;
								break;

							case "MON":
								monIndex = i;
								break;

							case "TUE":
								tueIndex = i;
								break;

							case "WED":
								wedIndex = i;
								break;

							case "THU":
								thuIndex = i;
								break;

							case "FRI":
								friIndex = i;
								break;

							case "SAT":
								satIndex = i;
								break;

							case "SUN":
								sunIndex = i;
								break;

							case "EDUCATIONDETAILS":
								educationDetailsIndex = i;
								break;

							default:
								break;
							}
						}
					}
				} else {
					int count = 0;
					request = new DoctorSignupRequest();

					DoctorRegistrationDetail detail = new DoctorRegistrationDetail();
					List<DoctorRegistrationDetail> details = new ArrayList<DoctorRegistrationDetail>();

					if (emailAddressIndex != null && checkIfNotNullOrNone(
							line.get(emailAddressIndex).replaceAll("'", "").replaceAll("\"", "")))
						request.setEmailAddress(line.get(emailAddressIndex).replaceAll("'", "").replaceAll("\"", ""));

					if (!DPDoctorUtils.anyStringEmpty(line.get(mobileNumberIndex))) {
						String mobileNumberValue = line.get(mobileNumberIndex).replaceAll("'", "").replaceAll("\"", "");
						if (!mobileNumberValue.equalsIgnoreCase("NONE")) {
							if (mobileNumberValue.startsWith("+91"))
								mobileNumberValue = mobileNumberValue.replace("+91", "");
							request.setMobileNumber(mobileNumberValue);

							List<UserCollection> userCollections = userRepository.findByEmailAddressAndUserState(
									mobileNumberValue, UserState.USERSTATECOMPLETE.getState());
							if (userCollections != null && !userCollections.isEmpty()) {
								for (UserCollection userCollection : userCollections) {
									if (!userCollection.getUserName()
											.equalsIgnoreCase(userCollection.getEmailAddress()))
										count++;
								}
							}

						}

					}

					if (count < 1) {
						if (firstNameIndex != null) {
							String patientName = line.get(firstNameIndex).replaceAll("'", "").replaceAll("\"", "");
							if (checkIfNotNullOrNone(patientName)) {
								String separator = ".";
								int sepPos = patientName.indexOf(separator);
								patientName = patientName.substring(sepPos + 1);

								request.setFirstName(patientName);
								// request.setLocalPatientName(patientName);
							}
						}

						if (genderIndex != null) {
							String gender = line.get(genderIndex).replaceAll("'", "").replaceAll("\"", "");
							if (checkIfNotNullOrNone(gender)) {
								if (gender.equalsIgnoreCase("F"))
									gender = "FEMALE";
								else if (gender.equalsIgnoreCase("M"))
									gender = "MALE";
								request.setGender(gender);
							}
						}

						if (dobIndex != null) {
							String dateOfBirth = line.get(dobIndex).replaceAll("'", "").replaceAll("\"", "");
							if (checkIfNotNullOrNone(dateOfBirth)) {
								String[] dob = dateOfBirth.split("-");

								DOB dobObject = new DOB(Integer.parseInt(dob[2]), Integer.parseInt(dob[1]),
										Integer.parseInt(dob[0]));
								request.setDob(dobObject);
							}
						}

						if (ageIndex != null
								&& checkIfNotNullOrNone(line.get(ageIndex).replaceAll("'", "").replaceAll("\"", ""))) {
							String ageValue = line.get(ageIndex).replaceAll("'", "").replaceAll("\"", "");
							String regex = "^\\d*\\.\\d+|\\d+\\.\\d*$";
							// check string contain decimal point or not
							if (ageValue.matches(regex)) {
								System.out.println(true);
								int indexOfDecimal = ageValue.indexOf(".");
								String ageStr = ageValue.substring(0, indexOfDecimal) + "Y "
										+ ageValue.substring(indexOfDecimal).replace(".", "") + "M";
								System.out.print(ageStr);
								String[] age = ageStr.split(" ");
								int year = 0, month = 0, day = 0;
								for (String str : age) {
									if (str.contains("Y"))
										year = Integer.parseInt(str.replace("Y", ""));
									else if (str.contains("M"))
										month = Integer.parseInt(str.replace("M", ""));
									// else if(str.contains("D"))day = Integer.parseInt(str.replace("D", ""));
								}

								Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
								int currentDay = localCalendar.get(Calendar.DATE) - day;
								int currentMonth = localCalendar.get(Calendar.MONTH) + 1 - month;
								int currentYear = localCalendar.get(Calendar.YEAR) - year;
								if (currentMonth < 0) {
									currentYear = currentYear - 1;
									currentMonth = 12 + currentMonth;
								}

								request.setDob(new DOB(currentDay, currentMonth, currentYear));

							} else {
								System.out.println(false);

								String[] age = ageValue.split(" ");
								int year = 0, month = 0, day = 0;
								for (String str : age) {
									if (str.contains("Y"))
										year = Integer.parseInt(str.replace("Y", ""));
									else if (str.contains("M"))
										month = Integer.parseInt(str.replace("M", ""));
									else if (str.contains("D"))
										day = Integer.parseInt(str.replace("D", ""));
								}

								Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
								int currentDay = localCalendar.get(Calendar.DATE) - day;
								int currentMonth = localCalendar.get(Calendar.MONTH) + 1 - month;
								int currentYear = localCalendar.get(Calendar.YEAR) - year;
								if (currentMonth < 0) {
									currentYear = currentYear - 1;
									currentMonth = 12 + currentMonth;
								}

							}
						}

						String speciality = null;
						if (specialityIndex != null && checkIfNotNullOrNone(
								line.get(specialityIndex).replaceAll("'", "").replaceAll("\"", ""))) {
							System.out.println(line.get(specialityIndex));
							speciality = line.get(specialityIndex);
							String specialitys[] = speciality.split("\\+");
							String[] trimmedArray = new String[specialitys.length];
							for (int i = 0; i < specialitys.length; i++)
								trimmedArray[i] = specialitys[i].trim();

							request.setSpecialities(Arrays.asList(trimmedArray));
							System.out.println(request.getSpecialities());
						}

						if (locationIndex != null && checkIfNotNullOrNone(
								line.get(locationIndex).replaceAll("'", "").replaceAll("\"", "")))
							request.setLocationName(line.get(locationIndex).replaceAll("'", ""));

						String country = null, city = null, state = null, postalCode = null, locality = null,
								streetAddress = null, lat = null, lon = null, expi = null;

						if (streetAddressIndex != null && checkIfNotNullOrNone(
								line.get(streetAddressIndex).replaceAll("'", "").replaceAll("\"", "")))
							streetAddress = line.get(streetAddressIndex).replaceAll("\\+", " ");
						request.setStreetAddress(streetAddress);

						if (localityIndex != null && checkIfNotNullOrNone(
								line.get(localityIndex).replaceAll("'", "").replaceAll("\"", ""))) {
							locality = line.get(localityIndex).replaceAll("'", "");
							String localitys[] = locality.split("\\+");
							request.setLocality(localitys[0]);
						}

						if (cityIndex != null
								&& checkIfNotNullOrNone(line.get(cityIndex).replaceAll("'", "").replaceAll("\"", "")))
							city = line.get(cityIndex).replaceAll("'", "");
						request.setCity(city);

						if (stateIndex != null
								&& checkIfNotNullOrNone(line.get(stateIndex).replaceAll("'", "").replaceAll("\"", "")))
							state = line.get(stateIndex).replaceAll("'", "");
						request.setState(state);

						country = "India";
						request.setCountry(country);

						if (latlongIndex != null && checkIfNotNullOrNone(
								line.get(latlongIndex).replaceAll("'", "").replaceAll("\"", ""))) {
							lat = line.get(latlongIndex).replaceAll("'", "");

							String latt[] = lat.split("\\+");
							request.setLatitude(Double.parseDouble(latt[0]));
							request.setLongitude(Double.parseDouble(latt[1]));
						}
						if (pincodeIndex != null && checkIfNotNullOrNone(
								line.get(pincodeIndex).replaceAll("'", "").replaceAll("\"", "")))
							postalCode = line.get(pincodeIndex).replaceAll("'", "");

						// 05-12-2019 14:51s
						SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/y HH:mm");
						// String dateSTri = line.get(patientRegistrationDateIndex).replace("\"", "");
						dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
						// Date date = dateFormat.parse(dateSTri);

						DoctorSignupRequestCollection collection = new DoctorSignupRequestCollection();
						BeanUtil.map(request, collection);
						doctorSignupRepository.save(collection);

						DoctorSignUp signup = null;
						if (collection != null) {
							signup = signUpService.doctorSignUp(request);
							System.out.println(signup);

							System.out.println("locationId " + signup.getUser().getLocationId());
							String reg = null;
							if (registrationDetailsIndex != null && checkIfNotNullOrNone(
									line.get(registrationDetailsIndex).replaceAll("'", "").replaceAll("\"", " "))) {
								// reg = line.get(registrationDetailsIndex).replaceAll("|", " ");
								String regs[] = line.get(registrationDetailsIndex).split("\\+");
								detail.setRegistrationId(regs[0]);
								detail.setMedicalCouncil(regs[1]);
								detail.setYearOfPassing(Integer.parseInt(regs[2].strip()));
								details.add(detail);
								registration.setRegistrationDetails(details);
								registration.setDoctorId(signup.getUser().getId());
								System.out.println(registration);
								if (signup.getUser() != null)
									registration.setDoctorId(signup.getUser().getId());
								doctorProfileService.addEditRegistrationDetail(registration);

								DoctorExperienceAddEditRequest exp = new DoctorExperienceAddEditRequest();
								if (experienceIndex != null && checkIfNotNullOrNone(
										line.get(experienceIndex).replaceAll("'", "").replaceAll("\"", ""))) {
									expi = line.get(experienceIndex).replaceAll("'", "");
									String expr[] = expi.split(" ");
									exp.setDoctorId(signup.getUser().getId());
									exp.setExperience(Integer.parseInt(expr[0]));
									doctorProfileService.addEditExperience(exp);
								}

								if (clinicFeesIndex != null && checkIfNotNullOrNone(
										line.get(clinicFeesIndex).replaceAll("'", "").replaceAll("\"", ""))) {
									String fees = line.get(clinicFeesIndex).replaceAll("'", "");

									ConsultationFee fee = new ConsultationFee();
									String separat = " ";
									int sepPos = fees.indexOf(separat);
									String str = fees.substring(sepPos + 1);
									fee.setAmount(Integer.parseInt(str));
									DoctorConsultationFeeAddEditRequest consultation = new DoctorConsultationFeeAddEditRequest();
									consultation.setConsultationFee(fee);
									consultation.setDoctorId(signup.getUser().getId());
									consultation.setLocationId(
											signup.getHospital().getLocationsAndAccessControl().get(0).getId());
									System.out.println(consultation);
									doctorProfileService.addEditConsultationFee(consultation);
								}

								if (educationDetailsIndex != null && checkIfNotNullOrNone(
										line.get(educationDetailsIndex).replaceAll("'", "").replaceAll("\"", ""))) {
									String ed = line.get(educationDetailsIndex).replaceAll("'", "");
									String edn[] = ed.split("\\+");
									DoctorEducationAddEditRequest doctoredu = new DoctorEducationAddEditRequest();

									List<Education> eduList = new ArrayList<Education>();
									for (int i = 0; i < edn.length; i++) {
										Education edu = new Education();
										edu.setQualification(edn[i]);
										eduList.add(edu);
									}

									doctoredu.setEducation(eduList);
									doctoredu.setDoctorId(signup.getUser().getId());
									doctorProfileService.addEditEducation(doctoredu);
								}
							}
							String timing[] = null;

							String time = null;

							List<WorkingSchedule> schedules = new ArrayList<WorkingSchedule>();

							if (monIndex != null && checkIfNotNullOrNone(
									line.get(monIndex).replaceAll("'", "").replaceAll("\"", " "))) {
								WorkingSchedule schedule = new WorkingSchedule();
								List<WorkingHours> hour = new ArrayList<WorkingHours>();
								schedule.setWorkingDay(Day.MONDAY);
								time = line.get(monIndex);
								timing = time.split("\\+");
								if (timing[0].contains("ON-CALL"))
									schedule.setIsByAppointment(true);

								else
									for (int i = 0; i < timing.length; i++) {
										WorkingHours hours = new WorkingHours();
										String trimmedArray[] = timing[i].split("\\-");
										String[] temp = new String[trimmedArray.length];
										for (int j = 0; j < trimmedArray.length; j++) {
											temp[j] = trimmedArray[j].trim();
											System.out.println("temp " + temp[j].toString());
										}
										if (temp[0].contains("AM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											System.out.println("separator" + index);
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											System.out.println("from time" + from);
											from = from * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											System.out.println("minutes" + minutes);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[0].contains("PM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[1].contains("AM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index - 1);
											Integer from = Integer.parseInt(temp2);
											from = from * 60;
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										System.out.println("hours" + hours);
										if (temp[1].contains("PM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											System.out.println(temp[1]);
											System.out.println("to" + from);
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										hour.add(hours);
										schedule.setWorkingHours(hour);

									}
								schedules.add(schedule);

							}

							if (tueIndex != null && checkIfNotNullOrNone(
									line.get(tueIndex).replaceAll("'", "").replaceAll("\"", " "))) {
								WorkingSchedule schedule = new WorkingSchedule();
								List<WorkingHours> hour = new ArrayList<WorkingHours>();
								schedule.setWorkingDay(Day.TUESDAY);
								time = line.get(tueIndex);
								timing = time.split("\\+");

								if (timing[0].contains("ON-CALL"))
									schedule.setIsByAppointment(true);
								else
									for (int i = 0; i < timing.length; i++) {
										WorkingHours hours = new WorkingHours();
										String trimmedArray[] = timing[i].split("\\-");
										String[] temp = new String[trimmedArray.length];
										for (int j = 0; j < trimmedArray.length; j++)
											temp[j] = trimmedArray[j].trim();
										System.out.println("temp " + temp);
										if (temp[0].contains("AM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											System.out.println("separator" + index);
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											System.out.println("from time" + from);
											from = from * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											System.out.println("minutes" + minutes);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[0].contains("PM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[1].contains("AM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index - 1);
											Integer from = Integer.parseInt(temp2);
											from = from * 60;
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										System.out.println("hours" + hours);
										if (temp[1].contains("PM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											System.out.println(temp[1]);
											System.out.println("to" + from);
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										hour.add(hours);
										schedule.setWorkingHours(hour);
									}
								schedules.add(schedule);

							}

							if (wedIndex != null && checkIfNotNullOrNone(
									line.get(wedIndex).replaceAll("'", "").replaceAll("\"", " "))) {
								WorkingSchedule schedule = new WorkingSchedule();
								List<WorkingHours> hour = new ArrayList<WorkingHours>();
								schedule.setWorkingDay(Day.WEDNESDAY);
								time = line.get(wedIndex);
								timing = time.split("\\+");

								if (timing[0].contains("ON-CALL"))
									schedule.setIsByAppointment(true);
								else
									for (int i = 0; i < timing.length; i++) {
										WorkingHours hours = new WorkingHours();
										String trimmedArray[] = timing[i].split("\\-");
										String[] temp = new String[trimmedArray.length];
										for (int j = 0; j < trimmedArray.length; j++)
											temp[j] = trimmedArray[j].trim();
										System.out.println("temp " + temp);
										if (temp[0].contains("AM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											System.out.println("separator" + index);
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											System.out.println("from time" + from);
											from = from * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											System.out.println("minutes" + minutes);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[0].contains("PM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[1].contains("AM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index - 1);
											Integer from = Integer.parseInt(temp2);
											from = from * 60;
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										System.out.println("hours" + hours);
										if (temp[1].contains("PM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											System.out.println(temp[1]);
											System.out.println("to" + from);
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										hour.add(hours);
										schedule.setWorkingHours(hour);
									}
								schedules.add(schedule);

							}

							if (thuIndex != null && checkIfNotNullOrNone(
									line.get(thuIndex).replaceAll("'", "").replaceAll("\"", " "))) {
								WorkingSchedule schedule = new WorkingSchedule();
								List<WorkingHours> hour = new ArrayList<WorkingHours>();
								schedule.setWorkingDay(Day.THURSDAY);
								time = line.get(thuIndex);
								timing = time.split("\\+");

								if (timing[0].contains("ON-CALL"))
									schedule.setIsByAppointment(true);
								else
									for (int i = 0; i < timing.length; i++) {
										WorkingHours hours = new WorkingHours();
										String trimmedArray[] = timing[i].split("\\-");
										String[] temp = new String[trimmedArray.length];
										for (int j = 0; j < trimmedArray.length; j++)
											temp[j] = trimmedArray[j].trim();
										System.out.println("temp " + temp);
										if (temp[0].contains("AM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											System.out.println("separator" + index);
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											System.out.println("from time" + from);
											from = from * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											System.out.println("minutes" + minutes);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[0].contains("PM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[1].contains("AM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index - 1);
											Integer from = Integer.parseInt(temp2);
											from = from * 60;
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										System.out.println("hours" + hours);
										if (temp[1].contains("PM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											System.out.println(temp[1]);
											System.out.println("to" + from);
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										hour.add(hours);
										schedule.setWorkingHours(hour);
									}
								schedules.add(schedule);

							}

							if (friIndex != null && checkIfNotNullOrNone(
									line.get(friIndex).replaceAll("'", "").replaceAll("\"", " "))) {
								WorkingSchedule schedule = new WorkingSchedule();
								List<WorkingHours> hour = new ArrayList<WorkingHours>();
								schedule.setWorkingDay(Day.FRIDAY);
								time = line.get(friIndex);
								timing = time.split("\\+");

								if (timing[0].contains("ON-CALL"))
									schedule.setIsByAppointment(true);
								else
									for (int i = 0; i < timing.length; i++) {
										WorkingHours hours = new WorkingHours();
										String trimmedArray[] = timing[i].split("\\-");
										String[] temp = new String[trimmedArray.length];
										for (int j = 0; j < trimmedArray.length; j++)
											temp[j] = trimmedArray[j].trim();
										System.out.println("temp " + temp);
										if (temp[0].contains("AM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											System.out.println("separator" + index);
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											System.out.println("from time" + from);
											from = from * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											System.out.println("minutes" + minutes);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[0].contains("PM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[1].contains("AM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index - 1);
											Integer from = Integer.parseInt(temp2);
											from = from * 60;
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										System.out.println("hours" + hours);
										if (temp[1].contains("PM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											System.out.println(temp[1]);
											System.out.println("to" + from);
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										hour.add(hours);
										schedule.setWorkingHours(hour);
									}
								schedules.add(schedule);

							}
							if (satIndex != null && checkIfNotNullOrNone(
									line.get(satIndex).replaceAll("'", "").replaceAll("\"", " "))) {
								WorkingSchedule schedule = new WorkingSchedule();
								List<WorkingHours> hour = new ArrayList<WorkingHours>();
								schedule.setWorkingDay(Day.SATURDAY);
								time = line.get(satIndex);
								timing = time.split("\\+");

								if (timing[0].contains("ON-CALL"))
									schedule.setIsByAppointment(true);
								else
									for (int i = 0; i < timing.length; i++) {
										WorkingHours hours = new WorkingHours();
										String trimmedArray[] = timing[i].split("\\-");
										String[] temp = new String[trimmedArray.length];
										for (int j = 0; j < trimmedArray.length; j++)
											temp[j] = trimmedArray[j].trim();
										System.out.println("temp " + temp);
										if (temp[0].contains("AM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											System.out.println("separator" + index);
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											System.out.println("from time" + from);
											from = from * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											System.out.println("minutes" + minutes);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[0].contains("PM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[1].contains("AM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index - 1);
											Integer from = Integer.parseInt(temp2);
											from = from * 60;
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										System.out.println("hours" + hours);
										if (temp[1].contains("PM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											System.out.println(temp[1]);
											System.out.println("to" + from);
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										hour.add(hours);
										schedule.setWorkingHours(hour);
									}
								schedules.add(schedule);

							}

							if (sunIndex != null && checkIfNotNullOrNone(
									line.get(sunIndex).replaceAll("'", "").replaceAll("\"", " "))) {
								WorkingSchedule schedule = new WorkingSchedule();
								List<WorkingHours> hour = new ArrayList<WorkingHours>();
								schedule.setWorkingDay(Day.SUNDAY);
								time = line.get(sunIndex);
								timing = time.split("\\+");
								if (timing[0].contains("ON-CALL"))
									schedule.setIsByAppointment(true);
								else
									for (int i = 0; i < timing.length; i++) {
										WorkingHours hours = new WorkingHours();
										String trimmedArray[] = timing[i].split("\\-");
										String[] temp = new String[trimmedArray.length];
										for (int j = 0; j < trimmedArray.length; j++)
											temp[j] = trimmedArray[j].trim();
										System.out.println("temp " + temp);
										if (temp[0].contains("AM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											System.out.println("separator" + index);
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											System.out.println("from time" + from);
											from = from * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											System.out.println("minutes" + minutes);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[0].contains("PM")) {
											temp[0] = temp[0].strip();
											int index = temp[0].indexOf(":");
											String temp2 = temp[0].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											String minutes = temp[0].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setFromTime(from);
										}
										if (temp[1].contains("AM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index - 1);
											Integer from = Integer.parseInt(temp2);
											from = from * 60;
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										System.out.println("hours" + hours);
										if (temp[1].contains("PM")) {
											temp[1] = temp[1].strip();
											int index = temp[1].indexOf(":");
											String temp2 = temp[1].substring(0, index);
											Integer from = Integer.parseInt(temp2);
											from = (from + 12) * 60;
											System.out.println(temp[1]);
											System.out.println("to" + from);
											String minutes = temp[1].substring(index + 1, index + 3);
											Integer min = Integer.parseInt(minutes);
											from = from + min;
											hours.setToTime(from);
										}
										hour.add(hours);
										schedule.setWorkingHours(hour);
									}
								schedules.add(schedule);

							}

							DoctorVisitingTimeAddEditRequest visitingTime = new DoctorVisitingTimeAddEditRequest();
							visitingTime.setWorkingSchedules(schedules);
							visitingTime.setDoctorId(signup.getUser().getId());
							visitingTime
									.setLocationId(signup.getHospital().getLocationsAndAccessControl().get(0).getId());
							doctorProfileService.addEditVisitingTime(visitingTime);

						}

						System.out.println(line.get(mobileNumberIndex));
						response = true;
					} else {
						System.out.println(emailAddressIndex + " doctor already exist with this email address "
								+ request.getMobileNumber());
						fileWriter.append(csvLine);
						fileWriter.append(NEW_LINE_SEPARATOR);
					}
				}
				lineCount++;
			}

		} catch (Exception e) {
			response = false;
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				try {
					scanner.close();
					if (fileWriter != null) {
						fileWriter.flush();
						fileWriter.close();
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	@Override
	public Boolean downloadUserAppPatientData() {
		Boolean response = false;
		CSVWriter csvWriter = null;
		try {

			Criteria criteria = new Criteria("user.userState").is(UserState.USERSTATECOMPLETE).and("role.role")
					.is("PATIENT").and("user").exists(true).and("user.signedUp").is(true);

			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id").append("title", "$title").append("firstName", "$user.firstName")
							.append("emailAddress", "$user.emailAddress")
							.append("mobileNumber", "$user.mobileNumber")));

			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id")

							.append("firstName", new BasicDBObject("$first", "$firstName"))
							.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
							.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))));

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "userId", "_id", "user"),
					Aggregation.unwind("user"), Aggregation.lookup("role_cl", "roleId", "_id", "role"),
					Aggregation.unwind("role"), Aggregation.match(criteria), project, group);

			List<User> results = mongoTemplate.aggregate(aggregation, UserRoleCollection.class, User.class)
					.getMappedResults();

			System.out.println("result" + results.size());
			csvWriter = new CSVWriter(new FileWriter("/home/ubuntu/Patients.csv"));

			writeHeader(PatientDownloadData.class, csvWriter, ComponentType.PATIENT, null);

			for (User patientDownloadData : results) {
//		    		if(patientDownloadData.getDob() != null) {
//		    			patientDownloadData.setDateOfBirth(patientDownloadData.getDob().getDays() +"/"+ patientDownloadData.getDob().getMonths()+"/" + patientDownloadData.getDob().getYears() +"/");
//		    			if(patientDownloadData.getDob().getAge() != null)patientDownloadData.setAge(patientDownloadData.getDob().getAge().getYears()+"");
//		    		}
//		    		if(patientDownloadData.getRegistrationDate() != null) {
//		    			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
//			    		patientDownloadData.setDate(sdf.format(new Date(patientDownloadData.getRegistrationDate())));
//		    		}

				patientDownloadData.setDob(null);
				writeData(patientDownloadData, csvWriter, ComponentType.PATIENT, null);
				response = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e + "Error downloading patient data");
//			throw new BusinessException(ServiceError.Unknown, "Error downloading patient data");
		} finally {
			try {
				if (csvWriter != null) {
					csvWriter.flush();
					csvWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return response;

	}

	private void writeHeader(Class classOfObject, CSVWriter csvWriter, ComponentType componentType,
			List<String> fieldsName) {
		List<String> headerString = new ArrayList<String>();

		switch (componentType) {
		case PATIENT:
			for (Field field : classOfObject.getDeclaredFields()) {
				field.setAccessible(true);

				if (!field.getName().equalsIgnoreCase("dob") && !field.getName().equalsIgnoreCase("registrationDate")) {
					headerString.add(field.getName());
				}
			}
			break;

		default:
			for (Field field : classOfObject.getDeclaredFields()) {
				field.setAccessible(true);
				headerString.add(field.getName());
			}
			break;
		}

		csvWriter.writeNext(headerString.toArray(new String[0]));
	}

	private void writeData(Object obj, CSVWriter writer, ComponentType componentType, List<String> fieldsName)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		List<String> dataString = new ArrayList<String>();

		switch (componentType) {
		case PATIENT:
			for (Field field : obj.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				if (!field.getName().equalsIgnoreCase("dob") && !field.getName().equalsIgnoreCase("registrationDate")) {
					dataString.add((field.get(obj) != null ? (String) (field.get(obj) + "") : ""));
				}
			}
			break;

		default:
			for (Field field : obj.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				dataString.add((field.get(obj) != null ? (String) (field.get(obj) + "") : ""));
			}
			break;
		}
		writer.writeNext(dataString.toArray(new String[0]));

	}

	@Override
	public String uploadDoctorNewData() {
		String response = null;
		FileWriter fileWriter = null;
		Scanner scanner = null;
		int lineCount = 0;
		String csvLine = null;
		try {
			fileWriter = new FileWriter(LIST_DOCTORS_NOT_UPLOADED_FILE);
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			System.out.println("enter");
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;

			DoctorSignupRequest request = null;

			DoctorRegistrationAddEditRequest registration = new DoctorRegistrationAddEditRequest();
			DoctorSignupRequestCollection collection = null;
			String country = null, city = null, state = null, postalCode = null, locality = null, streetAddress = null,
					lat = null, lon = null, expi = null;
			scanner = new Scanner(new File(UPLOAD_DOCTORS_DATA_FILE));

			Integer pNUMIndex = null, firstNameIndex = null, mobileNumberIndex = null, contactNumberIndex = null,
					emailAddressIndex = null, alternateMobileNumberIndex = null, genderIndex = null,
					localityIndex = null, specialityIndex = null, streetAddressIndex = null, locationIndex = null,
					cityIndex = null, pincodeIndex = null, latlongIndex = null, stateIndex = null,
					educationDetailsIndex = null, nationalIdIndex = null, dobIndex = null, ageIndex = null,
					bloodGroupIndex = null, clinicFeesIndex = null, countryIndex = null, remarksIndex = null,
					medicalHistoryIndex = null, referredByIndex = null, groupsIndex = null, experienceIndex = null,
					patientNotesIndex = null, patientRegistrationDateIndex = null, registrationDetailsIndex = null,
					clinicTimingIndex = null, monIndex = null, tueIndex = null, wedIndex = null, thuIndex = null,
					friIndex = null, satIndex = null, sunIndex = null, clinicTimeIndex = null, awardIndex = null,
					membershipIndex = null, experienceInDetailsIndex = null, trainingIndex = null;
			System.out.println("enter 1");

			while (scanner.hasNext()) {
				csvLine = scanner.nextLine();
				System.out.println("csvLine" + csvLine);
				List<String> line = CSVUtils.parseLine(csvLine);
				System.out.println("enter 2");

				if (lineCount == 0) {
					if (line != null && !line.isEmpty()) {
						for (int i = 0; i < line.size(); i++) {

							String key = line.get(i).trim().replaceAll("[^a-zA-Z]", "").toUpperCase();
							System.out.println(key);
							switch (key) {

							case "FIRSTNAME":
								firstNameIndex = i;
								break;

							case "EMAILADDRESS":
								emailAddressIndex = i;
								break;

							case "MOBILENUMBER":
								mobileNumberIndex = i;
								break;

							case "GENDER":
								genderIndex = i;
								break;

							case "SPECIALITY":
								specialityIndex = i;
								break;

							case "EXPERIENCE":
								experienceIndex = i;
								break;

							case "LOCALITYCITY":
								localityIndex = i;
								break;

							case "LOCATIONNAME":
								locationIndex = i;
								break;

							case "STREETADDRESS":
								streetAddressIndex = i;
								break;

							case "CLINICTIME":
								clinicTimeIndex = i;
								break;

							case "CLINICFEES":
								clinicFeesIndex = i;
								break;

							case "AWARDS":
								awardIndex = i;
								break;

							case "EDUCATIONDETAILS":
								educationDetailsIndex = i;
								break;

							case "MEMBERSHIPS":
								membershipIndex = i;
								break;

							case "EXPERIENCEINDETAILS":
								experienceInDetailsIndex = i;
								break;

							case "REGISTRATIONDETAILS":
								registrationDetailsIndex = i;
								break;

							case "TRAINING":
								trainingIndex = i;
								break;

							default:
								break;
							}
						}
					}
				} else {
					int count = 0;
					request = new DoctorSignupRequest();

					DoctorRegistrationDetail detail = new DoctorRegistrationDetail();
					List<DoctorRegistrationDetail> details = new ArrayList<DoctorRegistrationDetail>();

					if (!DPDoctorUtils.anyStringEmpty(line.get(emailAddressIndex))) {
						String email = line.get(emailAddressIndex).replaceAll("'", "").replaceAll("\"", "");
						System.out.println("emailAddressIndex" + email);
						request.setEmailAddress(email);
					}
					if (!DPDoctorUtils.anyStringEmpty(line.get(mobileNumberIndex))) {
						String mobileNumberValue = line.get(mobileNumberIndex).replaceAll("'", "").replaceAll("\"", "");
						if (!mobileNumberValue.equalsIgnoreCase("NONE")) {
							if (mobileNumberValue.startsWith("+91"))
								mobileNumberValue = mobileNumberValue.replace("+91", "");
							request.setMobileNumber(mobileNumberValue);

							List<UserCollection> userCollections = userRepository.findByEmailAddressAndUserState(
									mobileNumberValue, UserState.USERSTATECOMPLETE.getState());
							if (userCollections != null && !userCollections.isEmpty()) {
								for (UserCollection userCollection : userCollections) {
									if (!userCollection.getUserName()
											.equalsIgnoreCase(userCollection.getEmailAddress()))
										count++;
								}
							}

						}

					}
					System.out.println(count);

					if (count < 1) {
						try {
							if (firstNameIndex != null) {
								String patientName = line.get(firstNameIndex).replaceAll("'", "").replaceAll("\"", "");
								if (checkIfNotNullOrNone(patientName)) {
									String separator = ".";
									int sepPos = patientName.indexOf(separator);
									patientName = patientName.substring(sepPos + 1);
									request.setFirstName(patientName);
									// request.setLocalPatientName(patientName);
								}
							}

							if (genderIndex != null) {
								String gender = line.get(genderIndex).replaceAll("'", "").replaceAll("\"", "");
								if (checkIfNotNullOrNone(gender)) {
									if (gender.equalsIgnoreCase("F"))
										gender = "FEMALE";
									else if (gender.equalsIgnoreCase("M"))
										gender = "MALE";
									request.setGender(gender);
								}
							}

							if (dobIndex != null) {
								String dateOfBirth = line.get(dobIndex).replaceAll("'", "").replaceAll("\"", "");
								if (checkIfNotNullOrNone(dateOfBirth)) {
									String[] dob = dateOfBirth.split("-");

									DOB dobObject = new DOB(Integer.parseInt(dob[2]), Integer.parseInt(dob[1]),
											Integer.parseInt(dob[0]));
									request.setDob(dobObject);
								}
							}

							if (ageIndex != null && checkIfNotNullOrNone(
									line.get(ageIndex).replaceAll("'", "").replaceAll("\"", ""))) {
								String ageValue = line.get(ageIndex).replaceAll("'", "").replaceAll("\"", "");
								String regex = "^\\d*\\.\\d+|\\d+\\.\\d*$";
								// check string contain decimal point or not
								if (ageValue.matches(regex)) {
									System.out.println(true);
									int indexOfDecimal = ageValue.indexOf(".");
									String ageStr = ageValue.substring(0, indexOfDecimal) + "Y "
											+ ageValue.substring(indexOfDecimal).replace(".", "") + "M";
									System.out.print(ageStr);
									String[] age = ageStr.split(" ");
									int year = 0, month = 0, day = 0;
									for (String str : age) {
										if (str.contains("Y"))
											year = Integer.parseInt(str.replace("Y", ""));
										else if (str.contains("M"))
											month = Integer.parseInt(str.replace("M", ""));
										// else if(str.contains("D"))day = Integer.parseInt(str.replace("D", ""));
									}

									Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
									int currentDay = localCalendar.get(Calendar.DATE) - day;
									int currentMonth = localCalendar.get(Calendar.MONTH) + 1 - month;
									int currentYear = localCalendar.get(Calendar.YEAR) - year;
									if (currentMonth < 0) {
										currentYear = currentYear - 1;
										currentMonth = 12 + currentMonth;
									}

									request.setDob(new DOB(currentDay, currentMonth, currentYear));

								} else {
									System.out.println(false);

									String[] age = ageValue.split(" ");
									int year = 0, month = 0, day = 0;
									for (String str : age) {
										if (str.contains("Y"))
											year = Integer.parseInt(str.replace("Y", ""));
										else if (str.contains("M"))
											month = Integer.parseInt(str.replace("M", ""));
										else if (str.contains("D"))
											day = Integer.parseInt(str.replace("D", ""));
									}

									Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
									int currentDay = localCalendar.get(Calendar.DATE) - day;
									int currentMonth = localCalendar.get(Calendar.MONTH) + 1 - month;
									int currentYear = localCalendar.get(Calendar.YEAR) - year;
									if (currentMonth < 0) {
										currentYear = currentYear - 1;
										currentMonth = 12 + currentMonth;
									}

								}
							}

							String speciality = null;
							if (specialityIndex != null && checkIfNotNullOrNone(
									line.get(specialityIndex).replaceAll("'", "").replaceAll("\"", ""))) {
								System.out.println(line.get(specialityIndex));
								speciality = line.get(specialityIndex);
								String specialitys[] = speciality.split("\\+");
								String[] trimmedArray = new String[specialitys.length];
								for (int i = 0; i < specialitys.length; i++)
									trimmedArray[i] = specialitys[i].trim();

								request.setSpecialities(Arrays.asList(trimmedArray));
								System.out.println(request.getSpecialities());
							}

							if (locationIndex != null && checkIfNotNullOrNone(
									line.get(locationIndex).replaceAll("'", "").replaceAll("\"", ""))) {
								request.setLocationName(line.get(locationIndex).replaceAll("'", ""));
							}

							if (streetAddressIndex != null && checkIfNotNullOrNone(
									line.get(streetAddressIndex).replaceAll("'", "").replaceAll("\"", ""))) {
								streetAddress = line.get(streetAddressIndex).replaceAll("\\+", " ");
								request.setStreetAddress(streetAddress);
							}

							System.out.println("localityIndex" + localityIndex);
							if (localityIndex != null && checkIfNotNullOrNone(
									line.get(localityIndex).replaceAll("'", "").replaceAll("\"", ""))) {
								locality = line.get(localityIndex).replaceAll("'", "");
								String localitys[] = locality.split("\\+");
//								request.setLocality(localitys[0]);
								// set city
								String stringArr = Arrays.toString(localitys);
								System.out.println("edn" + stringArr);
								System.out.println("edn.length" + localitys.length);
								for (int j = 0; j < localitys.length; j++) {
									System.out.println("main " + j + localitys[j]);
									if (j == 0) {
										request.setLocality(localitys[j]);
									} else if (j == 1) {
										System.out.println("city" + localitys[j]);
										request.setCity(localitys[j]);
									}
								}
//								if (localitys[1] != null) {
//									request.setCity(localitys[1]);
//								}
							}

							System.out.println("cityIndex" + cityIndex);
							if (cityIndex != null && checkIfNotNullOrNone(
									line.get(cityIndex).replaceAll("'", "").replaceAll("\"", ""))) {
								city = line.get(cityIndex).replaceAll("'", "");
								request.setCity(city);
							}
							if (stateIndex != null && checkIfNotNullOrNone(
									line.get(stateIndex).replaceAll("'", "").replaceAll("\"", ""))) {
								state = line.get(stateIndex).replaceAll("'", "");
								request.setState(state);
							}

							country = "India";
							request.setCountry(country);

							if (latlongIndex != null && checkIfNotNullOrNone(
									line.get(latlongIndex).replaceAll("'", "").replaceAll("\"", ""))) {
								lat = line.get(latlongIndex).replaceAll("'", "");

								String latt[] = lat.split("\\+");
								request.setLatitude(Double.parseDouble(latt[0]));
								request.setLongitude(Double.parseDouble(latt[1]));
							}
							if (pincodeIndex != null && checkIfNotNullOrNone(
									line.get(pincodeIndex).replaceAll("'", "").replaceAll("\"", "")))
								postalCode = line.get(pincodeIndex).replaceAll("'", "");

							// 05-12-2019 14:51s
							SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/y HH:mm");
							// String dateSTri = line.get(patientRegistrationDateIndex).replace("\"", "");
							dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
							// Date date = dateFormat.parse(dateSTri);

							collection = new DoctorSignupRequestCollection();
							BeanUtil.map(request, collection);
							doctorSignupRepository.save(collection);
						} catch (Exception e) {
							System.out.println("Exception while signup STEP1");
						}

						DoctorSignUp signup = null;
						if (collection != null) {
							signup = signUpService.doctorSignUp(request);
							System.out.println("signup" + signup);
							try {
								System.out.println("ENTER IN 2ND TRY clini" + clinicFeesIndex);
								if (clinicFeesIndex != null && checkIfNotNullOrNone(
										line.get(clinicFeesIndex).replaceAll("'", "").replaceAll("\"", ""))) {
									String fees = line.get(clinicFeesIndex).replaceAll("'", "");

									ConsultationFee fee = new ConsultationFee();
									String separat = " ";
									int sepPos = fees.indexOf(separat);
									String str = fees.substring(sepPos + 1);
									fee.setAmount(Integer.parseInt(str));
									System.out.println("fee" + fee);
									DoctorConsultationFeeAddEditRequest consultation = new DoctorConsultationFeeAddEditRequest();
									consultation.setConsultationFee(fee);
									System.out.println("signup.getUser().getId()" + signup.getUser().getId());
									consultation.setDoctorId(signup.getUser().getId());
									consultation.setLocationId(
											signup.getHospital().getLocationsAndAccessControl().get(0).getId());
									System.out.println(consultation);
									doctorProfileService.addEditConsultationFee(consultation);

									System.out.println("fees add");
								}
								System.out.println("ENTER IN 2ND TRY reg" + registrationDetailsIndex);

								String regList = null;
								if (registrationDetailsIndex != null && checkIfNotNullOrNone(
										line.get(registrationDetailsIndex).replaceAll("'", "").replaceAll("\"", " "))) {
									regList = line.get(registrationDetailsIndex).replaceAll(" ", "");
									System.out.println("regList" + regList);
									if (!regList.contains("|")) {
										String regs[] = line.get(registrationDetailsIndex).split("\\+");
										detail.setRegistrationId(regs[0]);
										detail.setMedicalCouncil(regs[1]);
										detail.setYearOfPassing(Integer.parseInt(regs[2].strip()));
										details.add(detail);
										registration.setRegistrationDetails(details);
										registration.setDoctorId(signup.getUser().getId());
										System.out.println(registration);
										if (signup.getUser() != null)
											registration.setDoctorId(signup.getUser().getId());
										doctorProfileService.addEditRegistrationDetail(registration);
									} else {
										System.out.println("list");
										String ednList[] = regList.split("\\|");
										String stringArrList = Arrays.toString(ednList);
										System.out.println("ednL" + stringArrList);
										System.out.println("ednL" + stringArrList.length());
										System.out.println("reg.length" + ednList.length);
										DoctorRegistrationAddEditRequest doctoredu = new DoctorRegistrationAddEditRequest();
										List<DoctorRegistrationDetail> eduList = new ArrayList<DoctorRegistrationDetail>();

										for (int i = 0; i < ednList.length; i++) {
											String edn[] = ednList[i].split("\\+");
											String stringArr = Arrays.toString(edn);
											System.out.println("edn" + stringArr);
											System.out.println("edn.length" + edn.length);
											DoctorRegistrationDetail edu = new DoctorRegistrationDetail();
											for (int j = 0; j < edn.length; j++) {
												System.out.println("main " + j + edn[j]);
												if (j == 0) {
													edu.setRegistrationId(edn[j]);
												} else if (j == 1) {
													edu.setMedicalCouncil(edn[j]);
												} else if (j == 2) {
													edu.setYearOfPassing(
															Integer.parseInt(edn[j].replaceAll("\\s", "")));
												}
											}
											System.out.println("obj" + edu);
											eduList.add(edu);
										}
										System.out.println(eduList);
										doctoredu.setRegistrationDetails(eduList);
										doctoredu.setDoctorId(signup.getUser().getId());
										doctorProfileService.addEditRegistrationDetail(doctoredu);
									}
								}
								DoctorExperienceAddEditRequest exp = new DoctorExperienceAddEditRequest();
								if (experienceIndex != null && checkIfNotNullOrNone(
										line.get(experienceIndex).replaceAll("'", "").replaceAll("\"", ""))) {
									expi = line.get(experienceIndex).replaceAll("'", "");
									String expr[] = expi.split(" ");
									exp.setDoctorId(signup.getUser().getId());
									exp.setExperience(Integer.parseInt(expr[0]));
									System.out.println("edu" + expr[0]);
									doctorProfileService.addEditExperience(exp);
								}

								if (educationDetailsIndex != null && checkIfNotNullOrNone(
										line.get(educationDetailsIndex).replaceAll("'", "").replaceAll("\"", ""))) {
									String ed = line.get(educationDetailsIndex).replaceAll("'", "");
									if (!ed.contains("|")) {
										String edn[] = ed.split("\\+");
										String stringArr = Arrays.toString(edn);
										System.out.println("edn" + stringArr);
										DoctorEducationAddEditRequest doctoredu = new DoctorEducationAddEditRequest();

										List<Education> eduList = new ArrayList<Education>();
										Education edu = new Education();

										for (int i = 0; i < edn.length; i++) {
											System.out.println("main " + 1 + edn[i]);
											if (i == 0) {
												edu.setQualification(edn[i]);
											} else if (i == 1) {
												edu.setCollegeUniversity(edn[i]);
											} else if (i == 2) {
												edu.setYearOfPassing(Integer.parseInt(edn[i].replaceAll("\\s", "")));
											}
										}
										eduList.add(edu);

										doctoredu.setEducation(eduList);
										doctoredu.setDoctorId(signup.getUser().getId());
										doctorProfileService.addEditEducation(doctoredu);
									} else {
										System.out.println("list");
										String ednList[] = ed.split("\\|");
										String stringArrList = Arrays.toString(ednList);
										System.out.println("ednL" + stringArrList);
										System.out.println("ednL" + stringArrList.length());
										System.out.println("ednList.length" + ednList.length);
										DoctorEducationAddEditRequest doctoredu = new DoctorEducationAddEditRequest();
										List<Education> eduList = new ArrayList<Education>();

										for (int i = 0; i < ednList.length; i++) {
											String edn[] = ednList[i].split("\\+");
											String stringArr = Arrays.toString(edn);
											System.out.println("edn" + stringArr);
											System.out.println("edn" + stringArr.length());

											System.out.println("edn.length" + edn.length);
											Education edu = new Education();
											for (int j = 0; j < edn.length; j++) {
												System.out.println("main " + j + edn[j]);
												if (j == 0) {
													edu.setQualification(edn[j]);
												} else if (j == 1) {
													edu.setCollegeUniversity(edn[j]);
												} else if (j == 2) {
													edu.setYearOfPassing(
															Integer.parseInt(edn[j].replaceAll("\\s", "")));
												}

											}
											System.out.println("obj" + edu);
											eduList.add(edu);

										}
										System.out.println(eduList);
										doctoredu.setEducation(eduList);
										doctoredu.setDoctorId(signup.getUser().getId());
										doctorProfileService.addEditEducation(doctoredu);
									}
								}
								// Experience In Details
								System.out.println("membershipIndex" + membershipIndex);

								if (experienceInDetailsIndex != null && checkIfNotNullOrNone(
										line.get(experienceInDetailsIndex).replaceAll("'", "").replaceAll("\"", ""))) {
									String expd = line.get(experienceInDetailsIndex).replaceAll("'", "");
									if (!expd.contains("|")) {
										String edn[] = expd.split("\\+");
										String stringArr = Arrays.toString(edn);
										System.out.println("edn" + stringArr);
										DoctorExperienceDetailAddEditRequest doctorexpd = new DoctorExperienceDetailAddEditRequest();

										List<DoctorExperienceDetail> eduList = new ArrayList<DoctorExperienceDetail>();
										DoctorExperienceDetail exd = new DoctorExperienceDetail();

										for (int i = 0; i < edn.length; i++) {
											System.out.println("main " + 1 + edn[i]);
											if (i == 0 && edn[i].contains("-")) {
												System.out.println("Year" + i + edn[i]);
												String[] arrOfStr = edn[i].split("-", 2);
												exd.setFrom(Integer.parseInt(arrOfStr[0]));
												if (arrOfStr[1].equalsIgnoreCase("Present")) {
													Date d = new Date();
													exd.setTo(d.getYear());
												} else {
													exd.setTo(Integer.parseInt(arrOfStr[1]));
												}
//												        (edn[i]);
											} else if (i == 1) {
												exd.setOrganization(edn[i]);
											} else if (i == 2) {
												exd.setCity(edn[i]);
											} else {
												exd.setOrganization(edn[i]);
											}
										}
										eduList.add(exd);

										doctorexpd.setExperienceDetails(eduList);
										doctorexpd.setDoctorId(signup.getUser().getId());
										doctorProfileService.addEditExperienceDetail(doctorexpd);
									} else {
										System.out.println("list");
										String ednList[] = expd.split("\\|");
										String stringArrList = Arrays.toString(ednList);
										System.out.println("ednL" + stringArrList);
										System.out.println("ednL" + stringArrList.length());
										System.out.println("ednList.length" + ednList.length);
										DoctorExperienceDetailAddEditRequest doctoredu = new DoctorExperienceDetailAddEditRequest();
										List<DoctorExperienceDetail> eduList = new ArrayList<DoctorExperienceDetail>();

										for (int i = 0; i < ednList.length; i++) {
											String edn[] = ednList[i].split("\\+");
											String stringArr = Arrays.toString(edn);
											System.out.println("edn" + stringArr);
											System.out.println("edn" + stringArr.length());

											System.out.println("edn.length" + edn.length);
											DoctorExperienceDetail exd = new DoctorExperienceDetail();
											for (int j = 0; j < edn.length; j++) {
												System.out.println("main " + 1 + edn[i]);
												if (i == 0 && edn[i].contains("-")) {
													System.out.println("Year" + i + edn[i]);
													String[] arrOfStr = edn[i].split("-", 2);
													exd.setFrom(Integer.parseInt(arrOfStr[0]));
													if (arrOfStr[1].equalsIgnoreCase("Present")) {
														Date d = new Date();
														exd.setTo(d.getYear());
													} else {
														exd.setTo(Integer.parseInt(arrOfStr[1]));
													}
//													        (edn[i]);
												} else if (i == 1) {
													exd.setOrganization(edn[i]);
												} else if (i == 2) {
													exd.setCity(edn[i]);
												} else {
													System.out.println("ed" + i + edn[i]);
													exd.setOrganization(edn[i]);
												}
											}
											System.out.println("obj" + exd);
											eduList.add(exd);
										}
										System.out.println(eduList);
										doctoredu.setExperienceDetails(eduList);
										doctoredu.setDoctorId(signup.getUser().getId());
										doctorProfileService.addEditExperienceDetail(doctoredu);
									}

								}
							} catch (Exception e) {
								System.out.println(
										"Exception while add education/detail experience part STEP 2" + e.getMessage());
							}

							try {
								// Membership In Details
								System.out.println("membershipIndex" + membershipIndex);
								if (membershipIndex != null && checkIfNotNullOrNone(
										line.get(membershipIndex).replaceAll("'", "").replaceAll("\"", ""))) {
									String membership = line.get(membershipIndex).replaceAll("'", "");

									DoctorProfessionalAddEditRequest doctorexpd = new DoctorProfessionalAddEditRequest();
									String memberships[] = membership.split("\\+");
									String[] trimmedArray = new String[memberships.length];
									for (int i = 0; i < memberships.length; i++)
										trimmedArray[i] = memberships[i].trim();

									doctorexpd.setMembership(Arrays.asList(trimmedArray));
									doctorexpd.setDoctorId(signup.getUser().getId());
									doctorProfileService.addEditProfessionalMembership(doctorexpd);
								}

								// training
								if (trainingIndex != null && checkIfNotNullOrNone(
										line.get(trainingIndex).replaceAll("'", "").replaceAll("\"", ""))) {
									String training = line.get(trainingIndex).replaceAll("'", "");

									DoctorTrainingAddEditRequest doctorexpd = new DoctorTrainingAddEditRequest();
									String memberships[] = training.split("\\+");
									String[] trimmedArray = new String[memberships.length];
									for (int i = 0; i < memberships.length; i++)
										trimmedArray[i] = memberships[i].trim();

									doctorexpd.setTrainingsCertifications(Arrays.asList(trimmedArray));
									doctorexpd.setDoctorId(signup.getUser().getId());
									doctorProfileService.addEditTraining(doctorexpd);
								}
							} catch (Exception e) {
								System.out.println("MemberShip & training" + e.getMessage());
							}
							try {
								// awads In Details
								System.out.println("awardIndex" + awardIndex);

								if (awardIndex != null && checkIfNotNullOrNone(
										line.get(awardIndex).replaceAll("'", "").replaceAll("\"", ""))) {
									String award = line.get(awardIndex).replaceAll("'", "");
									if (!award.contains("|")) {
										String edn[] = award.split("\\+");
										String stringArr = Arrays.toString(edn);
										System.out.println("award" + stringArr);
										DoctorAchievementAddEditRequest doctorexpd = new DoctorAchievementAddEditRequest();

										List<Achievement> eduList = new ArrayList<Achievement>();
										Achievement exd = new Achievement();

										for (int i = 0; i < edn.length; i++) {
											System.out.println("main " + 1 + edn[i]);
											if (i == 0) {
												System.out.println("acnam" + i + edn[i]);
												exd.setAchievementName(edn[i]);
												exd.setAchievementType(AchievementType.OTHER);
											} else if (i == 1) {
												exd.setYear(Integer.parseInt(edn[1]));
											}
										}
										eduList.add(exd);
										doctorexpd.setAchievements(eduList);
										doctorexpd.setDoctorId(signup.getUser().getId());
										doctorProfileService.addEditAchievement(doctorexpd);
									} else {
										System.out.println("list");
										String ednList[] = award.split("\\|");
										String stringArrList = Arrays.toString(ednList);
										System.out.println("award" + stringArrList);
										System.out.println("award" + stringArrList.length());
										System.out.println("awardList.length" + ednList.length);
										DoctorAchievementAddEditRequest doctoredu = new DoctorAchievementAddEditRequest();
										List<Achievement> eduList = new ArrayList<Achievement>();

										for (int i = 0; i < ednList.length; i++) {
											String edn[] = ednList[i].split("\\+");
											String stringArr = Arrays.toString(edn);
											System.out.println("edn" + stringArr);
											System.out.println("edn" + stringArr.length());

											System.out.println("edn.length" + edn.length);
											Achievement exd = new Achievement();
											for (int j = 0; j < edn.length; j++) {
												System.out.println("main " + 1 + edn[i]);
												System.out.println("main " + 1 + edn[i]);
												if (i == 0) {
													System.out.println("acnam" + i + edn[i]);
													exd.setAchievementName(edn[i]);
													;
												} else if (i == 1) {
													exd.setYear(Integer.parseInt(edn[1]));
												}
											}
											System.out.println("obj" + exd);
											eduList.add(exd);
										}
										System.out.println(eduList);
										doctoredu.setAchievements(eduList);
										doctoredu.setDoctorId(signup.getUser().getId());
										doctorProfileService.addEditAchievement(doctoredu);
									}
								}
							} catch (Exception e) {
								System.out.println("Awards" + e.getMessage());
							}

							String timing[] = null;
							String time = null;
							List<WorkingSchedule> schedules = new ArrayList<WorkingSchedule>();
							try {

								if (clinicTimeIndex != null && checkIfNotNullOrNone(
										line.get(clinicTimeIndex).replaceAll("'", "").replaceAll("\"", " "))) {

									System.out.println("clinicTimeIndex" + clinicTimeIndex);
								}
								if (monIndex != null && checkIfNotNullOrNone(
										line.get(monIndex).replaceAll("'", "").replaceAll("\"", " "))) {
									WorkingSchedule schedule = new WorkingSchedule();
									List<WorkingHours> hour = new ArrayList<WorkingHours>();
									schedule.setWorkingDay(Day.MONDAY);
									time = line.get(monIndex);
									timing = time.split("\\+");
									if (timing[0].contains("ON-CALL"))
										schedule.setIsByAppointment(true);

									else
										for (int i = 0; i < timing.length; i++) {
											WorkingHours hours = new WorkingHours();
											String trimmedArray[] = timing[i].split("\\-");
											String[] temp = new String[trimmedArray.length];
											for (int j = 0; j < trimmedArray.length; j++) {
												temp[j] = trimmedArray[j].trim();
												System.out.println("temp " + temp[j].toString());
											}
											if (temp[0].contains("AM")) {
												temp[0] = temp[0].strip();
												int index = temp[0].indexOf(":");
												System.out.println("separator" + index);
												String temp2 = temp[0].substring(0, index);
												Integer from = Integer.parseInt(temp2);
												System.out.println("from time" + from);
												from = from * 60;
												String minutes = temp[0].substring(index + 1, index + 3);
												System.out.println("minutes" + minutes);
												Integer min = Integer.parseInt(minutes);
												from = from + min;
												hours.setFromTime(from);
											}
											if (temp[0].contains("PM")) {
												temp[0] = temp[0].strip();
												int index = temp[0].indexOf(":");
												String temp2 = temp[0].substring(0, index);
												Integer from = Integer.parseInt(temp2);
												from = (from + 12) * 60;
												String minutes = temp[0].substring(index + 1, index + 3);
												Integer min = Integer.parseInt(minutes);
												from = from + min;
												hours.setFromTime(from);
											}
											if (temp[1].contains("AM")) {
												temp[1] = temp[1].strip();
												int index = temp[1].indexOf(":");
												String temp2 = temp[1].substring(0, index - 1);
												Integer from = Integer.parseInt(temp2);
												from = from * 60;
												String minutes = temp[1].substring(index + 1, index + 3);
												Integer min = Integer.parseInt(minutes);
												from = from + min;
												hours.setToTime(from);
											}
											System.out.println("hours" + hours);
											if (temp[1].contains("PM")) {
												temp[1] = temp[1].strip();
												int index = temp[1].indexOf(":");
												String temp2 = temp[1].substring(0, index);
												Integer from = Integer.parseInt(temp2);
												from = (from + 12) * 60;
												System.out.println(temp[1]);
												System.out.println("to" + from);
												String minutes = temp[1].substring(index + 1, index + 3);
												Integer min = Integer.parseInt(minutes);
												from = from + min;
												hours.setToTime(from);
											}
											hour.add(hours);
											schedule.setWorkingHours(hour);

										}
									schedules.add(schedule);

								}
								DoctorVisitingTimeAddEditRequest visitingTime = new DoctorVisitingTimeAddEditRequest();
								visitingTime.setWorkingSchedules(schedules);
								visitingTime.setDoctorId(signup.getUser().getId());
								visitingTime.setLocationId(
										signup.getHospital().getLocationsAndAccessControl().get(0).getId());
								doctorProfileService.addEditVisitingTime(visitingTime);
							} catch (Exception e) {
								System.out.println("Error While timing STEP3" + e.getMessage());
							}
						}

						System.out.println(line.get(mobileNumberIndex));
						response = "Data Uploaded Successfully";
					} else {
						System.out.println(emailAddressIndex + " doctor already exist with this email address "
								+ request.getMobileNumber());
						fileWriter.append(csvLine);
						fileWriter.append(NEW_LINE_SEPARATOR);
					}
				}
				lineCount++;
			}
			System.out.println("scanner not done");
		} catch (Exception e) {
			response = e.getMessage();
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				try {
					scanner.close();
					if (fileWriter != null) {
						fileWriter.flush();
						fileWriter.close();
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}
}
