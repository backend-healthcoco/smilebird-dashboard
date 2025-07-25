package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Zone extends GenericCollection{

    private String id;

    private String cityId;

    private String zoneName;

    private String explanation;

    private double latitude;

    private double longitude;

    private String postalCode;
    
    private Boolean isDiscarded= false;
    
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getCityId() {
	return cityId;
    }

    public void setCityId(String cityId) {
	this.cityId = cityId;
    }

    public String getZoneName() {
	return zoneName;
    }

    public void setZoneName(String zoneName) {
	this.zoneName = zoneName;
    }

    public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public double getLatitude() {
	return latitude;
    }

    public void setLatitude(double latitude) {
	this.latitude = latitude;
    }

    public double getLongitude() {
	return longitude;
    }

    public void setLongitude(double longitude) {
	this.longitude = longitude;
    }

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	@Override
	public String toString() {
		return "Zone [id=" + id + ", cityId=" + cityId + ", zoneName=" + zoneName
				+ ", explanation=" + explanation + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", postalCode=" + postalCode + "]";
	}
} 