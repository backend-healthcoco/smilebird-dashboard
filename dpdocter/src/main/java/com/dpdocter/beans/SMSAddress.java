package com.dpdocter.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
public class SMSAddress {

    private String recipient;

    @JsonProperty("recipient")
    public String getRecipient() {
	return recipient;
    }

    @XmlAttribute(name = "TO")
    @JsonProperty("recipient")
    public void setRecipient(String recipient) {
	this.recipient = recipient;
    }

    @Override
    public String toString() {
	return "SMSAddress [recipient=" + recipient + "]";
    }

}
