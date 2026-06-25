package com.adityavikas.codeverse.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "SolvedContestProblem")
public class SolvedProblemEntity {
    private ObjectId problemId;
    private LocalDateTime solvedAt;
    private int score;
    private int penalty; // how many wrong submissions user made (to make it like leetcode)
}
