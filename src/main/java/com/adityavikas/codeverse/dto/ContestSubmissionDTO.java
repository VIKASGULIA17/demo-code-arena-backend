package com.adityavikas.codeverse.dto;


import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class ContestSubmissionDTO {

    //from user

    private ObjectId problemId;//to link problem
    private ObjectId contestId; // to get submission between that time span

    private ExecuteRequest executeRequest;


}
