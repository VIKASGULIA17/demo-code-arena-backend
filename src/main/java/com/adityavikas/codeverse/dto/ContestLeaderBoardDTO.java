package com.adityavikas.codeverse.dto;

import com.adityavikas.codeverse.entity.SolvedProblemEntity;
import lombok.Data;
import lombok.NonNull;
import org.bson.types.ObjectId;

import java.util.List;

@Data
public class ContestLeaderBoardDTO {
    //it is coming from user

    @NonNull
    private ObjectId contestId;
    @NonNull
    private ObjectId userId;

    private List<SolvedProblemDTO> solvedProblems;





}
