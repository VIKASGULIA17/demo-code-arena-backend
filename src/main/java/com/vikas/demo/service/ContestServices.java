package com.vikas.demo.service;

import com.vikas.demo.entity.ContestEntity;
import com.vikas.demo.entity.User;
import com.vikas.demo.repository.ContestRepository;
import com.vikas.demo.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ContestServices {

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private UserRepository userRepository;

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
    // just in case i need if for it
    public Optional<ContestEntity> getContestInfo(ObjectId contestId){
        return contestRepository.findById(contestId);
    }
    //just in case i want to delete by id
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

    public ResponseEntity<?> deleteContestByName(String contestName){
        try{
            ContestEntity contestInfo=findByContestName(contestName);
            if(contestInfo!=null){
                contestRepository.deleteByContestName(contestName);

                return new ResponseEntity<>("Contest " + contestName +" got delelted",HttpStatus.GONE);
            }else{
                return new ResponseEntity<>("Contest with name: "+contestName +" Not found in the Database",HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ContestEntity findByContestName(String contestName){
        return contestRepository.findByContestName(contestName);
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

    public ResponseEntity<?> modifyContestByName(String contestName,ContestEntity newContestInfo){
        ContestEntity oldContestInfo = contestRepository.findByContestName(contestName);
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

    public ResponseEntity<?> registerInContest(String contestName,String userName){
        try{
            User user=userRepository.findByUserName(userName);
            ContestEntity contest=contestRepository.findByContestName(contestName);

            if(user==null || contest==null){
                return new ResponseEntity<>("User or Contest Not found",HttpStatus.NOT_FOUND);
            }else{
                if (contest.getRegisteredUserIds() == null) {
                    contest.setRegisteredUserIds(new ArrayList<>());
                }
                if (user.getRegisteredContest() == null) {
                    user.setRegisteredContest(new ArrayList<>());
                }
                ObjectId userId=user.getUserId();
                ObjectId contestId=contest.getContestId();
                if(contest.getRegisteredUserIds().contains(userId)){
                    return new ResponseEntity<>("User already registered for the contest",HttpStatus.CONFLICT);
                }

                contest.getRegisteredUserIds().add(userId);
                user.getRegisteredContest().add(contestId);

                contestRepository.save(contest);
                userRepository.save(user);

                return new ResponseEntity<>("Registered for contest",HttpStatus.OK);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("yohohoh",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}


