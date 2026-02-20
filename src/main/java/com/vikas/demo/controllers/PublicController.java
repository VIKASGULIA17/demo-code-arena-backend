package com.vikas.demo.controllers;


import com.vikas.demo.entity.User;
import com.vikas.demo.repository.UserRepository;
import com.vikas.demo.service.*;
import com.vikas.demo.utils.JwtUtils;
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
import java.util.Map;

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
