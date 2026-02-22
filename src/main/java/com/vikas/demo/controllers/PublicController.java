package com.vikas.demo.controllers;


import com.vikas.demo.entity.Problem;
import com.vikas.demo.entity.ProblemDetails;
import com.vikas.demo.entity.User;
import com.vikas.demo.repository.UserRepository;
import com.vikas.demo.service.*;
import com.vikas.demo.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/public")
public class PublicController {

    //contest services
    @Autowired
    private ContestServices contestServices;

    //user profile services
    @Autowired
    private UserProfileServices userProfileServices;

    //userserviceimpl
    @Autowired
    private UserServiceImpl userServiceimpl;

    //authentication manager for jwtoken
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ProblemServices problemService;


    @Autowired
    private ProblemDetailsServices problemDetailsServices;
    //jwtutils
    @Autowired
    private JwtUtils jwtUtils;

    //user repository
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemSubmissionServices problemSubmissionServices;



    //health check function
    @GetMapping("/health-check")
    public String healthCheck(){
        return "Its working! ok ";
    }


    //get all contest

    @GetMapping("/all-contests")
    private ResponseEntity<?> getContest(){
        return contestServices.getAllContests();
    }


    //see user profile
    @GetMapping("/profiles/{user_name}")
    private ResponseEntity<?> getUserProfile(@PathVariable String user_name){
        return userProfileServices.getUserProfile(user_name);
    }

    //get any submission
    @GetMapping("/submission/{submissionId}")
    private ResponseEntity<?> getParticularSubmission(@PathVariable ObjectId submissionId){
        return problemSubmissionServices.getSubmission(submissionId);
    }

    @GetMapping("/shared/{slug}")
    public ResponseEntity<?> getSharedSubmission(@PathVariable String slug){
        return problemSubmissionServices.getSubmissionBySlug(slug);
    }

    @Operation(summary = "This is used to fetch all Problems")
    @GetMapping("/problem/fetch")
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


    @Operation(summary = "Fetch heavy details (markdown, code) for a specific problem")
    @GetMapping("/problem/{problemId}")
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


    //to login user
    @PostMapping("/login")
    private ResponseEntity<?> login(@RequestBody User user){

        try{

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword()));
            UserDetails userDetails=userServiceimpl.loadUserByUsername(user.getUserName());
            String jwt =jwtUtils.generateToken(userDetails.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("user", userDetails);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("User not Found",HttpStatus.INTERNAL_SERVER_ERROR);
        }


//        String username=authentication.getName();
//
//        User user=userRepository.findByUserName(username);
//
//        return new ResponseEntity<>(user, HttpStatus.OK);

    }
}
