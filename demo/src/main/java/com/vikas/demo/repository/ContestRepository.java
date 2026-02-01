package com.vikas.demo.repository;

import com.vikas.demo.entity.ContestEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContestRepository extends MongoRepository<ContestEntity, ObjectId> {
}
