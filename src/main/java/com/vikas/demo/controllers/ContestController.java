package com.vikas.demo.controllers;

import com.vikas.demo.entity.ContestEntity;
import com.vikas.demo.service.ContestServices;
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

    @PostMapping
    private ResponseEntity<?> createContest(@RequestBody ContestEntity contestEntity){
        return contestServices.addContest(contestEntity);
    }

    @GetMapping
    private ResponseEntity<?> getContest(){
        return contestServices.getAllContests();
    }

    @GetMapping("{contestId}")
    private ResponseEntity<?> getContestInfo(@PathVariable ObjectId contestId){
        try{
            Optional<ContestEntity> contestInfo=contestServices.getContestInfo(contestId);
            if(contestInfo.isPresent()){
                return new ResponseEntity<>(contestInfo, HttpStatus.FOUND);
            }else{
                return new ResponseEntity<>("There is no Contest With ID :" +contestId,HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{contestId}")
    private ResponseEntity<?> deleteContest(@PathVariable ObjectId contestId){
        return contestServices.deleteContestById(contestId);
    }

    @PutMapping("{contestId}")
    private ResponseEntity<?> modifyContest(@PathVariable ObjectId contestId,@RequestBody ContestEntity contestEntity){
        return contestServices.modifyContestById(contestId,contestEntity);
    }
}
