package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.NutritionAppointment;
import com.dpdocter.beans.NutritionPlan;
import com.dpdocter.beans.NutritionRDA;
import com.dpdocter.beans.SubscriptionNutritionPlan;
import com.dpdocter.beans.Testimonial;
import com.dpdocter.beans.UserNutritionSubscription;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.NutritionPlanType;
import com.dpdocter.request.AddEditTestimonialRequest;
import com.dpdocter.request.NutritionPlanRequest;
import com.dpdocter.request.PlanPricesPutRemoveRequest;
import com.dpdocter.response.NutritionPlanNameWithCategoryResponse;
import com.dpdocter.response.NutritionPlanResponse;
import com.dpdocter.response.NutritionPlanWithCategoryResponse;
import com.dpdocter.response.UserNutritionSubscriptionResponse;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.Response;

public interface NutritionService {

	public List<NutritionPlanType> getPlanType();

	public NutritionPlan addEditNutritionPlan(NutritionPlan request);

	public NutritionPlan deleteNutritionPlan(String id);

	public NutritionPlanResponse getNutritionPlan(String id);

	public List<NutritionPlan> getNutritionPlans(int page, int size, String type, long updatedTime, boolean discarded);

	public SubscriptionNutritionPlan addEditSubscriptionPlan(SubscriptionNutritionPlan request);

	public SubscriptionNutritionPlan deleteSubscritionPlan(String id);

	public SubscriptionNutritionPlan getSubscritionPlan(String id);

	public List<SubscriptionNutritionPlan> getSubscritionPlans(int page, int size, String nutritionplanId,
			Boolean discarded);

	public List<NutritionPlanWithCategoryResponse> getNutritionPlanByCategory(NutritionPlanRequest request);

	public UserNutritionSubscription getUserSubscritionPlan(String id);

	public List<UserNutritionSubscriptionResponse> getUserSubscritionPlans(int page, int size, long updatedTime,
			boolean discarded, boolean isExpired);

	List<NutritionAppointment> getNutritionAppointments(int page, int size, long updatedTime, boolean discarded);

	public NutritionAppointment updateNutritionAppointmentStatus(String appointmentId, AppointmentState status);

	public List<SubscriptionNutritionPlan> getSubscritionPlans(List<ObjectId> idList);
	
	public List<NutritionPlan> getNutritionPlans(List<ObjectId> idList) ;

	Testimonial addEditTestimonial(AddEditTestimonialRequest request);

	Boolean discardTestimonial(String id, Boolean discarded);

	List<Testimonial> getTestimonialsByPlanId(String planId, int size, int page);

	List<NutritionPlanNameWithCategoryResponse> getNutritionPlanNameByCategory(NutritionPlanRequest request);

	Boolean putRemovePlanPrices(PlanPricesPutRemoveRequest request);

	NutritionPlan getNutritionPlanById(String id);

	public NutritionRDA addEditNutritionRDA(NutritionRDA request);

	Response<NutritionRDA> getNutritionRDA(String country, String countryId, String gender, String type, int page, int size, Boolean discarded);

	public NutritionRDA getNutritionRDAById(String id);

	public NutritionRDA discardNutritionRDA(String id, Boolean discarded);

	public Boolean uploadNutritionRDA(FormDataBodyPart file);
}
