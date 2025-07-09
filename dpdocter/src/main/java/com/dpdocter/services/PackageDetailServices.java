package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.PackageDetail;

public interface PackageDetailServices {
	
	public PackageDetail addEditPackageDetail(PackageDetail request);

	public List<PackageDetail> getPackageDetails(int size, int page, Boolean discarded,String searchTerm);
	
	public Integer countPackageDetails(Boolean discarded,String searchTerm);
	
	public PackageDetail deletePackageDetail(String id, Boolean discarded);

	
}
