package com.adityavikas.codeverse.repository;

import com.adityavikas.codeverse.entity.ContestLeaderBoardEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.ObjectInput;
import java.util.List;

public interface ContestLeaderBoardRepository extends MongoRepository<ContestLeaderBoardEntity, ObjectId> {

    List<ContestLeaderBoardEntity> findByContestIdOrderByTotalScoreDescTotalTimeAsc(ObjectId contestId);

}
