package com.vikas.demo.service;

import com.vikas.demo.entity.UserEntity;
import com.vikas.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class SignupServices {

    @Autowired
    private UserRepository userRepository;

    public void addUser(UserEntity userEntity){
        userEntity.setCreatedAt(LocalDateTime.now());
        userRepository.save(userEntity);
        return ;
    }

    public List<UserEntity> getAllUsers(){
        return userRepository.findAll();
    }
}



//controller -> service -> repository -> userEntity