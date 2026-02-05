package com.vikas.demo.controllers;


import com.vikas.demo.service.ContestServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {
    @Autowired
    private ContestServices contestServices;

    @GetMapping("/health-check")
    public String healthCheck(){
        return "Its working! ok ";
    }

    @GetMapping("/all-contests")
    private ResponseEntity<?> getContest(){
        return contestServices.getAllContests();
    }
}
