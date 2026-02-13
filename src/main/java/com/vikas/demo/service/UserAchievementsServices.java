package com.vikas.demo.service;

import com.vikas.demo.entity.UserBadges;
import com.vikas.demo.repository.UserBadgesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserAchievementsServices {

    @Autowired
    private UserBadgesRepository userBadgesRepository;

    public ResponseEntity<?> allBadges(){
        try{

        List<UserBadges> badges=userBadgesRepository.findAll();

        if(badges.isEmpty()){
            return new ResponseEntity<>("No badges Found", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(badges,HttpStatus.OK);
        }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> addUserBadges(UserBadges incomingBadgeInfo){
        try{
            userBadgesRepository.save(incomingBadgeInfo);
            return new ResponseEntity<>("Badge successfully added",HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> addMultipleBadges(List<UserBadges> incomingBatchOfBadges){
        try{
            userBadgesRepository.saveAll(incomingBatchOfBadges);
            return new ResponseEntity<>("Badges Group added successfully",HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
