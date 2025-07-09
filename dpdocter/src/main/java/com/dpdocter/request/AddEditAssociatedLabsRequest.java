package com.dpdocter.request;

import java.util.List;

public class AddEditAssociatedLabsRequest {
	private String locationId;
	private List<String> associatedLabs;

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public List<String> getAssociatedLabs() {
		return associatedLabs;
	}

	public void setAssociatedLabs(List<String> associatedLabs) {
		this.associatedLabs = associatedLabs;
	}

	@Override
	public String toString() {
		return "AddEditAssociatedLabsRequest [locationId=" + locationId + ", associatedLabs=" + associatedLabs + "]";
	}

}
