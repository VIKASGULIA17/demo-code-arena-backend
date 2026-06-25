package com.adityavikas.codeverse.dto;


import com.adityavikas.codeverse.entity.SolvedProblemEntity;
import lombok.Data;
import org.bson.types.ObjectId;

import java.util.List;

@Data
public class ContestLeaderBoardResponseDTO {

    private ObjectId contestId;

    private ObjectId userId;

    private List<SolvedProblemEntity> solvedProblems;

    private int totalScore;
    private int totalTime;//duration
}
