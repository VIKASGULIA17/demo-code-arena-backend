package com.vikas.demo.controllers;

import com.vikas.demo.entity.ContestEntity;
import com.vikas.demo.service.ContestServices;
import org.springframework.security.core.Authentication;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/contests")
public class ContestController {



    @Autowired
    private ContestServices contestServices;



    @GetMapping("/{contestName}")
    private ResponseEntity<?> getContestInfo(@PathVariable String contestName){

        try{
            ContestEntity contestInfo=contestServices.findByContestName(contestName);
            if(contestInfo!=null){
                return new ResponseEntity<>(contestInfo, HttpStatus.FOUND);
            }else{
                return new ResponseEntity<>("There is no Contest With Name :" +contestName,HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/{contestName}/register")
    public ResponseEntity<?> registerInContest(@PathVariable String contestName, Authentication authentication) {
        // This is the standard Spring Security method
        String username = authentication.getName();

        return contestServices.registerInContest(contestName,username);
    }

}
