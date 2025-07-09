package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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

import com.dpdocter.beans.PrintSettings;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.PrintSettingsService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping(value=PathProxy.PRINT_SETTINGS_BASE_URL, produces = MediaType.APPLICATION_JSON ,  consumes = MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PRINT_SETTINGS_BASE_URL, description = "Endpoint for print settings")
public class PrintSettingsApi {

	private static Logger logger = LogManager.getLogger(PrintSettingsApi.class.getName());

	@Autowired
	private PrintSettingsService printSettingsService;

	@Value(value = "${image.path}")
	private String imagePath;


	@PostMapping(value = PathProxy.PrintSettingsUrls.SAVE_PRINT_SETTINGS)
	@ApiOperation(value = "SAVE_PRINT_SETTINGS", notes = "SAVE_PRINT_SETTINGS")
	public Response<PrintSettings> saveSettings(@RequestBody PrintSettings request) {

		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		PrintSettings printSettings = printSettingsService.saveSettings(request);
		if (printSettings != null)
			printSettings.setClinicLogoUrl(getFinalImageURL(printSettings.getClinicLogoUrl()));
		Response<PrintSettings> response = new Response<PrintSettings>();
		response.setData(printSettings);
		return response;
	}

	@GetMapping(value = PathProxy.PrintSettingsUrls.GET_PRINT_SETTINGS)
	@ApiOperation(value = "GET_PRINT_SETTINGS", notes = "GET_PRINT_SETTINGS")
	public Response<PrintSettings> getSettings(@PathVariable(value = "printFilter") String printFilter,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId, @RequestParam(required = false, value = "page", defaultValue="0") int page,
			@RequestParam(required = false, value = "size", defaultValue="0") int size,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(printFilter, doctorId, locationId, hospitalId)) {
			logger.warn("PrintFilter, DoctorId or locationId or hospitalId cannot be null");
			throw new BusinessException(ServiceError.InvalidInput,
					"PrintFilter, DoctorId or locationId or hospitalId cannot be null");
		}
		List<PrintSettings> printSettings = printSettingsService.getSettings(printFilter, doctorId, locationId,
				hospitalId, page, size, updatedTime, discarded);
		if (printSettings != null) {
			for (Object pSettings : printSettings) {
				((PrintSettings) pSettings)
						.setClinicLogoUrl(getFinalImageURL(((PrintSettings) pSettings).getClinicLogoUrl()));
			}
		}
		Response<PrintSettings> response = new Response<PrintSettings>();
		response.setDataList(printSettings);
		return response;
	}

	@DeleteMapping(value = PathProxy.PrintSettingsUrls.DELETE_PRINT_SETTINGS)
	@ApiOperation(value = "DELETE_PRINT_SETTINGS", notes = "DELETE_PRINT_SETTINGS")
	public Response<PrintSettings> deletePrintSettings(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(id, doctorId, locationId, hospitalId)) {
			logger.warn("Id, DoctorId or locationId or hospitalId cannot be null");
			throw new BusinessException(ServiceError.InvalidInput,
					"Id, DoctorId or locationId or hospitalId cannot be null");
		}
		PrintSettings printSettings = printSettingsService.deletePrintSettings(id, doctorId, locationId, hospitalId,
				discarded);
		Response<PrintSettings> response = new Response<PrintSettings>();
		response.setData(printSettings);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;

	}

	@GetMapping(value = PathProxy.PrintSettingsUrls.ENBLE_SHOW_POWERED_BY)
	@ApiOperation(value = "ENBLE_SHOW_POWERED_BY", notes = "ENBLE_SHOW_POWERED_BY")
	public Response<Boolean> enablePoweredBy(@PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "showPoweredBy", defaultValue = "true") Boolean showPoweredBy) {

		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn(" DoctorId or locationId or hospitalId cannot be null");
			throw new BusinessException(ServiceError.InvalidInput,
					"PrintFilter, DoctorId or locationId or hospitalId cannot be null");
		}
		Boolean status = printSettingsService.enablePoweredBy(doctorId, locationId, hospitalId, showPoweredBy);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

}
