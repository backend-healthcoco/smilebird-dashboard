package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.AppointmentCollection;

public interface AppointmentRepository extends MongoRepository<AppointmentCollection, ObjectId>,
		PagingAndSortingRepository<AppointmentCollection, ObjectId> {

	AppointmentCollection findByAppointmentId(String appointmentId);

}
