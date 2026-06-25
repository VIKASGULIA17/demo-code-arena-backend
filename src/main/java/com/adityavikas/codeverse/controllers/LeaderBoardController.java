package com.adityavikas.codeverse.controllers;

import com.adityavikas.codeverse.repository.ContestLeaderBoardRepository;
import com.adityavikas.codeverse.services.ContestLeaderboardServices;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leaderBoard")
@AllArgsConstructor
public class LeaderBoardController {

     private final ContestLeaderboardServices contestLeaderboardServices;

    @GetMapping("/getContestLeaderBoard/{contestId}")
    private ResponseEntity<?> getContestLeaderboard(@RequestBody ObjectId contestId){

//        return contestLeaderboardServices.equals()


        return null;

    }

}
