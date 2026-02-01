package com.vikas.demo.controllers;

import java.util.ArrayList;
import java.util.List;

import com.vikas.demo.entity.UserEntity;
import com.vikas.demo.service.SignupServices;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signup")
public class SignupAuthentication {

    @Autowired
    private SignupServices signupServices;

    @GetMapping
    public List<UserEntity> getUser(){

//        signupServic
        return signupServices.getAllUsers();
    }

    @PostMapping
    public UserEntity setUser(@RequestBody UserEntity userEntity){
        signupServices.addUser(userEntity);
        return null;
    }
}
