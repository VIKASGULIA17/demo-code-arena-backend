package com.adityavikas.codeverse.controllers;
import com.adityavikas.codeverse.dto.ContestSubmissionDTO;
import com.adityavikas.codeverse.dto.JdoodleResponseDTO;
import com.adityavikas.codeverse.entity.User;
import com.adityavikas.codeverse.middleware.Middlewares;
import com.adityavikas.codeverse.repository.SolvedProblemRepository;
import com.adityavikas.codeverse.services.CodeExecutionService;
import com.adityavikas.codeverse.utils.CodeExecutionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/solvedProblem")
@AllArgsConstructor
public class SolvedProblemController {

    private final SolvedProblemRepository solvedProblemRepository;


    private final Middlewares middlewares;

        //we will use this controller to handle the user submission during contest

//    the data required for this

//    contest submission dto




    @PostMapping("submission/contest")
    private ResponseEntity<?> handleContestSubmit(HttpServletRequest request, @RequestBody ContestSubmissionDTO userResponse){

        String authenticationHeader=request.getHeader("Authorization");

        if(authenticationHeader==null){
            return new ResponseEntity<>("Login to run Code", HttpStatus.UNAUTHORIZED);
        }

        String username=middlewares.getUserNameByJwt(authenticationHeader);

        if(username == null){
            return new ResponseEntity<>("Login to run Code",HttpStatus.UNAUTHORIZED);
        }






        return null;
    }

}
