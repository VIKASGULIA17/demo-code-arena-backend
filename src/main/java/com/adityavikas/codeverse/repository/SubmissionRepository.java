package com.adityavikas.codeverse.repository;

import com.adityavikas.codeverse.entity.Submission;
import com.adityavikas.codeverse.services.SubmissionService;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SubmissionRepository extends MongoRepository<Submission, ObjectId> {

    Submission findByslug(String slug);
    @Aggregation(pipeline = {
            "{ '$match': { 'username': ?0, 'status': ?1 } }",
            "{ '$group': { '_id': '$problemId' } }" //this is gor distinct problem id
    })
    List<String> findDistinctProblemIdByUsernameAndStatus(String username,String status);

}
