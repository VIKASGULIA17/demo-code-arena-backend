package com.adityavikas.codeverse.entity;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class SolvedProblemEntity {
    private ObjectId problemId;
    private LocalDateTime solvedAt;
    private int score;
    private int penalty; // how many wrong submissions user made (to make it like leetcode) 
}
