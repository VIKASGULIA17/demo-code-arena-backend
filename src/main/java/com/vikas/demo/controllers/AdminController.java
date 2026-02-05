package com.vikas.demo.controllers;


import com.vikas.demo.entity.ContestEntity;
import com.vikas.demo.entity.User;
import com.vikas.demo.service.ContestServices;
import com.vikas.demo.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserServices userServices;
    @Autowired
    private ContestServices contestServices;


    //get all the users
    @GetMapping("/users")
    public List<User> getUser(){
        return userServices.getAllUsers();
    }

    //create a contest
    @PostMapping("/create-contest")
    private ResponseEntity<?> createContest(@RequestBody ContestEntity contestEntity){
        return contestServices.addContest(contestEntity);
    }


    //delete a contest
    @DeleteMapping("/deleteContest/{contestName}")
    private ResponseEntity<?> deleteContest(@PathVariable String contestName){
        return contestServices.deleteContestByName(contestName);
    }


    //edit a contest
    @PutMapping("/modifyContest/{contestName}")
    private ResponseEntity<?> modifyContest(@PathVariable String contestName,@RequestBody ContestEntity contestEntity){
        return contestServices.modifyContestByName(contestName,contestEntity);
    }
}
