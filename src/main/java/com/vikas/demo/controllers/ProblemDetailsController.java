package com.vikas.demo.controllers;

import com.vikas.demo.entity.ProblemDetails;
import com.vikas.demo.service.ProblemDetailsServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/problem-details")
@Tag(name = "Problem Details API", description = "Endpoints for the heavy text/code details linked to main problems")
public class ProblemDetailsController {

    @Autowired
    private ProblemDetailsServices problemDetailsServices;

    @Operation(summary = "Fetch heavy details (markdown, code) for a specific problem")
    @GetMapping("/{problemId}")
    public ResponseEntity<?> getProblemDetails(@PathVariable String problemId) {
        try {
            ObjectId objId = new ObjectId(problemId);
            Optional<ProblemDetails> details = problemDetailsServices.getDetailsByProblemId(objId);

            if (details.isPresent()) {
                return new ResponseEntity<>(details.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Details not found for this problem", HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid Problem ID format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Add details for a single problem (Admin Only)")
    @PostMapping("/add")
    public ResponseEntity<?> addProblemDetails(@RequestBody ProblemDetails details) {
        try {
            Boolean isSaved = problemDetailsServices.saveProblemDetails(details);
            if (isSaved) {
                return new ResponseEntity<>("Problem details added successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to add problem details", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Add details for multiple problems at once (Admin Only)")
    @PostMapping("/add-multiple")
    public ResponseEntity<?> addMultipleProblemDetails(@RequestBody List<ProblemDetails> detailsList) {
        try {
            Boolean areSaved = problemDetailsServices.saveMultipleProblemDetails(detailsList);
            if (areSaved) {
                return new ResponseEntity<>(detailsList.size() + " problem details added successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to add problem details", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}