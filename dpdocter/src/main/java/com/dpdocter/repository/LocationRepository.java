package com.dpdocter.repository;

import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.LocationCollection;

@Repository
public interface LocationRepository extends MongoRepository<LocationCollection, ObjectId> {

	List<LocationCollection> findByIdIn(Set<ObjectId> locationIds);

	List<LocationCollection> findByHospitalId(ObjectId hospitalId, boolean isLab, Pageable pageable);

	List<LocationCollection> findByHospitalId(ObjectId hospitalId, boolean isLab, Sort sort);

	@Query(value = "{'hospitalId':?0}", count = true)
	Integer countLabs(ObjectId hospitalId, boolean isLab);

	List<LocationCollection> findByIsLab(boolean isLab, Pageable pageable);

	List<LocationCollection> findByIsLab(boolean isLab, Sort sort);

	@Query(value = "{'isLab':?0,'isLocationListed':?1}", count = true)
	Integer countLabs(boolean isLab, boolean isLocationListed);

	@Query(value = "{'isLab':?0}", count = true)
	Integer countLabs(boolean isLab);

	@Query(value = "{'isClinic':?0,'isLocationListed':?1}", count = true)
	Integer countClinic(boolean isClinic, boolean isLocationListed);

	@Query(value = "{'isClinic':?0}", count = true)
	Integer countClinic(boolean isClinic);

	List<LocationCollection> findByHospitalIdAndIsClinicAndIsLab(ObjectId hospitalId, Boolean isClinic, Boolean isLab,
			Pageable pageable);

	
	


	Page<LocationCollection> findAll(Pageable pageable);

	@Query("{'localeUId':?0}")
	LocationCollection findByLocaleUID(String localeUId);

	@Query(value = "{'locationSlugUrl':{$regex:'^?0*',$options:'i'}}", count = true)
	Integer countBySlugUrl(String locationSlugUrl);

	LocationCollection findByLocationName(String locationName);

	LocationCollection findByIdAndIsActivate(ObjectId objectId, Boolean active);

}
