package com.dpdocter.services;

import com.dpdocter.request.ResetPasswordRequest;


public interface ResetPasswordService {

	
	Boolean resetPasswordForUser(ResetPasswordRequest request);
}
