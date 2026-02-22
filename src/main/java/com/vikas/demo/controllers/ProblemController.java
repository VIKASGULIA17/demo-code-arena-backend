package com.vikas.demo.controllers;

import com.vikas.demo.entity.Problem;
import com.vikas.demo.service.ProblemServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/problem")
@Tag(name="All Problem's API", description = "This is the API controller used to add, delete, or update problems. Accessible by admin only.")
public class ProblemController {

    @Autowired
    private ProblemServices problemService;

    @Operation(summary = "This is used to add a problem by an admin")
    @PostMapping("/add")
    public ResponseEntity<?> addProblem(@RequestBody Problem problem) {
        try {
            // Because we removed the DTO, Spring Boot automatically maps the incoming JSON
            // directly to this 'problem' object. We just pass it to the service!
            Boolean isSaved = problemService.saveProblem(problem);

            if (isSaved) {
                return new ResponseEntity<>("Problem added successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to add problem", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while adding the problem", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "This is used to add multiple problems at once by an admin")
    @PostMapping("/add-multiple")
    public ResponseEntity<?> addMultipleProblems(@RequestBody List<Problem> problems) {
        try {
            Boolean areSaved = problemService.saveMultipleProblems(problems);

            if (areSaved) {
                return new ResponseEntity<>(problems.size() + " problems added successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to add problems", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while adding the problems", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "This is used to fetch all Problems")
    @GetMapping("/fetch")
    public ResponseEntity<?> fetchAllProblems() {
        try {
            List<Problem> allProblems = problemService.fetchAllProblems();
            if (allProblems.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(allProblems, HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}