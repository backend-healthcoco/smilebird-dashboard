package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.DrugInfo;
import com.dpdocter.beans.MedicineOrder;
import com.dpdocter.beans.MedicineOrderRxImageRequest;
import com.dpdocter.beans.TrackingOrder;
import com.dpdocter.beans.Vendor;
import com.dpdocter.enums.LabType;
import com.dpdocter.enums.OrderStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AddEditVendorRequest;
import com.dpdocter.request.EditDeliveryBoyRequest;
import com.dpdocter.request.MedicineOrderAddEditAddressRequest;
import com.dpdocter.request.MedicineOrderAssignDeliveryBoyRequest;
import com.dpdocter.request.MedicineOrderPaymentAddEditRequest;
import com.dpdocter.request.MedicineOrderPreferenceAddEditRequest;
import com.dpdocter.request.MedicineOrderRXAddEditRequest;

import com.dpdocter.request.MedicineOrderVendorAssignRequest;
import com.dpdocter.response.CollectionBoyResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.MedicineOrderService;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping(value=PathProxy.ORDER_MEDICINE_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ORDER_MEDICINE_BASE_URL, description = "Endpoint for records")
public class MedicineOrderAPI {
	
	@Autowired
	private MedicineOrderService medicineOrderService;

	@PostMapping(value = PathProxy.OrderMedicineUrls.UPLOAD_IMAGE, consumes = { MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.OrderMedicineUrls.UPLOAD_IMAGE, notes = PathProxy.OrderMedicineUrls.UPLOAD_IMAGE)
	public Response<ImageURLResponse> saveMedicine(@RequestParam("file") MultipartFile file) {
		
		ImageURLResponse imageURL = medicineOrderService.uploadImage(file);
		//imageURL = getFinalImageURL(imageURL);
		Response<ImageURLResponse> response = new Response<ImageURLResponse>();
		response.setData(imageURL);
		return response;
	}
	
	@PostMapping(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_RX)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_RX, notes = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_RX)
	public Response<MedicineOrder> addEditRX(@RequestBody MedicineOrderRXAddEditRequest request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		MedicineOrder medicineOrder = medicineOrderService.addeditRx(request);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setData(medicineOrder);
		return response;
	}
	
	@PostMapping(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_RX_IMAGE)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_RX_IMAGE, notes = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_RX_IMAGE)
	public Response<MedicineOrder> addEditRXImage(@RequestBody MedicineOrderRxImageRequest request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		MedicineOrder medicineOrder = medicineOrderService.addeditRxImage(request);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setData(medicineOrder);
		return response;
	}
	
	@PostMapping(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_ADDRESS)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_ADDRESS, notes = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_ADDRESS)
	public Response<MedicineOrder> addEditAddress(@RequestBody MedicineOrderAddEditAddressRequest request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		MedicineOrder medicineOrder = medicineOrderService.addeditAddress(request);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setData(medicineOrder);
		return response;
	}
	
	@PostMapping(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_PREFERENCE)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_PREFERENCE, notes = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_PREFERENCE)
	public Response<MedicineOrder> addEditPreference(@RequestBody MedicineOrderPreferenceAddEditRequest request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		MedicineOrder medicineOrder = medicineOrderService.addeditPreferences(request);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setData(medicineOrder);
		return response;
	}
	
	@PostMapping(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_PAYMENT)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_PAYMENT, notes = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_PAYMENT)
	public Response<MedicineOrder> addEditPayment(@RequestBody MedicineOrderPaymentAddEditRequest request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		MedicineOrder medicineOrder = medicineOrderService.addeditPayment(request);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setData(medicineOrder);
		return response;
	}
	
	@GetMapping(value = PathProxy.OrderMedicineUrls.GET_BY_ID)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.GET_BY_ID, notes = PathProxy.OrderMedicineUrls.GET_BY_ID)
	public Response<MedicineOrder> getOrderById(@PathVariable("id") String id) {

		if(DPDoctorUtils.allStringsEmpty(id))
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input. Id cannot be null");
		}
		
		MedicineOrder medicineOrder = medicineOrderService.getOrderById(id);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setData(medicineOrder);
		return response;
	}
	
	@GetMapping(value = PathProxy.OrderMedicineUrls.UPDATE_STATUS)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.UPDATE_STATUS, notes = PathProxy.OrderMedicineUrls.UPDATE_STATUS)
	public Response<MedicineOrder> updateStatus(@PathVariable("id") String id , @RequestParam(required = false, value ="status") OrderStatus status ) {

		if(DPDoctorUtils.allStringsEmpty(id))
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input. Id cannot be null");
		}
		
		MedicineOrder medicineOrder = medicineOrderService.updateStatus(id, status);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setData(medicineOrder);
		return response;
	}
	
	@GetMapping(value = PathProxy.OrderMedicineUrls.DISCARD_MEDICINE_ORDER)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.DISCARD_MEDICINE_ORDER, notes = PathProxy.OrderMedicineUrls.DISCARD_MEDICINE_ORDER)
	public Response<Boolean> discardOrder(@PathVariable("id") String id , @RequestParam(required = false, value ="discarded") Boolean discarded ) {

		if(DPDoctorUtils.allStringsEmpty(id))
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input. Id cannot be null");
		}
		
		Boolean status = medicineOrderService.discardOrder(id, discarded);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}
	
	@GetMapping(value = PathProxy.OrderMedicineUrls.PATIENT_ORDER_GET_LIST)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.PATIENT_ORDER_GET_LIST, notes = PathProxy.OrderMedicineUrls.PATIENT_ORDER_GET_LIST)
	public Response<MedicineOrder> getPatientOrderList(@PathVariable("patientId") String patientId ,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0") String updatedTime ,  @RequestParam(required = false, value ="searchTerm") String searchTerm , 
			@RequestParam(required = false, value ="page", defaultValue = "0") int page ,  @RequestParam(required = false, value ="size", defaultValue = "0") int size ,@MatrixParam("status") List<String> status , @RequestParam(required = false, value ="isImageAvailable") Boolean isImageAvailable, @RequestParam(required = false, value ="isRXAvailable") Boolean isRXAvailable , @RequestParam(required = false, value ="discarded") Boolean discarded) {

		if(DPDoctorUtils.allStringsEmpty(patientId))
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input. Id cannot be null");
		}
		
		List<MedicineOrder> medicineOrders = medicineOrderService.getOrderListByPatient(patientId, updatedTime, searchTerm, page, size, status, isImageAvailable, isRXAvailable , discarded);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setDataList(medicineOrders);
		return response;
	}
	
	@GetMapping(value = PathProxy.OrderMedicineUrls.VENDOR_ORDER_GET_LIST)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.VENDOR_ORDER_GET_LIST, notes = PathProxy.OrderMedicineUrls.VENDOR_ORDER_GET_LIST)
	public Response<MedicineOrder> getVendorOrderList(@PathVariable("vendorId") String vendorId,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0") String updatedTime ,  @RequestParam(required = false, value ="searchTerm") String searchTerm , 
			@RequestParam(required = false, value ="page", defaultValue = "0") int page ,  @RequestParam(required = false, value ="size", defaultValue = "0") int size ) {

		if(DPDoctorUtils.allStringsEmpty(vendorId))
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input. Id cannot be null");
		}
		
		List<MedicineOrder> medicineOrders = medicineOrderService.getOrderListByVendor(vendorId, updatedTime, searchTerm, page, size);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setDataList(medicineOrders);
		return response;
	}
	
	@GetMapping(value = PathProxy.OrderMedicineUrls.ORDER_GET_LIST)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.ORDER_GET_LIST, notes = PathProxy.OrderMedicineUrls.ORDER_GET_LIST)
	public Response<MedicineOrder> getOrderList(@RequestParam(required = false, value ="updatedTime", defaultValue = "0") String updatedTime ,  
			@RequestParam(required = false, value ="searchTerm") String searchTerm , 
			@RequestParam(required = false, value ="page", defaultValue = "0") int page ,  
			@RequestParam(required = false, value ="size", defaultValue = "0") int size , 
			@RequestParam(required = false, value ="discarded") Boolean discarded , 
			@RequestParam(required = false, value ="from", defaultValue = "0") Long from,
			@RequestParam(required = false, value ="to", defaultValue = "0") Long to,
			@RequestParam(required = false, value ="isImageAvailable") Boolean isImageAvailable, 
			@RequestParam(required = false, value ="isRXAvailable") Boolean isRXAvailable,
			@RequestParam(required = false, value ="orderStatus") String orderStatus) {
		
		List<MedicineOrder> medicineOrders = medicineOrderService.getOrderList(updatedTime, searchTerm, page, size, discarded, from, to, isImageAvailable, isRXAvailable,orderStatus);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setDataList(medicineOrders);
		response.setCount(medicineOrderService.getOrderListCount(updatedTime, searchTerm, discarded, from, to));
		return response;
	}
	
	@PostMapping(value = PathProxy.OrderMedicineUrls.ADD_EDIT_VENDOR)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.ADD_EDIT_VENDOR, notes = PathProxy.OrderMedicineUrls.ADD_EDIT_VENDOR)
	public Response<Vendor> addEditVendor(@RequestBody AddEditVendorRequest request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		Vendor vendor = medicineOrderService.addEditVendor(request);
		Response<Vendor> response = new Response<Vendor>();
		response.setData(vendor);
		return response;
	}
	
	@GetMapping(value = PathProxy.OrderMedicineUrls.GET_VENDOR_BY_ID)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.GET_VENDOR_BY_ID, notes = PathProxy.OrderMedicineUrls.GET_VENDOR_BY_ID)
	public Response<Vendor> getVendorById(@PathVariable("id") String id) {

		if(DPDoctorUtils.allStringsEmpty(id))
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input. Id cannot be null");
		}
		
		Vendor vendor = medicineOrderService.getVendorById(id);
		Response<Vendor> response = new Response<Vendor>();
		response.setData(vendor);
		return response;
	}
	
	@GetMapping(value = PathProxy.OrderMedicineUrls.DISCARD_VENDOR)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.DISCARD_VENDOR, notes = PathProxy.OrderMedicineUrls.DISCARD_VENDOR)
	public Response<Vendor> discardVendor(@PathVariable("id") String id , @RequestParam(required = false, value ="discarded") Boolean discarded ) {

		if(DPDoctorUtils.allStringsEmpty(id))
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input. Id cannot be null");
		}
		
		Vendor status = medicineOrderService.discardVendor(id, discarded);
		Response<Vendor> response = new Response<Vendor>();
		response.setData(status);
		return response;
	}
	
	@GetMapping(value = PathProxy.OrderMedicineUrls.GET_VENDORS)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.GET_VENDORS, notes = PathProxy.OrderMedicineUrls.GET_VENDORS)
	public Response<Vendor> getVendors(@RequestParam(required = false, value ="updatedTime", defaultValue = "0") String updatedTime ,  @RequestParam(required = false, value ="searchTerm") String searchTerm , 
			@RequestParam(required = false, value ="page", defaultValue = "0") int page ,  @RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value ="discarded") Boolean discarded ) {

		List<Vendor> medicineOrders = medicineOrderService.getVendorList( updatedTime, searchTerm, page, size, discarded);
		Response<Vendor> response = new Response<Vendor>();
		response.setDataList(medicineOrders);
		response.setCount(medicineOrderService.getVendorListCount(updatedTime, searchTerm, discarded));
		return response;
	}
	
	@PostMapping(value = PathProxy.OrderMedicineUrls.ASSIGN_VENDOR)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.ASSIGN_VENDOR, notes = PathProxy.OrderMedicineUrls.ASSIGN_VENDOR)
	public Response<MedicineOrder> assignVendorToOrder(@RequestBody MedicineOrderVendorAssignRequest request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		MedicineOrder medicineOrder = medicineOrderService.assignVendorToOrder(request);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setData(medicineOrder);
		return response;
	}
	
	@GetMapping(value = PathProxy.OrderMedicineUrls.GET_DELIVERY_BOY_LIST)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.GET_DELIVERY_BOY_LIST, notes = PathProxy.OrderMedicineUrls.GET_DELIVERY_BOY_LIST)
	public Response<CollectionBoyResponse> getDeliveryBoyList(@RequestParam(required = false, value ="updatedTime", defaultValue = "0") String updatedTime ,  @RequestParam(required = false, value ="searchTerm") String searchTerm , 
			@RequestParam(required = false, value ="page", defaultValue = "0") int page ,  @RequestParam(required = false, value ="size", defaultValue = "0") int size , @RequestParam(required = false, value ="discarded") Boolean discarded) {
		
		List<CollectionBoyResponse> collectionBoys = medicineOrderService.getCollectionBoyList(size, page, null, searchTerm, LabType.MEDICINE_ORDER.getType(), discarded);
		Response<CollectionBoyResponse> response = new Response<CollectionBoyResponse>();
		response.setDataList(collectionBoys);
		response.setCount(medicineOrderService.getCollectionBoyListCount(null, searchTerm, LabType.MEDICINE_ORDER.getType(), discarded));
		return response;
	}
	
	@GetMapping(value = PathProxy.OrderMedicineUrls.GET_DELIVERY_BOY_BY_ID)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.GET_DELIVERY_BOY_BY_ID, notes = PathProxy.OrderMedicineUrls.GET_DELIVERY_BOY_BY_ID)
	public Response<CollectionBoyResponse> getCollectionBoyById(@PathVariable("id") String id) {

		if(DPDoctorUtils.allStringsEmpty(id))
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input. Id cannot be null");
		}
		
		CollectionBoyResponse collectionBoyResponse = medicineOrderService.getCollectionBoyById(id);
		Response<CollectionBoyResponse> response = new Response<CollectionBoyResponse>();
		response.setData(collectionBoyResponse);
		return response;
	}
	
	@DeleteMapping(value = PathProxy.OrderMedicineUrls.DISCARD_DELIVERY_BOY)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.DISCARD_DELIVERY_BOY, notes = PathProxy.OrderMedicineUrls.DISCARD_DELIVERY_BOY)
	public Response<CollectionBoy> discardDeliveryBoy(@PathVariable("id") String id, @RequestParam(required = false, value ="discarded") Boolean discarded) {

		if(DPDoctorUtils.allStringsEmpty(id))
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input. Id cannot be null");
		}
		
		CollectionBoy collectionBoyResponse = medicineOrderService.discardCollectionBoy(id, discarded);
		Response<CollectionBoy> response = new Response<CollectionBoy>();
		response.setData(collectionBoyResponse);
		return response;
	}
	
	@PostMapping(value = PathProxy.OrderMedicineUrls.ADD_EDIT_TRACKING_DETAILS)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.ADD_EDIT_TRACKING_DETAILS, notes = PathProxy.OrderMedicineUrls.ADD_EDIT_TRACKING_DETAILS)
	public Response<TrackingOrder> addEditTrackingDetails(@RequestBody TrackingOrder request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		TrackingOrder trackingOrder = medicineOrderService.addeditTrackingDetails(request);
		Response<TrackingOrder> response = new Response<TrackingOrder>();
		response.setData(trackingOrder);
		return response;
	}

	@GetMapping(value = PathProxy.OrderMedicineUrls.GET_TRACKING_DETAILS)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.GET_TRACKING_DETAILS, notes = PathProxy.OrderMedicineUrls.GET_TRACKING_DETAILS)
	public Response<TrackingOrder> getTrackingDetailsByOrder(@PathVariable("orderId") String orderId , @RequestParam(required = false, value ="page", defaultValue = "0") int page , @RequestParam(required = false, value ="size", defaultValue = "0") int size , 
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0") String updatedTime,@RequestParam(required = false, value ="searchTerm") String searchTerm) {

		if(orderId == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		List<TrackingOrder> trackingOrders = medicineOrderService.getTrackingList(orderId, updatedTime, searchTerm, page, size);
		Response<TrackingOrder> response = new Response<TrackingOrder>();
		response.setDataList(trackingOrders);
		return response;
	}
	
	@PostMapping(value = PathProxy.OrderMedicineUrls.ASSIGN_DELIVERY_BOY)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.ASSIGN_DELIVERY_BOY, notes = PathProxy.OrderMedicineUrls.ASSIGN_DELIVERY_BOY)
	public Response<MedicineOrder> assignDeliveryBoyToOrder(@RequestBody MedicineOrderAssignDeliveryBoyRequest request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		MedicineOrder medicineOrder = medicineOrderService.assignDeliveryBoyToOrder(request);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setData(medicineOrder);
		return response;
	}
	
	@PutMapping(value = PathProxy.OrderMedicineUrls.EDIT_DELIVERY_BOY)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.EDIT_DELIVERY_BOY, notes = PathProxy.OrderMedicineUrls.EDIT_DELIVERY_BOY)
	public Response<CollectionBoyResponse> editDeliveryBoy(@RequestBody EditDeliveryBoyRequest request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		CollectionBoyResponse collectionBoyResponse = medicineOrderService.editCollectionBoy(request);
		Response<CollectionBoyResponse> response = new Response<CollectionBoyResponse>();
		response.setData(collectionBoyResponse);
		return response;
	}
	
	@GetMapping(value = PathProxy.OrderMedicineUrls.GET_DRUG_INFO_LIST)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.GET_DRUG_INFO_LIST, notes = PathProxy.OrderMedicineUrls.GET_DRUG_INFO_LIST)
	public Response<DrugInfo> getGetDrugInfoList(@RequestParam(required = false, value ="page", defaultValue = "0") int page , @RequestParam(required = false, value ="size", defaultValue = "0") int size , 
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0") String updatedTime,@RequestParam(required = false, value ="searchTerm") String searchTerm,@RequestParam(required = false, value ="discarded") Boolean discarded) {
		
		List<DrugInfo> drugInfos = medicineOrderService.getDrugInfo(page, size, updatedTime, searchTerm, discarded);
		Response<DrugInfo> response = new Response<DrugInfo>();
		response.setDataList(drugInfos);
		return response;
	}
	
	
}
