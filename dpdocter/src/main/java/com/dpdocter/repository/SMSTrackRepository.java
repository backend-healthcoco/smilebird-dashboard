package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.SMSTrackDetail;

public interface SMSTrackRepository extends MongoRepository<SMSTrackDetail, ObjectId>, PagingAndSortingRepository<SMSTrackDetail, ObjectId> {

    List<SMSTrackDetail> findByLocationIdAndHospitalIdAndTypeIn(ObjectId locationId, ObjectId hospitalId, String[] type, Pageable pageable);

    List<SMSTrackDetail> findByLocationIdAndHospitalIdAndTypeIn(ObjectId locationId, ObjectId hospitalId, String[] type, Sort sort);

    List<SMSTrackDetail> findByDoctorIdAndLocationIdAndHospitalIdAndTypeIn(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String[] type, Pageable pageable);

    List<SMSTrackDetail> findByDoctorIdAndLocationIdAndHospitalIdAndTypeIn(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String[] type, Sort sort);

    @Query(value = "{'doctorId' : ?0, 'locationId': ?1, 'hospitalId' : ?2}", count = true)
    Integer getDoctorsSMSCount(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

    List<SMSTrackDetail> findByTypeIn(String[] type, Pageable pageRequest);

    List<SMSTrackDetail> findByTypeIn(String[] type, Sort sort);
  
    SMSTrackDetail findByResponseId(String requestId);
}
