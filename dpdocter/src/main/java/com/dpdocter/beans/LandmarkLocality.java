package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class LandmarkLocality extends GenericCollection{

    private String id;

    private String cityId;

    private String landmark;

    private String locality;

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

    public String getLandmark() {
	return landmark;
    }

    public void setLandmark(String landmark) {
	this.landmark = landmark;
    }

    public String getLocality() {
	return locality;
    }

    public void setLocality(String locality) {
	this.locality = locality;
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
		return "LandmarkLocality [id=" + id + ", cityId=" + cityId + ", landmark=" + landmark + ", locality=" + locality
				+ ", explanation=" + explanation + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", postalCode=" + postalCode + "]";
	}
}
