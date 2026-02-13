package com.vikas.demo.service;

import com.vikas.demo.entity.User;
import com.vikas.demo.entity.UserProfile;
import com.vikas.demo.repository.UserProfileRepository;
import com.vikas.demo.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserProfileServices {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> createUserprofile(UserProfile userProfile, User user) {
        if(user!=null){
            userProfile.setUserName(user.getUserName());
            userProfileRepository.save(userProfile);
            return new ResponseEntity<>("User Profile saved", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("No user found",HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> updateProfile(UserProfile incomingChanges,User user){
        try{
            if(user!=null){

                UserProfile existingProfile=userProfileRepository.findByUserName(user.getUserName());

                if(existingProfile==null){
                    return createUserprofile(incomingChanges,user);
                }
                else{
                    if (incomingChanges.getFull_name() != null) {
                        existingProfile.setFull_name(incomingChanges.getFull_name());
                    }
                    if (incomingChanges.getBio() != null) {
                        existingProfile.setBio(incomingChanges.getBio());
                    }
                    if (incomingChanges.getAvatar_link() != null) {
                        existingProfile.setAvatar_link(incomingChanges.getAvatar_link());
                    }
                    if (incomingChanges.getSchool_name() != null) {
                        existingProfile.setSchool_name(incomingChanges.getSchool_name());
                    }
                    if (incomingChanges.getCountry() != null) {
                        existingProfile.setCountry(incomingChanges.getCountry());
                    }
                    if (incomingChanges.getWebsite_link() != null) {
                        existingProfile.setWebsite_link(incomingChanges.getWebsite_link());
                    }

                    if (incomingChanges.getOverall_rank() != 0) {
                        existingProfile.setOverall_rank(incomingChanges.getOverall_rank());
                    }
                }
                userProfileRepository.save(existingProfile);
                return new ResponseEntity<>("User profile updated",HttpStatus.OK);
            }else{
                return new ResponseEntity<>("No user found",HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> getUserProfile(String username){
        try{
            User user=userRepository.findByUserName(username);


            UserProfile userProfile=userProfileRepository.findByUserName(username);


            if(userProfile!=null){
                return new ResponseEntity<>(userProfile,HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("User Not Found",HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
