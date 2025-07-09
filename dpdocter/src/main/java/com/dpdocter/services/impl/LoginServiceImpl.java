package com.dpdocter.services.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.LoginService;
import com.dpdocter.services.OTPService;

import common.util.web.DPDoctorUtils;

@Service
public class LoginServiceImpl implements LoginService {

	private static Logger logger = LogManager.getLogger(LoginServiceImpl.class.getName());

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OTPService otpService;

    @Value(value = "${image.path}")
    private String imagePath;

    @Value(value = "${Signup.role}")
    private String role;
    

    @Override
    @Transactional
    public Boolean adminLogin(String mobileNumber) {
	Boolean response = false;
	try {
		/*RoleCollection roleCollection = roleRepository.findByRole(RoleEnum.SUPER_ADMIN.getRole());
	    if (roleCollection == null) {
		logger.warn(role);
		throw new BusinessException(ServiceError.NoRecord, role);
	    }
	    List<UserRoleCollection> userRoleCollections = userRoleRepository.findByRoleId(roleCollection.getId());
	    @SuppressWarnings("unchecked")
	    Collection<String> userIds = CollectionUtils.collect(userRoleCollections, new BeanToPropertyValueTransformer("userId")); 
	    */
		/*Criteria criteria = new Criteria("mobileNumber").is(request.getMobileNumber()).and("id").in(userIds);
		Query query = new Query(); query.addCriteria(criteria);
		List<UserCollection> userCollections = mongoTemplate.find(query, UserCollection.class);*/
//		UserCollection userCollection = null;
//		if(userCollections != null && !userCollections.isEmpty())userCollection = userCollections.get(0);
	/*    if (userCollections == null || userCollections.isEmpty()) {
		logger.warn("Invalid mobile Number and Password");
		throw new BusinessException(ServiceError.InvalidInput, "Invalid mobile Number and Password");
	    }else{
	    	
	    	for(UserCollection userCollection : userCollections){
	    		char[] salt = userCollection.getSalt();
				char[] passwordWithSalt = new char[request.getPassword().length + salt.length]; 
				for(int i = 0; i < request.getPassword().length; i++)
					        passwordWithSalt[i] = request.getPassword()[i];
				for(int i = 0; i < salt.length; i++)
					    	passwordWithSalt[i+request.getPassword().length] = salt[i];
				if(Arrays.equals(userCollection.getPassword(), DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt))){
					userCollection.setLastSession(new Date());
				    userCollection = userRepository.save(userCollection);
				    response = new User();
				    BeanUtil.map(userCollection, response);
				}
	    	}
	    }*/
	   /*if(response == null){
		   logger.warn(login);
			throw new BusinessException(ServiceError.Unknown, login);
	   }*/
//<<<<<<< Updated upstream
		UserCollection userCollection = userRepository.findByMobileNumberAndUserState(mobileNumber, UserState.ADMIN.getState());
//=======
//		
//		UserCollection userCollection = userRepository.findAdminByMobileNumber(mobileNumber, UserState.ADMIN.getState());
//>>>>>>> Stashed changes
		if(userCollection == null)
		{
			throw new BusinessException(ServiceError.NotAuthorized,"Admin with provided mobile number not found");
		}
		else
		{
			if(!DPDoctorUtils.anyStringEmpty(userCollection.getEmailAddress())) {
			String emailAddress = userCollection.getEmailAddress();
			response = otpService.otpGeneratorOnEmail(emailAddress,mobileNumber);
			}
		}		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while login");
	    throw new BusinessException(ServiceError.Unknown, "Error occured while login");
	}
	return response;
	}
}
