package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "zone_cl")
public class ZoneCollection extends GenericCollection{

    @Id
    private ObjectId id;

    @Field
    private ObjectId cityId;

    @Field
    private String zoneName;

    @Field
    private String explanation;

    @Field
    private Double latitude;

    @Field
    private Double longitude;

    @Field
    private String postalCode;
    
    @Field
    private Boolean isDiscarded= false;

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public ObjectId getCityId() {
	return cityId;
    }

    public void setCityId(ObjectId cityId) {
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

	public Double getLatitude() {
	return latitude;
    }

    public void setLatitude(Double latitude) {
	this.latitude = latitude;
    }

    public Double getLongitude() {
	return longitude;
    }

    public void setLongitude(Double longitude) {
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
		return "ZoneCollection [id=" + id + ", cityId=" + cityId + ", zoneName=" + zoneName
				+ ", explanation=" + explanation + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", postalCode=" + postalCode + "]";
	}
} 