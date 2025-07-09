package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.PackageDetailObject;
import com.dpdocter.enums.PackageType;

public interface PackageDetailObjectService {

	public PackageDetailObject addEditPackageDetailObject(PackageDetailObject request);

	public List<PackageDetailObject> getPackages(int size, int page, Boolean isDiscarded, String searchTerm);

	public Integer countPackages(Boolean isDiscarded, String searchTerm);

	public PackageDetailObject getPackageDetailByPackageName(PackageType packageName);
	
	public Boolean deletePackageDetail(String id, Boolean isDiscarded);
}
