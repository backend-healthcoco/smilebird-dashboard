package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.ConsultationProblemDetails;
import com.dpdocter.request.ConsultationProblemDetailsRequest;

public interface ConsultationProblemDetailsService {

	ConsultationProblemDetails addEditProblemDetails(ConsultationProblemDetailsRequest request);

	 List<ConsultationProblemDetails> getProblemDetails(int size, int page, Boolean discarded,
			 String searchTerm,String doctorId,	String problemDetailsId, String userId);

	Integer countConsultationProblemDetails(Boolean discarded, String searchTerm, String doctorId,
			String problemDetailsId, String userId);

}
