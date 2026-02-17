package com.vikas.demo.controllers;


import com.vikas.demo.entity.ProblemSubmissionEntity;
import com.vikas.demo.entity.User;
import com.vikas.demo.service.ProblemSubmissionServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/submissions")
public class ProblemSubmissionController {

    @Autowired
    private ProblemSubmissionServices problemSubmissionServices;

    @GetMapping
    private ResponseEntity<?> getAllSubmission(Authentication authentication){
        String username=authentication.getName();
        return problemSubmissionServices.getAllSubmissionOfUser(username);
    }

    @GetMapping("{submissionId}")
    private ResponseEntity<?> getParticularSubmission(@PathVariable ObjectId submissionId){
        return problemSubmissionServices.getSubmission(submissionId);
    }

    @GetMapping("/problem/{problemId}")
    private ResponseEntity<?> getAllSubmissionOfSameProblem(@PathVariable ObjectId problemId,Authentication authentication){
        return problemSubmissionServices.getALlSubmissionOfSameProblemOfUser(authentication.getName(),problemId);
    }

    @PostMapping
    private ResponseEntity<?> createNewSubmission(@RequestBody ProblemSubmissionEntity newEntry,Authentication authentication){
        newEntry.setUsername(authentication.getName());
        return problemSubmissionServices.createSubmission(newEntry);
    }


}
