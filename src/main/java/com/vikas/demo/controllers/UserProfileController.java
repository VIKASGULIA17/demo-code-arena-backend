package com.vikas.demo.controllers;


import com.vikas.demo.entity.User;
import com.vikas.demo.entity.UserProfile;
import com.vikas.demo.repository.UserRepository;
import com.vikas.demo.service.UserProfileServices;
import com.vikas.demo.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

    @Autowired
    private UserProfileServices userProfileServices;

    @Autowired
    private UserRepository userRepository;



    @PostMapping
    private ResponseEntity<?> createUserProfile(@RequestBody  UserProfile userProfile, Authentication authentication){
        String username=authentication.getName();
        User user=userRepository.findByUserName(username);
        return userProfileServices.createUserprofile(userProfile,user);
    }

    @PatchMapping("/update")
    private ResponseEntity<?> updateUserProfile(@RequestBody UserProfile incomingChanges,Authentication authentication){
        String username=authentication.getName();
        User user=userRepository.findByUserName(username);

        return userProfileServices.updateProfile(incomingChanges,user);
    }
}
