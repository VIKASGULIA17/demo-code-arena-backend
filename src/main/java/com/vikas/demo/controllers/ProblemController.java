package com.vikas.demo.controllers;

import com.vikas.demo.dto.ProblemDTO;
import com.vikas.demo.entity.Problem;
import com.vikas.demo.service.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/problem")
@Tag(name="All Problem's API",description = "This is the API controller used to add delete or update the problem and it is accessible by admin only")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @Operation(summary = "This is used to add problem by admin")
    @PostMapping("/add")
    public ResponseEntity<?> addProblem(@RequestBody ProblemDTO problemDTO){
        try{
            Problem problem = new Problem();
            problem.setSno(problemDTO.getSno());
            problem.setTitle(problem.getTitle());
            problem.setSlug(problemDTO.getSlug());
            problem.setTopicTags(problemDTO.getTopicTags());
            problem.setDifficulty(problem.getDifficulty());
            problem.setAcceptanceRate(problemDTO.getAcceptanceRate());
            problem.setInputType(problemDTO.getInputType());
            problem.setReturnType(problemDTO.getReturnType());
            problem.setStatus(problemDTO.isStatus());
            problem.setFunctionName(problemDTO.getFunctionName());
            Boolean isSaved = problemService.saveProblem(problem);
            if(isSaved){
                return new ResponseEntity<>("Problem added",HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("Problem not added", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Problem not added", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "This is used to fetch all Users")
    @GetMapping("/fetch")
    public ResponseEntity<?> fetchAllProblems(){
        try{
            List<Problem> allProblems = problemService.fetchAllProblems();
            if(allProblems.isEmpty()){
                return new ResponseEntity<>("",HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(allProblems,HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>("",HttpStatus.BAD_REQUEST);
        }
    }

}
