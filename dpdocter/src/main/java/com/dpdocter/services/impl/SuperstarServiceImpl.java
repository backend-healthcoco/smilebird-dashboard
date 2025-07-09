package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DoctorSchoolAssociation;
import com.dpdocter.beans.NutritionSchoolAssociation;
import com.dpdocter.beans.School;
import com.dpdocter.beans.SchoolBranch;
import com.dpdocter.beans.SuperStarHealthCamp;
import com.dpdocter.collections.DoctorSchoolAssociationCollection;
import com.dpdocter.collections.NutritionSchoolAssociationCollection;
import com.dpdocter.collections.SchoolBranchCollection;
import com.dpdocter.collections.SchoolCollection;
import com.dpdocter.collections.SuperStarHealthCampCollection;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.collections.WellnessUserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorSchoolAssociationRepository;
import com.dpdocter.repository.NutritionSchoolAssociationRepository;
import com.dpdocter.repository.SchoolBranchRepository;
import com.dpdocter.repository.SchoolRepository;
import com.dpdocter.repository.SuperStarHealthCampRepository;
import com.dpdocter.repository.TokenRepository;
import com.dpdocter.repository.WellnessUserRepository;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SuperstarService;

import common.util.web.DPDoctorUtils;

@Service
public class SuperstarServiceImpl implements SuperstarService {

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	private SchoolBranchRepository schoolBranchRepository;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private WellnessUserRepository wellnessUserRepository;

	@Autowired
	private SuperStarHealthCampRepository superStarHealthCampRepository;

	@Autowired
	private MailService mailService;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private NutritionSchoolAssociationRepository nutritionSchoolAssociationRepository;
	
	@Autowired
	private DoctorSchoolAssociationRepository doctorSchoolAssociationRepository;

	@Override
	@Transactional
	public School addEditSchool(School request) {
		School response = null;
		SchoolCollection schoolCollection = null;
		WellnessUserCollection wellnessUserCollection = null;
		try {
//<<<<<<< Updated upstream

			if (request.getId() == null) {
				schoolCollection = new SchoolCollection();
				request.setCreatedTime(new Date());
				if (request.getUserName() != null) {
					wellnessUserCollection = new WellnessUserCollection();
					wellnessUserCollection.setUserName(request.getUserName());
					wellnessUserRepository.save(wellnessUserCollection);
				} else {
					throw new BusinessException(ServiceError.Forbidden, "Username cannot be null for new user");
				}
			} else {
				schoolCollection = schoolRepository.findById(new ObjectId(request.getId())).orElse(null);

//=======
//			if(request.getId() == null)
//			{
//				 schoolCollection = new SchoolCollection();
//				 request.setCreatedTime(new Date());
//			}
//			else
//			{
//				schoolCollection = schoolRepository.findById(new ObjectId(request.getId())).orElse(null);
//>>>>>>> Stashed changes
			}
			BeanUtil.map(request, schoolCollection);
			schoolCollection = schoolRepository.save(schoolCollection);
			response = new School();
			BeanUtil.map(schoolCollection, response);
			if (wellnessUserCollection != null) {
				if (schoolCollection.getEmailAddress() != null) {
					TokenCollection tokenCollection = new TokenCollection();
					tokenCollection.setResourceId(wellnessUserCollection.getId());
					tokenCollection.setCreatedTime(new Date());
					tokenCollection = tokenRepository.save(tokenCollection);

					String body = mailBodyGenerator.superStartActivateAccount(schoolCollection.getSchoolName(),
							tokenCollection.getId(), "superstarSetPassword.vm", "School");
					mailService.sendEmail(schoolCollection.getEmailAddress(), "Password setup for Healthcoco account.",
							body, null);
				}
			}

		} catch (DuplicateKeyException de) {
			de.printStackTrace();
			// logger.error(de);
			throw new BusinessException(ServiceError.Unknown, "User already registerd. Please login");
		} catch (BusinessException be) {
			be.printStackTrace();
			// logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e + " Error occured while contacting Healthcoco");
			throw new BusinessException(ServiceError.Unknown, "Error occured while contacting Healthcoco");
		}
		return response;
	}

	@Override
	@Transactional
	public School getSchoolById(String id) {
		School response = null;
		SchoolCollection schoolCollection = null;

		try {

			schoolCollection = schoolRepository.findById(new ObjectId(id)).orElse(null);

			if (schoolCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			schoolCollection = schoolRepository.save(schoolCollection);
			if (schoolCollection != null) {
				response = new School();
				BeanUtil.map(schoolCollection, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public School activateSchool(String id, Boolean isActivated) {
		School response = null;
		SchoolCollection schoolCollection = null;

		try {

			schoolCollection = schoolRepository.findById(new ObjectId(id)).orElse(null);

			if (schoolCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			schoolCollection.setIsActivated(isActivated);
			schoolCollection = schoolRepository.save(schoolCollection);
			if (schoolCollection != null) {
				response = new School();
				BeanUtil.map(schoolCollection, response);
				if (schoolCollection.getEmailAddress() != null) {

				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public School verifySchool(String id, Boolean isVerified) {
		School response = null;
		SchoolCollection schoolCollection = null;

		try {

			schoolCollection = schoolRepository.findById(new ObjectId(id)).orElse(null);

			if (schoolCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			schoolCollection.setIsVerified(isVerified);
			schoolCollection = schoolRepository.save(schoolCollection);
			if (schoolCollection != null) {
				response = new School();
				BeanUtil.map(schoolCollection, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<School> getSchoolList(String updatedTime, String searchTerm, int page, int size, Boolean isActivated) {
		List<School> schools = null;
		Aggregation aggregation = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			/*
			 * if (!DPDoctorUtils.anyStringEmpty(patientId)) {
			 * criteria.and("patientId").is(new ObjectId(patientId)); }
			 */

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("schoolName").regex("^" + searchTerm, "i"),
						new Criteria("userName").regex("^" + searchTerm, "i"));
			}

			if (isActivated != null) {
				criteria.and("isActivated").is(isActivated);
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			schools = mongoTemplate.aggregate(aggregation, SchoolCollection.class, School.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return schools;
	}

	@Override
	@Transactional
	public Integer getSchoolListCount(String updatedTime, String searchTerm, Boolean isActivated) {
		Integer count = 0;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			/*
			 * if (!DPDoctorUtils.anyStringEmpty(patientId)) {
			 * criteria.and("patientId").is(new ObjectId(patientId)); }
			 */

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("schoolName").regex("^" + searchTerm, "i"),
						new Criteria("userName").regex("^" + searchTerm, "i"));
			}

			if (isActivated != null) {
				criteria.and("isActivated").is(isActivated);
			}

			count = (int) mongoTemplate.count(new Query(criteria), SchoolCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return count;
	}

	@Override
	@Transactional
	public List<SchoolBranch> getSchoolBranchesForSchool(String schoolId, String updatedTime, String searchTerm,
			int page, int size, Boolean isActivated) {
		List<SchoolBranch> schoolBranchs = null;
		Aggregation aggregation = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (!DPDoctorUtils.anyStringEmpty(schoolId)) {
				criteria.and("schoolId").is(new ObjectId(schoolId));
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("schoolName").regex("^" + searchTerm, "i"),
						new Criteria("userName").regex("^" + searchTerm, "i"));
			}

			if (isActivated != null) {
				criteria.and("isActivated").is(isActivated);
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			schoolBranchs = mongoTemplate.aggregate(aggregation, SchoolBranchCollection.class, SchoolBranch.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return schoolBranchs;
	}

	@Override
	@Transactional
	public Integer getSchoolBranchesForSchoolCount(String schoolId, String updatedTime, String searchTerm,
			Boolean isActivated) {
		Integer count = 0;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (!DPDoctorUtils.anyStringEmpty(schoolId)) {
				criteria.and("schoolId").is(new ObjectId(schoolId));
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("schoolName").regex("^" + searchTerm, "i"),
						new Criteria("userName").regex("^" + searchTerm, "i"));
			}

			if (isActivated != null) {
				criteria.and("isActivated").is(isActivated);
			}

			count = (int) mongoTemplate.count(new Query(criteria), SchoolBranchCollection.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return count;
	}

	@Override
	@Transactional
	public SchoolBranch addEditSchoolBranch(SchoolBranch request) {
		SchoolBranch response = null;
		SchoolBranchCollection schoolCollection = null;
		WellnessUserCollection wellnessUserCollection = null;

		try {
//<<<<<<< Updated upstream


			if (request.getId() == null) {
				schoolCollection = new SchoolBranchCollection();
				request.setCreatedTime(new Date());
				if (request.getUserName() != null) {
					wellnessUserCollection = new WellnessUserCollection();
					wellnessUserCollection.setUserName(request.getUserName());
					wellnessUserRepository.save(wellnessUserCollection);
				} else {
					throw new BusinessException(ServiceError.Forbidden, "Username cannot be null for new user");
				}
			} else {
				schoolCollection = schoolBranchRepository.findById(new ObjectId(request.getId())).orElse(null);

//=======
//			
//			if(request.getId() == null)
//			{
//				 schoolCollection = new SchoolBranchCollection();
//				 request.setCreatedTime(new Date());
//			}
//			else
//			{
//				schoolCollection = schoolBranchRepository.findById(new ObjectId(request.getId())).orElse(null);
//>>>>>>> Stashed changes
			}
			BeanUtil.map(request, schoolCollection);
			schoolCollection = schoolBranchRepository.save(schoolCollection);
			response = new SchoolBranch();
			BeanUtil.map(schoolCollection, response);
			if (wellnessUserCollection != null) {
				if (schoolCollection.getEmailAddress() != null) {
					TokenCollection tokenCollection = new TokenCollection();
					tokenCollection.setResourceId(wellnessUserCollection.getId());
					tokenCollection.setCreatedTime(new Date());
					tokenCollection = tokenRepository.save(tokenCollection);

					String body = mailBodyGenerator.superStartActivateAccount(schoolCollection.getBranchName(),
							tokenCollection.getId(), "superstarSetPassword.vm", "Branch");
					mailService.sendEmail(schoolCollection.getEmailAddress(), "Password setup for Healthcoco account.",
							body, null);
				}
			}

		} catch (DuplicateKeyException de) {
			de.printStackTrace();
			// logger.error(de);
			throw new BusinessException(ServiceError.Unknown, "Mobile number already registerd. Please login");
		} catch (BusinessException be) {
			be.printStackTrace();
			// logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e + " Error occured while contacting Healthcoco");
			throw new BusinessException(ServiceError.Unknown, "Error occured while contacting Healthcoco");
		}
		return response;
	}

	@Override
	@Transactional
	public SchoolBranch getSchoolBranchById(String id) {
		SchoolBranch response = null;
		SchoolBranchCollection schoolBranchCollection = null;

		try {

			schoolBranchCollection = schoolBranchRepository.findById(new ObjectId(id)).orElse(null);

			if (schoolBranchCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			schoolBranchCollection = schoolBranchRepository.save(schoolBranchCollection);
			if (schoolBranchCollection != null) {
				response = new SchoolBranch();
				BeanUtil.map(schoolBranchCollection, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public SchoolBranch activateSchoolBranch(String id, Boolean isActivated) {
		SchoolBranch response = null;
		SchoolBranchCollection schoolBranchCollection = null;

		try {

			schoolBranchCollection = schoolBranchRepository.findById(new ObjectId(id)).orElse(null);

			if (schoolBranchCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			schoolBranchCollection.setIsActivated(isActivated);
			schoolBranchCollection = schoolBranchRepository.save(schoolBranchCollection);
			if (schoolBranchCollection != null) {
				response = new SchoolBranch();
				BeanUtil.map(schoolBranchCollection, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public SchoolBranch verifySchoolBranch(String id, Boolean isVerified) {
		SchoolBranch response = null;
		SchoolBranchCollection schoolBranchCollection = null;

		try {

			schoolBranchCollection = schoolBranchRepository.findById(new ObjectId(id)).orElse(null);

			if (schoolBranchCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			schoolBranchCollection.setIsVerified(isVerified);
			schoolBranchCollection = schoolBranchRepository.save(schoolBranchCollection);
			if (schoolBranchCollection != null) {
				response = new SchoolBranch();
				BeanUtil.map(schoolBranchCollection, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public SchoolBranch discardSchoolBranch(String id, Boolean discarded) {
		SchoolBranch response = null;
		SchoolBranchCollection schoolBranchCollection = null;

		try {

			schoolBranchCollection = schoolBranchRepository.findById(new ObjectId(id)).orElse(null);

			if (schoolBranchCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			schoolBranchCollection.setDiscarded(discarded);
			schoolBranchCollection = schoolBranchRepository.save(schoolBranchCollection);
			if (schoolBranchCollection != null) {
				response = new SchoolBranch();
				BeanUtil.map(schoolBranchCollection, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<SchoolBranch> getSchoolBranches(String updatedTime, String searchTerm, int page, int size,
			Boolean isActivated, Boolean isDiscarded) {
		List<SchoolBranch> schoolBranchs = null;
		Aggregation aggregation = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			/*
			 * if (!DPDoctorUtils.anyStringEmpty(schoolId)) {
			 * criteria.and("schoolId").is(new ObjectId(schoolId)); }
			 */

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("schoolName").regex("^" + searchTerm, "i"),
						new Criteria("userName").regex("^" + searchTerm, "i"));
			}

			if (isActivated != null) {
				criteria.and("isActivated").is(isActivated);
			}

			if (isDiscarded != null) {
				criteria.and("discarded").is(isDiscarded);
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			schoolBranchs = mongoTemplate.aggregate(aggregation, SchoolBranchCollection.class, SchoolBranch.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return schoolBranchs;
	}

	@Override
	@Transactional
	public Integer getSchoolBranchesCount(String updatedTime, String searchTerm, Boolean isActivated,
			Boolean isDiscarded) {
		Integer count = 0;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			/*
			 * if (!DPDoctorUtils.anyStringEmpty(schoolId)) {
			 * criteria.and("schoolId").is(new ObjectId(schoolId)); }
			 */

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("schoolName").regex("^" + searchTerm, "i"),
						new Criteria("userName").regex("^" + searchTerm, "i"));
			}

			if (isActivated != null) {
				criteria.and("isActivated").is(isActivated);
			}

			if (isDiscarded != null) {
				criteria.and("discarded").is(isDiscarded);
			}

			count = (int) mongoTemplate.count(new Query(criteria), SchoolBranchCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return count;
	}

	@Override
	@Transactional
	public Boolean checkUserNameExists(String userName) {
		Boolean response = false;
		WellnessUserCollection wellnessUserCollection = null;
		try {

			wellnessUserCollection = wellnessUserRepository.findByUserName(userName);

			if (wellnessUserCollection != null) {
				response = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public SuperStarHealthCamp addEditHealthcamp(SuperStarHealthCamp request) {
		SuperStarHealthCamp response = null;
		SuperStarHealthCampCollection superStarHealthCampCollection = null;
		List<ObjectId> doctorObjectIds = null;
		try {
			if (request.getId() == null) {
				superStarHealthCampCollection = new SuperStarHealthCampCollection();
				request.setCreatedTime(new Date());
			} else {
				superStarHealthCampCollection = superStarHealthCampRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			BeanUtil.map(request, superStarHealthCampCollection);
			if (request.getDoctorId() != null) {
				doctorObjectIds = new ArrayList<>();
				for (String doctorId : request.getDoctorId()) {
					doctorObjectIds.add(new ObjectId(doctorId));
				}
			}
			superStarHealthCampCollection.setDoctorId(doctorObjectIds);
			superStarHealthCampCollection.setDoctors(request.getDoctors());
			superStarHealthCampCollection = superStarHealthCampRepository.save(superStarHealthCampCollection);
			response = new SuperStarHealthCamp();
			BeanUtil.map(superStarHealthCampCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e + " Error occured while contacting Healthcoco");
			throw new BusinessException(ServiceError.Unknown, "Error occured while contacting Healthcoco");
		}
		return response;
	}

	@Override
	@Transactional
	public SuperStarHealthCamp getHealthcampById(String id) {
		SuperStarHealthCamp response = null;
		SuperStarHealthCampCollection superStarHealthCampCollection = null;
		/*
		 * List<String> doctorIds = null; List<Doctor> doctors = null;
		 */
		try {

			superStarHealthCampCollection = superStarHealthCampRepository.findById(new ObjectId(id)).orElse(null);
			response = new SuperStarHealthCamp();
			BeanUtil.map(superStarHealthCampCollection, response);
			

			/*
			 * if (superStarHealthCampCollection.getDoctorId() != null) { doctorIds = new
			 * ArrayList<>(); for (ObjectId doctorId :
			 * superStarHealthCampCollection.getDoctorId()) {
			 * doctorIds.add(String.valueOf(doctorId)); }
			 * 
			 * Criteria criteria = new
			 * Criteria("userId").in(superStarHealthCampCollection.getDoctorId());
			 * 
			 * Aggregation aggregation = null;
			 * 
			 * aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
			 * Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			 * 
			 * doctors = mongoTemplate.aggregate(aggregation, DoctorCollection.class,
			 * Doctor.class).getMappedResults(); } response.setDoctors(doctors);
			 * response.setDoctorId(doctorIds);
			 */

		} catch (DuplicateKeyException de) {
			de.printStackTrace();
			// logger.error(de);
			throw new BusinessException(ServiceError.Unknown, "Mobile number already registerd. Please login");
		} catch (BusinessException be) {
			be.printStackTrace();
			// logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e + " Error occured while contacting Healthcoco");
			throw new BusinessException(ServiceError.Unknown, "Error occured while contacting Healthcoco");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean discardHealthCamp(String id, Boolean discarded) {
		Boolean response = false;
		SuperStarHealthCampCollection superStarHealthCampCollection = null;
		try {

			superStarHealthCampCollection = superStarHealthCampRepository.findById(new ObjectId(id)).orElse(null);
			superStarHealthCampCollection.setDiscarded(discarded);
			superStarHealthCampCollection = superStarHealthCampRepository.save(superStarHealthCampCollection);
			response = true;

		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e + " Error occured while contacting Healthcoco");
			throw new BusinessException(ServiceError.Unknown, "Error occured while contacting Healthcoco");
		}
		return response;
	}
	
	
	@Override
	@Transactional
	public List<SuperStarHealthCamp> getHealthcampByDoctorId(String doctorId) {
		List<SuperStarHealthCamp> response = null;
		try {

			Criteria criteria = new Criteria("doctorId").is(doctorId);

			Aggregation aggregation = null;

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Direction.DESC, "createdTime")));

			response = mongoTemplate.aggregate(aggregation, SuperStarHealthCampCollection.class, SuperStarHealthCamp.class).getMappedResults();


		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e + " Error occured while contacting Healthcoco");
			throw new BusinessException(ServiceError.Unknown, "Error occured while contacting Healthcoco");
		}
		return response;
	}
	
	

	@Override
	@Transactional
	public List<SuperStarHealthCamp> getHealthcamps(String updatedTime, String searchTerm, int page, int size) {
		List<SuperStarHealthCamp> healthCamps = null;
		Aggregation aggregation = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
						new Criteria("title").regex("^" + searchTerm, "i"));
			}

		
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			healthCamps = mongoTemplate.aggregate(aggregation, SuperStarHealthCampCollection.class, SuperStarHealthCamp.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return healthCamps;
	}

	
	@Override
	@Transactional
	public Integer getHealthcampCount(String updatedTime, String searchTerm, int page, int size) {
		Integer count = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
						new Criteria("title").regex("^" + searchTerm, "i"));
			}

			count = (int) mongoTemplate.count(new Query(criteria), SuperStarHealthCampCollection.class);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return count;
	}

	
	@Override
	@Transactional
	public NutritionSchoolAssociation addEditNutritionSchoolAssociation(NutritionSchoolAssociation request) {
		NutritionSchoolAssociation response = null;
		NutritionSchoolAssociationCollection nutritionSchoolAssociationCollection = null;
		List<ObjectId> doctorObjectIds = null;
		try {
			if (request.getId() == null) {
				nutritionSchoolAssociationCollection = new NutritionSchoolAssociationCollection();
				request.setCreatedTime(new Date());
			} else {
				nutritionSchoolAssociationCollection = nutritionSchoolAssociationRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			BeanUtil.map(request, nutritionSchoolAssociationCollection);
			if (request.getDoctorId() != null) {
				doctorObjectIds = new ArrayList<>();
				for (String doctorId : request.getDoctorId()) {
					doctorObjectIds.add(new ObjectId(doctorId));
				}
			}
			nutritionSchoolAssociationCollection.setDoctorId(doctorObjectIds);
			nutritionSchoolAssociationCollection.setDoctors(request.getDoctors());
			nutritionSchoolAssociationCollection = nutritionSchoolAssociationRepository.save(nutritionSchoolAssociationCollection);
			response = new NutritionSchoolAssociation();
			BeanUtil.map(nutritionSchoolAssociationCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e + " Error occured while contacting Healthcoco");
			throw new BusinessException(ServiceError.Unknown, "Error occured while contacting Healthcoco");
		}
		return response;
	}
	
	@Override
	@Transactional
	public List<NutritionSchoolAssociation> getNutritionSchoolAssociation(String branchId , String updatedTime, String searchTerm, int page, int size) {
		List<NutritionSchoolAssociation> healthCamps = null;
		Aggregation aggregation = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));
			
			criteria.and("branchId").is(new ObjectId(branchId));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
						new Criteria("title").regex("^" + searchTerm, "i"));
			}

		
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "branch.branchName")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "branch.branchName")));
			}
			
			System.out.println(aggregation);

			healthCamps = mongoTemplate.aggregate(aggregation, NutritionSchoolAssociationCollection.class, NutritionSchoolAssociation.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return healthCamps;
	}

	
	@Override
	@Transactional
	public Integer getNutritionSchoolAssociationCount(String branchId, String updatedTime, String searchTerm, int page, int size) {
		Integer count = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));
			
			criteria.and("branchId").is(new ObjectId(branchId));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
						new Criteria("title").regex("^" + searchTerm, "i"));
			}

			count = (int) mongoTemplate.count(new Query(criteria), NutritionSchoolAssociationCollection.class);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return count;
	}

	@Override
	@Transactional
	public DoctorSchoolAssociation addEditDoctorSchoolAssociation(DoctorSchoolAssociation request) {
		DoctorSchoolAssociation response = null;
		DoctorSchoolAssociationCollection doctorSchoolAssociationCollection = null;
		List<ObjectId> doctorObjectIds = null;
		try {
			if (request.getId() == null) {
				doctorSchoolAssociationCollection = new DoctorSchoolAssociationCollection();
				request.setCreatedTime(new Date());
			} else {
				doctorSchoolAssociationCollection = doctorSchoolAssociationRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			BeanUtil.map(request, doctorSchoolAssociationCollection);
			if (request.getDoctorId() != null) {
				doctorObjectIds = new ArrayList<>();
				for (String doctorId : request.getDoctorId()) {
					doctorObjectIds.add(new ObjectId(doctorId));
				}
			}
			doctorSchoolAssociationCollection.setDoctorId(doctorObjectIds);
			doctorSchoolAssociationCollection.setDoctors(request.getDoctors());
			doctorSchoolAssociationCollection = doctorSchoolAssociationRepository.save(doctorSchoolAssociationCollection);
			response = new DoctorSchoolAssociation();
			BeanUtil.map(doctorSchoolAssociationCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error occured while associating doctor with school branch");
		}
		return response;
	}
	
	@Override
	@Transactional
	public List<DoctorSchoolAssociation> getDoctorSchoolAssociation(String branchId , String updatedTime, String searchTerm, int page, int size) {
		List<DoctorSchoolAssociation> doctorSchoolAssociations = null;
		Aggregation aggregation = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));
			
			criteria.and("branchId").is(new ObjectId(branchId));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("departments.departments").regex("^" + searchTerm, "i"),
						new Criteria("departments.departments").regex("^" + searchTerm, "i"));
			}

		
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "branch.branchName")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "branch.branchName")));
			}
			
			doctorSchoolAssociations = mongoTemplate.aggregate(aggregation, DoctorSchoolAssociationCollection.class, DoctorSchoolAssociation.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error occured while getting associates doctor with school branch");
		}
		return doctorSchoolAssociations;
	}

	
	@Override
	@Transactional
	public Integer getDoctorSchoolAssociationCount(String branchId, String updatedTime, String searchTerm) {
		Integer count = null;
		try {
			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));
			
			criteria.and("branchId").is(new ObjectId(branchId));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("departments.departments").regex("^" + searchTerm, "i"),
						new Criteria("departments.departments").regex("^" + searchTerm, "i"));
			}

			count = (int) mongoTemplate.count(new Query(criteria), DoctorSchoolAssociationCollection.class);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error occured while count associates doctor with school branch");
		}
		return count;
	}
}
