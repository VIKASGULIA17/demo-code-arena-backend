package com.vikas.demo.repository;

import com.vikas.demo.entity.ProblemDetails;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProblemDetailsRepository extends MongoRepository<ProblemDetails, ObjectId> {
    // Custom query to find the details using the main problem's ID
    Optional<ProblemDetails> findByProblemId(ObjectId problemId);
}