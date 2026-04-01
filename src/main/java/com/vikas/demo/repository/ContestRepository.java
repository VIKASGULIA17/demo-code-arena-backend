package com.vikas.demo.repository;

import com.vikas.demo.entity.ContestEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContestRepository extends MongoRepository<ContestEntity, ObjectId> {
    ContestEntity findByContestName(String contestName);

    ContestEntity findByContestId(ObjectId contestId);

    void deleteByContestName(String contestName);
}
