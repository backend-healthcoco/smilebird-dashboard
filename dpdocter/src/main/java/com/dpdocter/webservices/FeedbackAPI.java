package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.DoctorAppFeedback;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.PatientFeedbackResponse;
import com.dpdocter.services.FeedbackService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.FEEDBACK_BASE_URL, produces = MediaType.APPLICATION_JSON , consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.FEEDBACK_BASE_URL, description = "Endpoint for FEEDBACK")
public class FeedbackAPI {

	@Autowired
	FeedbackService feedbackService;

	@PostMapping(PathProxy.FeedbackUrls.SAVE_DOCTOR_APP_FEEDBACK)
	@POST
	public Response<DoctorAppFeedback> saveDoctorAppFeedback(DoctorAppFeedback doctorAppFeedback) {
		DoctorAppFeedback feedback = null;
		Response<DoctorAppFeedback> response = new Response<>();
		try {
			if (doctorAppFeedback == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
			}
			feedback = feedbackService.saveDoctorAppFeedback(doctorAppFeedback);
			response.setData(feedback);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping(PathProxy.FeedbackUrls.GET_PATIENT_FEEDBACKS)
	@ApiOperation(value = PathProxy.FeedbackUrls.GET_PATIENT_FEEDBACKS, notes = PathProxy.FeedbackUrls.GET_PATIENT_FEEDBACKS)
	public Response<PatientFeedbackResponse> getPatientFeedback(@MatrixParam("services") List<String> services,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="patientId") String patientId,
			@RequestParam(required = false, value ="doctorId") String doctorId, @RequestParam(required = false, value ="localeId") String localeId,
			@RequestParam(required = false, value ="locationId") String locationId, @RequestParam(required = false, value ="hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded") Boolean discarded, @RequestParam(required = false, value ="isApproved") Boolean isApproved,
			@RequestParam(required = false, value ="type") String type) {
		Response<PatientFeedbackResponse> response = new Response<>();
		List<PatientFeedbackResponse> feedbacks = null;
		try {
			Integer count = feedbackService.countPatientFeedbackList(patientId, doctorId, localeId, locationId,
					hospitalId, type, services, discarded, isApproved);
			if (count > 0)
				feedbacks = feedbackService.getPatientFeedbackList(size, page, patientId, doctorId, localeId,
						locationId, hospitalId, type, services, discarded, isApproved);
			response.setDataList(feedbacks);
			response.setCount(count);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	@GetMapping(PathProxy.FeedbackUrls.APPROVE_PATIENT_FEEDBACKS)
	@ApiOperation(value = PathProxy.FeedbackUrls.APPROVE_PATIENT_FEEDBACKS, notes = PathProxy.FeedbackUrls.APPROVE_PATIENT_FEEDBACKS)
	public Response<Boolean> approvedPatientFeedback(@PathVariable("id") String id,
			@RequestParam(required = false, value ="isApproved") Boolean isApproved) {
		Response<Boolean> response = new Response<Boolean>();
		try {
			if (DPDoctorUtils.anyStringEmpty(id)) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
			}

			response.setData(feedbackService.approvePatientFeedback(id, isApproved));

		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

}
