package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.BlogImagesCollection;

public interface BlogImagesRepository extends MongoRepository<BlogImagesCollection, ObjectId>,
		PagingAndSortingRepository<BlogImagesCollection, ObjectId> {

	List<BlogImagesCollection> findByCatagory(String category, Pageable pageable);

	List<BlogImagesCollection> findByUserIdAndCatagory(ObjectId userId, String category, Pageable pageable);

	List<BlogImagesCollection> findByUserIdAndCatagory(ObjectId userId, String category);

	List<BlogImagesCollection> findByUserId(ObjectId userId);

	List<BlogImagesCollection> findByUserId(ObjectId userId, Pageable pageable);

	List<BlogImagesCollection> findByCatagory(String category);

	Page<BlogImagesCollection> findAll(Pageable pageable);

}
