package com.vikas.demo.controllers;

import com.vikas.demo.entity.UserBadges;
import com.vikas.demo.service.UserAchievementsServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/badge")
@Tag(name = "ALl badges API's" ,description = "This is to register ,modify and delete badges")
public class UserBadgesController {

    @Autowired
    private UserAchievementsServices userAchievementsServices;

    @GetMapping
    private ResponseEntity<?> getAllBadges(){
        return userAchievementsServices.allBadges();
    }

    @PostMapping("/addMultiple")
    private ResponseEntity<?> addMultipleBadges(@RequestBody List<UserBadges> newBadgesGroup){
        return userAchievementsServices.addMultipleBadges(newBadgesGroup);
    }

    @PostMapping
    private ResponseEntity<?> addBadge(@RequestBody UserBadges userBadges){
        return userAchievementsServices.addUserBadges(userBadges);
    }
}
