package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.BulkSmsCredits;
import com.dpdocter.beans.BulkSmsCreditsRequest;
import com.dpdocter.beans.BulkSmsPackage;
import com.dpdocter.beans.BulkSmsReport;

public interface BulkSmsPackageServices {

	BulkSmsPackage addEditBulkSmsPackage(BulkSmsPackage request);
	
	List<BulkSmsPackage> getBulkSmsPackage(int page,int size,String searchTerm,Boolean discarded);
	
	Integer CountBulkSmsPackage(String doctorId,Boolean discarded);
	
	BulkSmsPackage getBulkSmsPackageById(String bulkSmsId);
	
	BulkSmsCredits getCreditsByDoctorId(String doctorId);
	
	Boolean addBulkSmsCredits(BulkSmsCreditsRequest request);
	
	List<BulkSmsCredits> getBulkSmsHistory(int page,int size,String searchTerm,String doctorId,String locationId);
	
	BulkSmsPackage deleteSmsPackage(String packageId,Boolean discarded);

	List<BulkSmsReport> getSmsReport(int page, int size, String doctorId, String locationId);

	
}
