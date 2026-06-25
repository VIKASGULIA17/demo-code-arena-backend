package com.adityavikas.codeverse.services;

import com.adityavikas.codeverse.dto.ContestLeaderBoardDTO;
import com.adityavikas.codeverse.dto.ContestLeaderBoardResponseDTO;
import com.adityavikas.codeverse.dto.SolvedProblemDTO;
import com.adityavikas.codeverse.entity.ContestLeaderBoardEntity;
import com.adityavikas.codeverse.entity.SolvedProblemEntity;
import com.adityavikas.codeverse.repository.ContestLeaderBoardRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ContestLeaderboardServices {

    private static final Logger logger = LoggerFactory.getLogger(ContestLeaderboardServices.class);

    private final ContestLeaderBoardRepository contestLeaderBoardRepository;

    private final ModelMapper modelMapper;


    public ResponseEntity<?> getContestLeaderboard(ObjectId contestId){

        try{

            if(contestId==null)return new ResponseEntity<>("Contest Id doesnt Exist",HttpStatus.BAD_REQUEST);




            List<ContestLeaderBoardEntity> currentResult= contestLeaderBoardRepository.findByContestIdOrderByTotalScoreDescTotalTimeAsc(contestId);


                List<ContestLeaderBoardResponseDTO> response=currentResult.stream().map(entity -> modelMapper.map(entity,ContestLeaderBoardResponseDTO.class)).collect(Collectors.toList());

            return new ResponseEntity<>(response,HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error occured while fetching contest");
            return new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    public boolean createContestLeaderboard(ContestLeaderBoardDTO userResponse){

        try{

            ContestLeaderBoardEntity leaderBoardData=modelMapper.map(userResponse,ContestLeaderBoardEntity.class);

            List<SolvedProblemDTO> solvedProblemEntity=userResponse.getSolvedProblems();
//            solvedProblemEntity.set

            //to set score i'll have to extract data from solved problem
//            leaderBoardData.setTotalScore();



            return true;

        } catch (Exception e) {
            logger.error("Error occurred while creating leaderboard");
            return false;
        }


    }

}
