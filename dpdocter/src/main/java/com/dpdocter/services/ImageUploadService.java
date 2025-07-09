package com.dpdocter.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.UploadFile;
import com.dpdocter.beans.UploadFilePath;
import com.dpdocter.response.ImageURLResponse;

public interface ImageUploadService {

	

//	ImageURLResponse uploadFile(MultipartFile file, String path, Boolean createThumbnail);

	ImageURLResponse uploadFile(MultipartFile file, String path, Boolean createThumbnail, String type);
	
	List<UploadFile> getFiles(String searchTerm,String type,int page,int size);

	List<UploadFilePath> getPath(String searchTerm, int page, int size);

	Boolean uploadDoctorData();

	Boolean uploadClinicData();

	Boolean downloadUserAppPatientData();

	String uploadDoctorNewData();
}
