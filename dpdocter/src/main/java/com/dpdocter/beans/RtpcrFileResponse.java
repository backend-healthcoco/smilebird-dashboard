package com.dpdocter.beans;

import com.dpdocter.response.ImageURLResponse;

public class RtpcrFileResponse {

	private String filename;
	
	private String fileType;
	
	private String imageBitLink;
	
	private ImageURLResponse file;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public ImageURLResponse getFile() {
		return file;
	}

	public void setFile(ImageURLResponse file) {
		this.file = file;
	}

	public String getImageBitLink() {
		return imageBitLink;
	}

	public void setImageBitLink(String imageBitLink) {
		this.imageBitLink = imageBitLink;
	}
	
	
	
	
}
