package com.dpdocter.webservices.v3;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.BroadcastType;
import com.dpdocter.enums.SmsRoute;

@Document(collection = "dental_camp_msg_template_cl")
public class CampMsgTemplateCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String title;
	@Field
	private String purpose;
	@Field
	private SmsRoute route;
	@Field
	private String senderId;
	@Field
	private String templateId;
	@Field
	private String message;
	@Field
	private Boolean discarded = false;

	// ForWhatsapp template request
	@Field
	private String templateName;
	@Field
	private String category;
	@Field
	private String languageCode;
	@Field
	private String headerMediaUrl;
	@Field
	private List<String> headerValues;
	@Field
	private List<String> bodyValues;
	@Field
	private String headerFileName;
	@Field
	private String footer;
	@Field
	private List<String> buttonValues;
	@Field
	private BroadcastType broadcastType;
	@Field
	private ObjectId treatmentId;
	@Field
	private Boolean preTreatment = false;
	@Field
	private Boolean postTreatment = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public SmsRoute getRoute() {
		return route;
	}

	public void setRoute(SmsRoute route) {
		this.route = route;
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

	public BroadcastType getBroadcastType() {
		return broadcastType;
	}

	public void setBroadcastType(BroadcastType broadcastType) {
		this.broadcastType = broadcastType;
	}

	public ObjectId getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(ObjectId treatmentId) {
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

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public List<String> getBodyValues() {
		return bodyValues;
	}

	public void setBodyValues(List<String> bodyValues) {
		this.bodyValues = bodyValues;
	}

}
