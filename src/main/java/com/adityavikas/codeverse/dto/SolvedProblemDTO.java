package com.adityavikas.codeverse.dto;

import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
public class SolvedProblemDTO {

    private ObjectId problemId;
    private int score;
    private int penalty; // how many wrong submissions user made (to make it like leetcode)

}
