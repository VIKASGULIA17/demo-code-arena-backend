package com.adityavikas.codeverse.entity;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class SolvedProblemEntity {
    private ObjectId problemId;
    private LocalDateTime solvedAt;
    private int score;
}
