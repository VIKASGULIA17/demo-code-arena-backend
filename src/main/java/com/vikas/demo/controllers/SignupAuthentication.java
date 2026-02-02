package com.vikas.demo.controllers;

import java.util.List;

import com.vikas.demo.entity.User;
import com.vikas.demo.service.SignupServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signup")
public class SignupAuthentication {

    @Autowired
    private SignupServices signupServices;

    @GetMapping
    public List<User> getUser(){

//        signupServic
        return signupServices.getAllUsers();
    }

    @PostMapping
    public User setUser(@RequestBody User userEntity){
        signupServices.addUser(userEntity);
        return null;
    }
}
