package com.adityavikas.codeverse.services;


import com.adityavikas.codeverse.dto.ContestSubmissionDTO;
import com.adityavikas.codeverse.dto.JdoodleResponseDTO;
import com.adityavikas.codeverse.utils.CodeExecutionUtil;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SolvedProblemService {

    private static final Logger logger = LoggerFactory.getLogger(SolvedProblemService.class);

    private final CodeExecutionUtil codeExecutionUtil;
    public ResponseEntity<?> handleContestSubmission(ObjectId userId, ContestSubmissionDTO userResponse){
        try{

            JdoodleResponseDTO response=codeExecutionUtil.runJdoodleCode(userResponse.getExecuteRequest());

//            if(response.)

            return null;
        } catch (Exception e) {
            return null;
        }


    }

}
