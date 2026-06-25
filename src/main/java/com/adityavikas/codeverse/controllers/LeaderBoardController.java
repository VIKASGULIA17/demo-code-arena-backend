package com.adityavikas.codeverse.controllers;

import com.adityavikas.codeverse.dto.ContestLeaderBoardDTO;
import com.adityavikas.codeverse.repository.ContestLeaderBoardRepository;
import com.adityavikas.codeverse.services.ContestLeaderboardServices;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leaderBoard")
@AllArgsConstructor
public class LeaderBoardController {

     private final ContestLeaderboardServices contestLeaderboardServices;

    @GetMapping("/getContestLeaderBoard/{contestId}")
    private ResponseEntity<?> getContestLeaderboard(@PathVariable ObjectId contestId){

        return contestLeaderboardServices.getContestLeaderboard(contestId);

    }

    @PostMapping("/createLeaderBoard")
    private ResponseEntity<?> createLeaderBoardForContest(@RequestBody ContestLeaderBoardDTO contestLeaderBoardDTO){



        return null;
    }

}
