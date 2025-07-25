package com.dpdocter.response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dpdocter.collections.GenericCollection;

public class MessageResponse extends GenericCollection{

	private String body;
	
	private String sender;
	
	private String type;
	
	private String source;
	
	private String id;
	
	private String createdDateTime;
	
	private Integer  totalCount;
	
	private List<MessageData> data;
	
	private String dlrurl;
	
	private Map<String,String>error;
	
	private String messageId;

	private Integer unicode;
	
	private String template_id;
	
	SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
	
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

	
	

	public String getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	

	public List<MessageData> getData() {
		return data;
	}

	public void setData(List<MessageData> data) {
		this.data = data;
	}

	public String getDlrurl() {
		return dlrurl;
	}

	public void setDlrurl(String dlrurl) {
		this.dlrurl = dlrurl;
	}

	public Map<String, String> getError() {
		return error;
	}

	public void setError(Map<String, String> error) {
		this.error = error;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Integer getUnicode() {
		return unicode;
	}

	public void setUnicode(Integer unicode) {
		this.unicode = unicode;
	}

	public String getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}

	
	
}
