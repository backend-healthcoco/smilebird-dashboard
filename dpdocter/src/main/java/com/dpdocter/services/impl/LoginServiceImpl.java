package com.dpdocter.services.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.services.LoginService;
import com.dpdocter.services.OTPService;

import common.util.web.DPDoctorUtils;

@Service
public class LoginServiceImpl implements LoginService {

	private static Logger logger = LogManager.getLogger(LoginServiceImpl.class.getName());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;

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
			
			UserCollection userCollection = userRepository.findByMobileNumberAndUserState(mobileNumber,
					UserState.ADMIN.getState());
			if (userCollection == null) {
				throw new BusinessException(ServiceError.NotAuthorized, "Admin with provided mobile number not found");
			} else {
				if (!DPDoctorUtils.anyStringEmpty(userCollection.getEmailAddress())) {
					String emailAddress = userCollection.getEmailAddress();
					response = otpService.otpGeneratorOnEmail(emailAddress, mobileNumber);
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
