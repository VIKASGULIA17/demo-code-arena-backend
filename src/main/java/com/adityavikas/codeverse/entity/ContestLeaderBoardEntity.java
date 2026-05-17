package com.adityavikas.codeverse.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Document(collection = "contest-leaderBoard")
public class ContestLeaderBoardEntity {
    @Id
    private ObjectId leaderBoardId;

    private ObjectId contestId; //reference
    private ObjectId userId; //for user reference
    private List<SolvedProblemEntity> solvedProblems; //it will include
//    1 - > problemId
//    2->solvedAt
//    3rd -> score (based on their difficulty)
//    Scoring:
//    Easy = 1pt
//    Medium = 3pt
//     Hard = 5pt

    private int totalScore;
    private int totalTime;//duration

}
