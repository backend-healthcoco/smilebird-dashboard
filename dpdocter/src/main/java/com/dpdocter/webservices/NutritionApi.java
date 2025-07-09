package com.dpdocter.webservices;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.NutritionAppointment;
import com.dpdocter.beans.NutritionPlan;
import com.dpdocter.beans.NutritionRDA;
import com.dpdocter.beans.SubscriptionNutritionPlan;
import com.dpdocter.beans.Testimonial;
import com.dpdocter.beans.UserNutritionSubscription;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.NutritionPlanType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AddEditTestimonialRequest;
import com.dpdocter.request.NutritionPlanRequest;
import com.dpdocter.request.PlanPricesPutRemoveRequest;
import com.dpdocter.response.NutritionPlanNameWithCategoryResponse;
import com.dpdocter.response.NutritionPlanResponse;
import com.dpdocter.response.NutritionPlanWithCategoryResponse;
import com.dpdocter.response.UserNutritionSubscriptionResponse;
import com.dpdocter.services.NutritionService;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.NUTRITION_URL, produces = MediaType.APPLICATION_JSON,consumes = MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.NUTRITION_URL, description = "Endpoint for login")
public class NutritionApi {

	private static Logger logger = LogManager.getLogger(NutritionApi.class.getName());

	@Autowired
	private NutritionService nutritionService;

	@PostMapping(value = PathProxy.NutritionUrl.ADD_EDIT_NUTRITION_PLAN)
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_NUTRITION_PLAN, notes = PathProxy.NutritionUrl.ADD_EDIT_NUTRITION_PLAN)
	public Response<NutritionPlan> addEditPlan(@RequestBody NutritionPlan request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<NutritionPlan> response = new Response<NutritionPlan>();

		response.setData(nutritionService.addEditNutritionPlan(request));

		return response;
	}

	@GetMapping(PathProxy.NutritionUrl.GET_NUTRITION_PLAN_BY_ID)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_PLAN_BY_ID, notes = PathProxy.NutritionUrl.GET_NUTRITION_PLAN_BY_ID)
	public Response<NutritionPlanResponse> getPlanById(@PathVariable("id") String id) {

		Response<NutritionPlanResponse> response = null;

		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		response = new Response<NutritionPlanResponse>();
		response.setData(nutritionService.getNutritionPlan(id));

		return response;
	}

	@GetMapping(PathProxy.NutritionUrl.GET_NUTRITION_PLAN)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_PLAN, notes = PathProxy.NutritionUrl.GET_NUTRITION_PLAN)
	public @ResponseBody Response<NutritionPlan> getPlan(@RequestParam(required = false, value ="page", defaultValue = "0") int page, 
			@RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="type") String type, 
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0") long updatedTime,
			@RequestParam(required = false, value ="discarded", defaultValue = "true") boolean discarded) {

		Response<NutritionPlan> response = new Response<NutritionPlan>();
		response.setDataList(nutritionService.getNutritionPlans(page, size, type, updatedTime, discarded));

		return response;
	}

	@DeleteMapping(PathProxy.NutritionUrl.DELETE_NUTRITION_PLAN)
	@ApiOperation(value = PathProxy.NutritionUrl.DELETE_NUTRITION_PLAN, notes = PathProxy.NutritionUrl.DELETE_NUTRITION_PLAN)
	public Response<NutritionPlan> DeletePlan(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<NutritionPlan> response = new Response<NutritionPlan>();
		response.setData(nutritionService.deleteNutritionPlan(id));

		return response;
	}

	@GetMapping(PathProxy.NutritionUrl.GET_ALL_PLAN_CATEGORY)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_ALL_PLAN_CATEGORY, notes = PathProxy.NutritionUrl.GET_ALL_PLAN_CATEGORY)
	public Response<NutritionPlanType> getPlanCategory() {

		Response<NutritionPlanType> response = new Response<NutritionPlanType>();
		response.setDataList(nutritionService.getPlanType());

		return response;
	}

	@GetMapping(PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLAN_BY_ID)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLAN_BY_ID, notes = PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLAN_BY_ID)
	public Response<SubscriptionNutritionPlan> getSubscriptionPlan(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<SubscriptionNutritionPlan> response = new Response<SubscriptionNutritionPlan>();
		response.setData(nutritionService.getSubscritionPlan(id));

		return response;
	}

	@GetMapping(PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLANS)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLANS, notes = PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLANS)
	public Response<SubscriptionNutritionPlan> getSubscriptionPlans(@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value ="nutritionplanId") String nutritionplanId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true") boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(nutritionplanId)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<SubscriptionNutritionPlan> response = new Response<SubscriptionNutritionPlan>();
		response.setDataList(nutritionService.getSubscritionPlans(page, size, nutritionplanId, discarded));

		return response;
	}

	@DeleteMapping(PathProxy.NutritionUrl.DELETE_SUBSCRIPTION_PLAN)
	@ApiOperation(value = PathProxy.NutritionUrl.DELETE_SUBSCRIPTION_PLAN, notes = PathProxy.NutritionUrl.DELETE_SUBSCRIPTION_PLAN)
	public Response<SubscriptionNutritionPlan> DeleteSubscriptionPlan(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<SubscriptionNutritionPlan> response = new Response<SubscriptionNutritionPlan>();
		response.setData(nutritionService.deleteSubscritionPlan(id));

		return response;
	}

	@PostMapping(value = PathProxy.NutritionUrl.ADD_EDIT_SUBSCRIPTION_PLAN)
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_SUBSCRIPTION_PLAN, notes = PathProxy.NutritionUrl.ADD_EDIT_SUBSCRIPTION_PLAN)
	public Response<SubscriptionNutritionPlan> addEditPlan(@RequestBody SubscriptionNutritionPlan request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getNutritionPlanId())) {
			throw new BusinessException(ServiceError.InvalidInput, " nutritionPlanId must not be null");
		}
		Response<SubscriptionNutritionPlan> response = new Response<SubscriptionNutritionPlan>();
		response.setData(nutritionService.addEditSubscriptionPlan(request));
		return response;
	}

	@PostMapping(PathProxy.NutritionUrl.GET_NUTRITION_PLAN_CATEGORY)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_PLAN_CATEGORY, notes = PathProxy.NutritionUrl.GET_NUTRITION_PLAN_CATEGORY)
	public Response<NutritionPlanWithCategoryResponse> getPlanByCategory(@RequestBody NutritionPlanRequest request) {

		Response<NutritionPlanWithCategoryResponse> response = new Response<NutritionPlanWithCategoryResponse>();
		response.setDataList(nutritionService.getNutritionPlanByCategory(request));

		return response;
	}

	@GetMapping(PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTION)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTION, notes = PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTION)
	public Response<UserNutritionSubscription> getUserPlanSubscription(@PathVariable("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<UserNutritionSubscription> response = new Response<UserNutritionSubscription>();
		response.setData(nutritionService.getUserSubscritionPlan(id));
		return response;
	}

	@GetMapping(PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTIONS)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTIONS, notes = PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTIONS)
	public Response<UserNutritionSubscriptionResponse> getUserPlanSubscriptions(@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value ="updatedTime", defaultValue = "0") long updatedTime,
			@RequestParam(required = false, value ="discarded", defaultValue = "false") boolean discarded,
			@RequestParam(required = false, value ="isExpired", defaultValue = "false") boolean isExpired) {
		Response<UserNutritionSubscriptionResponse> response = new Response<UserNutritionSubscriptionResponse>();
		response.setDataList(nutritionService.getUserSubscritionPlans(page, size, updatedTime, discarded, isExpired));
		return response;
	}

	@GetMapping(PathProxy.NutritionUrl.GET_NUTRITION_APPOINTMENTS)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_APPOINTMENTS, notes = PathProxy.NutritionUrl.GET_NUTRITION_APPOINTMENTS)
	public Response<NutritionAppointment> getNutritionAppointments(@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value ="updatedTime", defaultValue = "0") long updatedTime,
			@RequestParam(required = false, value ="discarded", defaultValue = "false") boolean discarded) {
		Response<NutritionAppointment> response = new Response<NutritionAppointment>();
		response.setDataList(nutritionService.getNutritionAppointments(page, size, updatedTime, discarded));
		return response;
	}

	@PutMapping(PathProxy.NutritionUrl.UPDATE_NUTRITION_APPOINTMENT_STATUS)
	@ApiOperation(value = PathProxy.NutritionUrl.UPDATE_NUTRITION_APPOINTMENT_STATUS, notes = PathProxy.NutritionUrl.UPDATE_NUTRITION_APPOINTMENT_STATUS)
	public Response<NutritionAppointment> updateNutritionAppointmentStatus(
			@PathVariable("appointmentId") String appointmentId,
			@RequestParam(required = false, value ="status", defaultValue = "RESCHEDULE") AppointmentState status) {
		if (DPDoctorUtils.anyStringEmpty(appointmentId) || status == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<NutritionAppointment> response = new Response<NutritionAppointment>();
		response.setData(nutritionService.updateNutritionAppointmentStatus(appointmentId, status));
		return response;
	}
	
	@PostMapping(value = PathProxy.NutritionUrl.ADD_EDIT_TESTIMONIALS)
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_TESTIMONIALS, notes = PathProxy.NutritionUrl.ADD_EDIT_TESTIMONIALS)
	public Response<Testimonial> addEditTestimonials(@RequestBody AddEditTestimonialRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<Testimonial> response = new Response<Testimonial>();

		response.setData(nutritionService.addEditTestimonial(request));

		return response;
	}
	
	@DeleteMapping(value = PathProxy.NutritionUrl.DELETE_TESTIMONIALS)
	@ApiOperation(value = PathProxy.NutritionUrl.DELETE_TESTIMONIALS, notes = PathProxy.NutritionUrl.DELETE_TESTIMONIALS)
	public Response<Boolean> deleteTestimonials(@PathVariable("id") String id , @RequestParam(required = false, value ="discarded", defaultValue = "false") Boolean discarded) {
		if (id == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<Boolean> response = new Response<Boolean>();

		response.setData(nutritionService.discardTestimonial(id, discarded));

		return response;
	}
	
	@GetMapping(value = PathProxy.NutritionUrl.GET_TESTIMONIALS_BY_PLAN_ID)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_TESTIMONIALS_BY_PLAN_ID, notes = PathProxy.NutritionUrl.GET_TESTIMONIALS_BY_PLAN_ID)
	public Response<Testimonial> getTestimonialsByPlanId(@PathVariable("id") String planId , @RequestParam(required = false, value ="page", defaultValue = "0") int page ,  @RequestParam(required = false, value ="size", defaultValue = "0") int size ) {
		if (planId == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<Testimonial> response = new Response<Testimonial>();

		response.setDataList(nutritionService.getTestimonialsByPlanId(planId, size, page));

		return response;
	}


	@PostMapping(PathProxy.NutritionUrl.GET_NUTRITION_PLAN_NAME_CATEGORY)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_PLAN_NAME_CATEGORY, notes = PathProxy.NutritionUrl.GET_NUTRITION_PLAN_NAME_CATEGORY)
	public Response<NutritionPlanNameWithCategoryResponse> getPlanNameByCategory(@RequestBody NutritionPlanRequest request) {

		Response<NutritionPlanNameWithCategoryResponse> response = new Response<NutritionPlanNameWithCategoryResponse>();
		response.setDataList(nutritionService.getNutritionPlanNameByCategory(request));

		return response;
	}
	
	@PostMapping(PathProxy.NutritionUrl.PUT_REMOVE_PLAN_PRICES)
	@ApiOperation(value = PathProxy.NutritionUrl.PUT_REMOVE_PLAN_PRICES, notes = PathProxy.NutritionUrl.PUT_REMOVE_PLAN_PRICES)
	public Response<Boolean> putRemovePlanPrices(@RequestBody PlanPricesPutRemoveRequest request) {

		Response<Boolean> response = new Response<Boolean>();
		response.setData(nutritionService.putRemovePlanPrices(request));

		return response;
	}

	
	@GetMapping(PathProxy.NutritionUrl.GET_USER_NUTRITION_PLAN_BY_ID)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_USER_NUTRITION_PLAN_BY_ID, notes = PathProxy.NutritionUrl.GET_USER_NUTRITION_PLAN_BY_ID)
	public Response<NutritionPlan> getNutritionPlanById(@PathVariable("id") String id) {
		Response<NutritionPlan> response = null;
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		response = new Response<NutritionPlan>();
		response.setData(nutritionService.getNutritionPlanById(id));

		return response;
	}
	
	@PostMapping(value = PathProxy.NutritionUrl.ADD_EDIT_NUTRITION_RDA)
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_NUTRITION_RDA, notes = PathProxy.NutritionUrl.ADD_EDIT_NUTRITION_RDA)
	public Response<NutritionRDA> addEditNutritionRDA(@RequestBody NutritionRDA request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<NutritionRDA> response = new Response<NutritionRDA>();
		response.setData(nutritionService.addEditNutritionRDA(request));

		return response;
	}
	
	@GetMapping(value = PathProxy.NutritionUrl.GET_NUTRITION_RDA_BY_ID)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_RDA_BY_ID, notes = PathProxy.NutritionUrl.GET_NUTRITION_RDA_BY_ID)
	public Response<NutritionRDA> getNutritionRDA(@PathVariable("id") String id) {

		Response<NutritionRDA> response = new Response<NutritionRDA>();

		response.setData(nutritionService.getNutritionRDAById(id));

		return response;
	}
	
	@GetMapping(value = PathProxy.NutritionUrl.GET_NUTRITION_RDA)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_RDA, notes = PathProxy.NutritionUrl.GET_NUTRITION_RDA)
	public Response<NutritionRDA> getNutritionRDA(@RequestParam(required = false, value ="country") String country,
			@RequestParam(required = false, value ="countryId") String countryId,
			@RequestParam(required = false, value ="gender") String gender,
			@RequestParam(required = false, value ="type") String type,
			@RequestParam(required = false, value ="page", defaultValue = "0") int page, 
			@RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="discarded", defaultValue = "false")  Boolean discarded) {

		Response<NutritionRDA> response = nutritionService.getNutritionRDA(country, countryId, gender, type, page, size, discarded);

		return response;
	}
	
	@DeleteMapping(value = PathProxy.NutritionUrl.DELETE_NUTRITION_RDA)
	@ApiOperation(value = PathProxy.NutritionUrl.DELETE_NUTRITION_RDA, notes = PathProxy.NutritionUrl.DELETE_NUTRITION_RDA)
	public Response<NutritionRDA> discardRecipeNutrientType(@PathVariable("id") String id,
			@RequestParam(required = false, value ="discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<NutritionRDA> response = new Response<NutritionRDA>();
		response.setData(nutritionService.discardNutritionRDA(id, discarded));
		return response;
	}
	
	@PostMapping(value = PathProxy.NutritionUrl.UPLOAD_NUTRITION_RDA, consumes = { MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.NutritionUrl.UPLOAD_NUTRITION_RDA, notes = PathProxy.NutritionUrl.UPLOAD_NUTRITION_RDA)
	public Response<Boolean> uploadNutritionRDA(@FormDataParam("file") FormDataBodyPart file) {
		if(file == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(nutritionService.uploadNutritionRDA(file));
		return response;
	}

}
