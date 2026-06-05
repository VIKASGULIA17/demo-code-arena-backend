package com.adityavikas.codeverse.controllers;

import com.adityavikas.codeverse.dto.ContestProblemDTO;
import com.adityavikas.codeverse.dto.ContestProblemResponseDTO;
import com.adityavikas.codeverse.services.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contestProblem")
@AllArgsConstructor
@Tag(name="Contest problem API Controller (accessed right to admin only)",description = "This contains all the endpoints to handle add,delete,edit options of contest's problem")
public class ContestProblemController {

    private final ProblemService problemService;

    @PostMapping("/{contestId}/problem")
    private ResponseEntity<?> createProblemForContest(@PathVariable ObjectId contestId, @RequestBody ContestProblemDTO contestProblemDTO){

        Map<String,Integer> returnResponse = new HashMap<>();
        returnResponse.put("status",0);
        try{
            boolean isProblemAdded = problemService.addContestProblem(contestId,contestProblemDTO);
            if(isProblemAdded){
                returnResponse.put("status",1);
                return new ResponseEntity<>(returnResponse, HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>(returnResponse  , HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(returnResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{contestId}/problem/{problemId}")
    private ResponseEntity<?> updateProblemForContest(@PathVariable ObjectId contestId, @PathVariable ObjectId problemId, @RequestBody ContestProblemDTO contestProblemDTO){

        Map<String,Integer> returnResponse = new HashMap<>();
        returnResponse.put("status",0);
        try{
            boolean isProblemUpdated = problemService.updateContestProblem(contestId, problemId, contestProblemDTO);
            if(isProblemUpdated){
                returnResponse.put("status",1);
                return new ResponseEntity<>(returnResponse, HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>(returnResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(returnResponse, HttpStatus.BAD_REQUEST);
        }
    }


    @Operation(summary = "This is used to fetch all contest problem")
    @GetMapping("/fetchContestProblemForEditor/{contestId}")
    private ResponseEntity<?> fetchAllContestProblem(@PathVariable ObjectId contestId){
        try{
            List<ContestProblemDTO> allProblems = problemService.getContestProblemsForEditor(contestId);
            if(allProblems.isEmpty()){
                return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(allProblems,HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
    }

}
