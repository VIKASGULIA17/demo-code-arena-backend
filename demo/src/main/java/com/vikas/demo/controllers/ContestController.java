package com.vikas.demo.controllers;

import com.vikas.demo.entity.ContestEntity;
import com.vikas.demo.service.ContestServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
