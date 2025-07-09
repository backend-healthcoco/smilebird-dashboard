package com.dpdocter.beans;

import java.util.List;

public class FooterSetup {

    private Boolean customFooter;

    private Boolean showSignature;

    private List<PrintSettingsText> bottomText;
    
    private Boolean showPoweredBy=false;

    public Boolean getCustomFooter() {
	return customFooter;
    }

    public Boolean getShowPoweredBy() {
		return showPoweredBy;
	}

	public void setShowPoweredBy(Boolean showPoweredBy) {
		this.showPoweredBy = showPoweredBy;
	}

	public void setCustomFooter(Boolean customFooter) {
	this.customFooter = customFooter;
    }

    public Boolean getShowSignature() {
	return showSignature;
    }

    public void setShowSignature(Boolean showSignature) {
	this.showSignature = showSignature;
    }

    public List<PrintSettingsText> getBottomText() {
	return bottomText;
    }

    public void setBottomText(List<PrintSettingsText> bottomText) {
	this.bottomText = bottomText;
    }

    @Override
    public String toString() {
	return "FooterSetup [customFooter=" + customFooter + ", showSignature=" + showSignature + ", bottomText=" + bottomText + "]";
    }
}
