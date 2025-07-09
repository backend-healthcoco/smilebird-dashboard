package com.dpdocter.services;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.FileDetails;
import com.dpdocter.response.ImageURLResponse;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface FileManager {
    ImageURLResponse saveImageAndReturnImageUrl(FileDetails fileDetails, String path, Boolean createThumbnail) throws Exception;

    String saveThumbnailAndReturnThumbNailUrl(FileDetails fileDetails, String path);

    void saveRecord(FormDataBodyPart file, String recordPath);

	ImageURLResponse saveImage(FormDataBodyPart file, String recordPath, Boolean createThumbnail);

	String saveThumbnailUrl(MultipartFile file, String path);

	ImageURLResponse saveImage(MultipartFile file, String recordPath, Boolean createThumbnail);

}
