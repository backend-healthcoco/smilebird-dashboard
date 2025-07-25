package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.User;
import com.dpdocter.beans.UserNutritionSubscription;
import com.dpdocter.collections.GenericCollection;

public class UserNutritionSubscriptionResponse extends GenericCollection {

	private User user;
	private List<UserNutritionSubscription> userNutritionSubscription;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<UserNutritionSubscription> getUserNutritionSubscription() {
		return userNutritionSubscription;
	}

	public void setUserNutritionSubscription(List<UserNutritionSubscription> userNutritionSubscription) {
		this.userNutritionSubscription = userNutritionSubscription;
	}

}
