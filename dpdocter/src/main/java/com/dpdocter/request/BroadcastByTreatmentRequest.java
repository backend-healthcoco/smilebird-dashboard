package com.dpdocter.request;

import java.util.List;

import com.dpdocter.enums.BroadcastType;

public class BroadcastByTreatmentRequest {
	private List<String> templateId;
	private BroadcastType broadcastType;
	private String message;
	private String languageCode;
	private String headerMediaUrl;
	private List<String> headerValues;
	private List<String> bodyValues;
	private String headerFileName;
	private String footer;
	private List<String> buttonValues;
	private String userId;

	public List<String> getTemplateId() {
		return templateId;
	}

	public void setTemplateId(List<String> templateId) {
		this.templateId = templateId;
	}

	public BroadcastType getBroadcastType() {
		return broadcastType;
	}

	public void setBroadcastType(BroadcastType broadcastType) {
		this.broadcastType = broadcastType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getHeaderMediaUrl() {
		return headerMediaUrl;
	}

	public void setHeaderMediaUrl(String headerMediaUrl) {
		this.headerMediaUrl = headerMediaUrl;
	}

	public List<String> getHeaderValues() {
		return headerValues;
	}

	public void setHeaderValues(List<String> headerValues) {
		this.headerValues = headerValues;
	}

	public List<String> getBodyValues() {
		return bodyValues;
	}

	public void setBodyValues(List<String> bodyValues) {
		this.bodyValues = bodyValues;
	}

	public String getHeaderFileName() {
		return headerFileName;
	}

	public void setHeaderFileName(String headerFileName) {
		this.headerFileName = headerFileName;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public List<String> getButtonValues() {
		return buttonValues;
	}

	public void setButtonValues(List<String> buttonValues) {
		this.buttonValues = buttonValues;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
