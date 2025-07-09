package com.dpdocter.services;

import com.dpdocter.beans.User;

public interface UserService {

	public User loadUserByUsername(String userName);

}
