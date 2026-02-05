package com.vikas.demo.controllers;

import com.vikas.demo.entity.User;
import com.vikas.demo.service.UserServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/signup")
@Tag(name = "Sign up API's" ,description = "This is to register ,modify and delete users")
public class UserController {

    @Autowired
    private UserServices userServices;
    //getting all users


    @GetMapping("{userName}")
    public ResponseEntity<?> getUserByName(@PathVariable String userName){
        return userServices.findUserByUserName(userName);
    }

    //creating a user
    @PostMapping
    public ResponseEntity<?> setUser(@RequestBody User userEntity){
        return userServices.addUser(userEntity);
    }





}
