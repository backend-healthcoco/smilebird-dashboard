package com.dpdocter.beans;

import com.dpdocter.enums.MailType;

public class MailData {
    private MailType mailType;

    private String id;

    public MailType getMailType() {
	return mailType;
    }

    public void setMailType(MailType mailType) {
	this.mailType = mailType;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    @Override
    public String toString() {
	return "MailData [mailType=" + mailType + ", id=" + id + "]";
    }

}
