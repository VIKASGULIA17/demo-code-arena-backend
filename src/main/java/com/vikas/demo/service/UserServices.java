package com.vikas.demo.service;

import com.vikas.demo.entity.User;
import com.vikas.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserServices {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> addUser(User userData){
        try{
            User userInfo=userRepository.findByUserName(userData.getUserName());

            if(userInfo==null){
                userData.setCreatedAt(LocalDateTime.now());
                userRepository.save(userData);

                return new ResponseEntity<>("User created with \n"+ "UserName: "+userData.getUserName() +"\nEmail id : "+ userData.getEmailId(),HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>("User with username :"+ userData.getUserName() +" already exist ,Create with another userName",HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> findUserByUserName(String userName){
        try{
            User userInfo=userRepository.findByUserName(userName);

            if(userInfo!=null){
                return new ResponseEntity<>(userInfo, HttpStatus.FOUND);
            }else{
                return  new ResponseEntity<>("User with Name: "+userName +" Not found",HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //to get all the users
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }


}



//controller -> service -> repository -> userEntity