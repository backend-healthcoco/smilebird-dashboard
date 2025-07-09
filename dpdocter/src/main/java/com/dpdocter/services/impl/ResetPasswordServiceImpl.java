package com.dpdocter.services.impl;



import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.ResetPasswordRequest;

import com.dpdocter.services.ResetPasswordService;
import com.dpdocter.tokenstore.CustomPasswordEncoder;



@Service
public class ResetPasswordServiceImpl implements ResetPasswordService {

	@Autowired
	public UserRepository userrepository;
	
	@Autowired
	public CustomPasswordEncoder passwordEncoder;
	
	
	@Override
	public Boolean resetPasswordForUser(ResetPasswordRequest request) {
		
		Boolean response = false;
		UserCollection userCollection =null;
		
		if(request.getUserName()!=null)
		{
			 userCollection= userrepository.findByUserName(request.getUserName());
		}
		else {
			throw new BusinessException(ServiceError.Unknown,
					"Username doesn't exist,please signup first.");
		}
		
		if(userCollection!=null)
		{
			if(request.getPassword()!=null && request.getPassword().length >0) {
				
		userCollection.setPassword(passwordEncoder.encode(new String(request.getPassword())).toCharArray());
			
		userCollection = userrepository.save(userCollection);
		response=true;
			}
		
				
		}
		
		return response;
	}
	
	
}
