package com.dpdocter.response;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.BroadcastType;
import com.dpdocter.enums.SmsRoute;

public class CampMsgTemplateResponse extends GenericCollection{
	private String id;
	private String title;
	private String purpose;
	private SmsRoute route;
	private String senderId;
	private String templateId;
	private String message;
	private Boolean discarded= false;
	private String templateName;
	private String category;
	private String languageCode;
	private String headerMediaUrl;
	private List<String> headerValues;
	private String headerFileName;
	private String footer;
	private List<String> buttonValues;
	private BroadcastType broadcastType;
	private String treatmentId;
	private Boolean preTreatment = false;
	private Boolean postTreatment = false;
	
	public BroadcastType getBroadcastType() {
		return broadcastType;
	}
	public void setBroadcastType(BroadcastType broadcastType) {
		this.broadcastType = broadcastType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Boolean getDiscarded() {
		return discarded;
	}
	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
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
	public String getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(String treatmentId) {
		this.treatmentId = treatmentId;
	}

	public Boolean getPreTreatment() {
		return preTreatment;
	}
	public void setPreTreatment(Boolean preTreatment) {
		this.preTreatment = preTreatment;
	}
	public Boolean getPostTreatment() {
		return postTreatment;
	}
	public void setPostTreatment(Boolean postTreatment) {
		this.postTreatment = postTreatment;
	}
	public SmsRoute getRoute() {
		return route;
	}
	public void setRoute(SmsRoute route) {
		this.route = route;
	}

}
