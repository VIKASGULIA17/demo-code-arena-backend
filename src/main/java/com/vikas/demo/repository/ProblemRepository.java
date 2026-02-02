package com.vikas.demo.repository;

import com.vikas.demo.entity.Problem;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProblemRepository extends MongoRepository<Problem, ObjectId> {
}
