package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DiagnosticTestPackage;
import com.dpdocter.beans.DiagnosticTestSamplePickUpSlot;
import com.dpdocter.beans.OrderDiagnosticTest;
import com.dpdocter.collections.DiagnosticTestPickUpSlotCollection;

public interface DiagnosticTestOrderService {

	List<DiagnosticTestSamplePickUpSlot> getDiagnosticTestSamplePickUpTimeSlots();

	DiagnosticTestPickUpSlotCollection addEditPickUpTimeSlots(DiagnosticTestPickUpSlotCollection request);

	OrderDiagnosticTest updateDiagnosticTestOrderStatus(String orderId, String status);

	List<OrderDiagnosticTest> getOrders(String locationId, String userId, int page, int size);

	OrderDiagnosticTest getDiagnosticTestOrderById(String orderId);

	List<DiagnosticTestPackage> getDiagnosticTestPackages(String locationId, String hospitalId, Boolean discarded, int page, int size);

	DiagnosticTestPackage addEditDiagnosticTestPackage(DiagnosticTestPackage request);

}
