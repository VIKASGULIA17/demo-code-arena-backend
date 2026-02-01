package com.vikas.demo.service;

import com.vikas.demo.entity.ContestEntity;
import com.vikas.demo.repository.ContestRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ContestServices {

    @Autowired
    private ContestRepository contestRepository;

    public ResponseEntity<?> addContest(ContestEntity contestEntity){
        try {
            contestRepository.save(contestEntity);
            return new ResponseEntity<>(contestEntity, HttpStatus.CREATED);
        }
        catch( Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<?> getAllContests(){
        List<ContestEntity> contestList=contestRepository.findAll();
        if(!contestList.isEmpty()){
            return new ResponseEntity<>(contestList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public Optional<ContestEntity> getContestInfo(ObjectId contestId){
        return contestRepository.findById(contestId);
    }

    public ResponseEntity<?> deleteContestById(ObjectId contestId){
        try{

            Optional<ContestEntity> contest=contestRepository.findById(contestId);
            contestRepository.deleteById(contestId);
            if(contest.isPresent()){
                return new ResponseEntity<>("Contest with id : "+ contestId + " Deleted",HttpStatus.ACCEPTED);
            }else{
                return new ResponseEntity<>("Contest with id : "+ contestId +" Not found",HttpStatus.NOT_FOUND);
            }


        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<?> modifyContestById(ObjectId contestId,ContestEntity newContestInfo){
        ContestEntity oldContestInfo = contestRepository.findById(contestId).orElse(null);
        try{


        if (oldContestInfo == null) {
            return new ResponseEntity<>("Contest not found", HttpStatus.NOT_FOUND);
        }

        if (!newContestInfo.getContestName().isEmpty()) {
            oldContestInfo.setContestName(newContestInfo.getContestName());
        }

        if (!newContestInfo.getContestDescription().isEmpty()) {
            oldContestInfo.setContestDescription(newContestInfo.getContestDescription());
        }

        if (newContestInfo.getDuration() != 0) {
            oldContestInfo.setDuration(newContestInfo.getDuration());
        }

        if (newContestInfo.getStartTime() != null) {
            oldContestInfo.setStartTime(newContestInfo.getStartTime());
        }

        contestRepository.save(oldContestInfo);
        return new ResponseEntity<>(oldContestInfo, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Contest Info Updated!",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
