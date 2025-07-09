package com.dpdocter.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.CompanyDetail;
import com.dpdocter.beans.SafePlaceMobileNumber;
import com.dpdocter.collections.SafePlaceMobileNumberCollection;
import com.dpdocter.response.ImageURLResponse;

//for covid project

public interface CompanyDetailService {

	public CompanyDetail addEditCompanyDetail(CompanyDetail request);

	public List<CompanyDetail> getCompanyDetail(int size, int page, Boolean isDiscarded, String searchTerm);

	public Integer countCompanyDetail(Boolean isDiscarded, String searchTerm);

	public Boolean deleteCompanyDetail(String companyId, Boolean isDiscarded);

	public CompanyDetail getCompanyDetailById(String companyId);

//	public ImageURLResponse uploadImages(FileDetails request, String module);

	public ImageURLResponse uploadImageMultipart(MultipartFile file);

	public Boolean activeAdmin(String id, Boolean isActive);

	public List<SafePlaceMobileNumber> getMobileNumbers(int size, int page, Boolean isDiscarded,
			String searchTerm);
	
	public Integer getMobileNumbersCount(Boolean isDiscarded, String searchTerm);

	public SafePlaceMobileNumber addEditMobileNumberReason(SafePlaceMobileNumber request);

}
