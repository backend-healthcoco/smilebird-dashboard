package com.dpdocter.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.CollectionBoyCautionMoney;
import com.dpdocter.beans.CollectionBoyCautionMoneyHistory;
import com.dpdocter.beans.DrugInfo;
import com.dpdocter.beans.MedicineOrder;
import com.dpdocter.beans.MedicineOrderRxImageRequest;
import com.dpdocter.beans.TrackingOrder;
import com.dpdocter.beans.Vendor;
import com.dpdocter.enums.OrderStatus;
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
import com.sun.jersey.multipart.FormDataBodyPart;

public interface MedicineOrderService {

	//ImageURLResponse saveRXMedicineOrderImage(FormDataBodyPart file, String patientIdString);

	MedicineOrder addeditRx(MedicineOrderRXAddEditRequest request);

	MedicineOrder addeditAddress(MedicineOrderAddEditAddressRequest request);

	MedicineOrder addeditPayment(MedicineOrderPaymentAddEditRequest request);

	MedicineOrder addeditPreferences(MedicineOrderPreferenceAddEditRequest request);

	MedicineOrder updateStatus(String id, OrderStatus status);

	Boolean discardOrder(String id, Boolean discarded);

	MedicineOrder getOrderById(String id);

	//List<MedicineOrder> getOrderList(String updatedTime, String searchTerm, int page, int size);

	Vendor addEditVendor(AddEditVendorRequest request);

	Vendor getVendorById(String id);

	Vendor discardVendor(String id, Boolean discarded);

	List<MedicineOrder> getOrderListByVendor(String vendorId, String updatedTime, String searchTerm, int page,
			int size);

	/*List<MedicineOrder> getOrderListByPatient(String patientId, String updatedTime, String searchTerm, int page,
			int size);*/

	//List<Vendor> getVendorList(String updatedTime, String searchTerm, int page, int size);

	MedicineOrder assignVendorToOrder(MedicineOrderVendorAssignRequest request);

	/*List<MedicineOrder> getOrderListByPatient(String patientId, String updatedTime, String searchTerm, int page,
			int size, List<String> status, Boolean isImageAvailable, Boolean isRXAvailable);
*/
	Integer getCBCount(String locationId, String searchTerm, String labType);

	List<Vendor> getVendorList(String updatedTime, String searchTerm, int page, int size, Boolean discarded);

	List<MedicineOrder> getOrderListByPatient(String patientId, String updatedTime, String searchTerm, int page,
			int size, List<String> status, Boolean isImageAvailable, Boolean isRXAvailable, Boolean discarded);

	List<CollectionBoyResponse> getCollectionBoyList(int size, int page, String locationId, String searchTerm,
			String labType, Boolean discarded);

	CollectionBoy discardCollectionBoy(String id, Boolean discarded);

	CollectionBoyResponse getCollectionBoyById(String id);

	TrackingOrder addeditTrackingDetails(TrackingOrder request);

	List<TrackingOrder> getTrackingList(String orderId, String updatedTime, String searchTerm, int page, int size);

	MedicineOrder assignDeliveryBoyToOrder(MedicineOrderAssignDeliveryBoyRequest request);

	Integer getVendorListCount(String updatedTime, String searchTerm, Boolean discarded);

	Integer getCollectionBoyListCount(String locationId, String searchTerm, String labType, Boolean discarded);

	Integer getOrderListCount(String updatedTime, String searchTerm, Boolean discarded, Long from, Long to);

	/*List<MedicineOrder> getOrderList(String updatedTime, String searchTerm, int page, int size, Boolean discarded,
			Long from, Long to);*/

	CollectionBoyResponse editCollectionBoy(EditDeliveryBoyRequest request);

	//ImageURLResponse uploadImage(FormDataBodyPart file);

	List<MedicineOrder> getOrderList(String updatedTime, String searchTerm, int page, int size, Boolean discarded,
			Long from, Long to, Boolean isImageAvailable, Boolean isRXAvailable,String orderStatus);

	List<CollectionBoyCautionMoney> getCollectionBoyCautionMoneyHistory(String collectionBoyId, Long from, Long to,
			int page, int size);

	Boolean discardCollectionBoyCautionMoney(String id, Boolean discarded);

	CollectionBoyCautionMoney addeditCollectionBoyCautionMoney(CollectionBoyCautionMoney request);

	CollectionBoyCautionMoneyHistory addeditCollectionBoyCautionMoneyHistory(CollectionBoyCautionMoneyHistory request);

	List<DrugInfo> getDrugInfo(int page, int size, String updatedTime, String searchTerm, Boolean discarded);

	ImageURLResponse uploadImage(MultipartFile file);

	MedicineOrder addeditRxImage(MedicineOrderRxImageRequest request);

	
}
