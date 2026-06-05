package com.adityavikas.codeverse.controllers;

import com.adityavikas.codeverse.dto.ContestProblemResponseDTO;
import com.adityavikas.codeverse.entity.Contest;
import com.adityavikas.codeverse.entity.User;
import com.adityavikas.codeverse.middleware.Middlewares;
import com.adityavikas.codeverse.repository.ContestRepository;
import com.adityavikas.codeverse.services.ContestService;
import com.adityavikas.codeverse.services.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contest")
@Tag(name="Contest API Controller (accessed right to admin only)",description = "This contains all the endpoints to handle add,delete,edit options")
public class ContestController {

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private Middlewares middlewares;

    @Autowired
    private ContestService contestService;

    @Autowired
    private ProblemService problemService;

    @Operation(summary = "This API endpoint is used to register into contest by users")
    @PostMapping("/register/{contestId}")
    public ResponseEntity<?> registerContest(HttpServletRequest request,@PathVariable String contestId){
        Map<String,Integer> returnResponse = new HashMap<>();
        returnResponse.put("status",0);
        try{
            String authorizationHeader = request.getHeader("authorization");
            boolean isConnected = contestService.registerInContest(contestId, authorizationHeader);
            if(isConnected){
                returnResponse.put("status",1);
                return new ResponseEntity<>(returnResponse,HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>(returnResponse,HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Operation(summary = "This API Endpoint is used to check whether the current user registered in the contest or not")
    @GetMapping("/isRegisterContest/{contestId}")
    public ResponseEntity<?> isRegisterContestByUser(HttpServletRequest request,@PathVariable String contestId){
        Map<String,Integer> returnResponse = new HashMap<>();
        returnResponse.put("status",0);
        try{
            String authorizationHeader = request.getHeader("Authorization");
            String userId = middlewares.getUserIdByJwt(authorizationHeader);
            boolean isRegistered = contestService.isRegisterInContest(contestId,userId);
            if(isRegistered){
                returnResponse.put("status",1);
                return new ResponseEntity<>(returnResponse,HttpStatus.OK);
            }
            return new ResponseEntity<>(returnResponse,HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(returnResponse,HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "This is used to fetch all contest problem")
    @GetMapping("/fetchContestProblemForUser/{contestId}")
    private ResponseEntity<?> fetchAllContestProblem(@PathVariable ObjectId contestId){
        try{
            List<ContestProblemResponseDTO> allProblems = problemService.getContestProblems(contestId);
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
