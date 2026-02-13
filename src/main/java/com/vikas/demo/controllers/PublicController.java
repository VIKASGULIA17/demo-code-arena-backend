package com.vikas.demo.controllers;


import com.vikas.demo.entity.User;
import com.vikas.demo.repository.UserRepository;
import com.vikas.demo.service.ContestServices;
import com.vikas.demo.service.UserProfileServices;
import com.vikas.demo.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {
    @Autowired
    private ContestServices contestServices;

    @Autowired
    private UserProfileServices userProfileServices;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/health-check")
    public String healthCheck(){
        return "Its working! ok ";
    }

    @GetMapping("/all-contests")
    private ResponseEntity<?> getContest(){
        return contestServices.getAllContests();
    }

    @GetMapping("/profiles/{user_name}")
    private ResponseEntity<?> getUserProfile(@PathVariable String user_name){
        return userProfileServices.getUserProfile(user_name);
    }

    @GetMapping("/login")
    private ResponseEntity<?> login(Authentication authentication){
        String username=authentication.getName();

        User user=userRepository.findByUserName(username);

        return new ResponseEntity<>(user, HttpStatus.OK);

    }
}
