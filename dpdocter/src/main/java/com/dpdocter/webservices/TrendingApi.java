package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.Trending;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.TrendingResponse;
import com.dpdocter.services.TrendingService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.TRENDING_URL, produces = MediaType.APPLICATION_JSON ,  consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.TRENDING_URL, description = "Endpoint for trending")
public class TrendingApi {
	private static Logger logger = LogManager.getLogger(TrendingApi.class.getName());

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private TrendingService trendingService;

	@PostMapping(value = PathProxy.TrendingUrls.ADD_EDIT_TRENDING)
	@ApiOperation(value = PathProxy.TrendingUrls.ADD_EDIT_TRENDING, notes = PathProxy.TrendingUrls.ADD_EDIT_TRENDING)
	public Response<TrendingResponse> addEditTrending(@RequestBody Trending request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		if (DPDoctorUtils.anyStringEmpty(request.getBlogId()) && DPDoctorUtils.anyStringEmpty(request.getOfferId())) {
			logger.warn("BlogId or OfferId should not be null or empty");
			throw new BusinessException(ServiceError.InvalidInput, "BlogId or OfferId should not be null or empty.");

		}
		TrendingResponse trending = trendingService.addEditTrending(request);
		Response<TrendingResponse> response = new Response<TrendingResponse>();

		response.setData(trending);
		return response;
	}

	@DeleteMapping(value = PathProxy.TrendingUrls.DELETE_TRENDING)
	@ApiOperation(value = PathProxy.TrendingUrls.DELETE_TRENDING, notes = PathProxy.TrendingUrls.DELETE_TRENDING)
	public Response<TrendingResponse> deleteTrending(@PathVariable("id") String id,
			@RequestParam(required = false, value ="discarded", defaultValue = "true") Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<TrendingResponse> response = new Response<TrendingResponse>();
		TrendingResponse trending = trendingService.deleteTrending(id, discarded);

		response.setData(trending);
		return response;
	}

	@GetMapping(value = PathProxy.TrendingUrls.GET_TRENDING)
	@ApiOperation(value = PathProxy.TrendingUrls.GET_TRENDING, notes = PathProxy.TrendingUrls.GET_TRENDING)
	public Response<TrendingResponse> getTrending(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<TrendingResponse> response = new Response<TrendingResponse>();

		TrendingResponse trending = trendingService.getTrending(id);
		response.setData(trending);
		return response;
	}

	@GetMapping(value = PathProxy.TrendingUrls.GET_TRENDINGS)
	@ApiOperation(value = PathProxy.TrendingUrls.GET_TRENDINGS, notes = PathProxy.TrendingUrls.GET_TRENDINGS)
	public Response<TrendingResponse> getTrendings(@RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="discarded") Boolean discarded, @RequestParam(required = false, value ="searchTerm") String searchTerm,
			@RequestParam(required = false, value ="trendingType") String trendingType, @RequestParam(required = false, value ="resourceType") String resourceType,@RequestParam(required = false, value ="toTime") String toTime,@RequestParam(required = false, value ="fromTime") String fromTime) {

		Response<TrendingResponse> response = new Response<TrendingResponse>();
		List<TrendingResponse> trendings = trendingService.getTrendings(size, page, discarded, searchTerm, trendingType,
				resourceType,toTime,fromTime);
		response.setDataList(trendings);
		return response;
	}

	@GetMapping(value = PathProxy.TrendingUrls.COUNT_TRENDING)
	@ApiOperation(value = PathProxy.TrendingUrls.COUNT_TRENDING, notes = PathProxy.TrendingUrls.COUNT_TRENDING)
	public Response<Integer> countTrendings(@RequestParam(required = false, value ="discarded") Boolean discarded,
			@RequestParam(required = false, value ="searchTerm") String searchTerm, @RequestParam(required = false, value ="trendingType") String trendingType,
			@RequestParam(required = false, value ="resourceType") String resourceType) {

		Response<Integer> response = new Response<Integer>();
		Integer count = trendingService.countTrendings(discarded, searchTerm, trendingType, resourceType);
		response.setData(count);
		return response;
	}

	@GetMapping(value = PathProxy.TrendingUrls.UPDATE_RANK)
	@ApiOperation(value = PathProxy.TrendingUrls.UPDATE_RANK, notes = PathProxy.TrendingUrls.UPDATE_RANK)
	public Response<TrendingResponse> updateRank(@PathVariable("id") String id, @RequestParam(required = false, value ="rank") int rank) {

		Response<TrendingResponse> response = new Response<TrendingResponse>();
		TrendingResponse trnding = trendingService.updateRank(id, rank);
		response.setData(trnding);
		return response;
	}

}
