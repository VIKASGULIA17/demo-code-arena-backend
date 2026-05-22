package com.adityavikas.codeverse.repository;

import com.adityavikas.codeverse.entity.ProblemDetails;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProblemDetailRepository extends MongoRepository<ProblemDetails,ObjectId> {

    ProblemDetails findByProblemId(ObjectId problemId);
    void deleteByProblemId(ObjectId problemId);

    void deleteAllByProblemId(List<ObjectId> problemIds);
}
