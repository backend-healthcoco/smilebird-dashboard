package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.LabAssociation;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Specimen;

public interface LocationServices {
	public List<GeocodedLocation> geocodeLocation(String address);

	List<GeocodedLocation> geocodeTimeZone(Double latitude, Double longitude);

	Location addEditParentLab(String locationId, Boolean isParent);

	List<Location> getAssociatedLabs(String locationId, Boolean isParent);

	// Location addAssociatedLabs(List<LabAssociation> labAssociations);

	List<Specimen> getSpecimenList(int page, int size, String searchTerm);

	Boolean addAssociatedLabs(List<LabAssociation> labAssociations);

	Specimen addEditSpecimen(Specimen request);

}
