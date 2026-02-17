package com.vikas.demo.repository;

import com.vikas.demo.entity.ProblemSubmissionEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProblemSubmissionRepository extends MongoRepository<ProblemSubmissionEntity, ObjectId> {

    List<ProblemSubmissionEntity> findByUsername(String username);
    List<ProblemSubmissionEntity> findByUsernameAndProblemId(String username, ObjectId problemId);
    ProblemSubmissionEntity findBySubmissionId(ObjectId submissionId);

}
