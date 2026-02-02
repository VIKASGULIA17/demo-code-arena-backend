package com.vikas.demo.service;

import com.vikas.demo.entity.User;
import com.vikas.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SignupServices {

    @Autowired
    private UserRepository userRepository;

    public void addUser(User userEntity){
        userEntity.setCreatedAt(LocalDateTime.now());
        userRepository.save(userEntity);
        return ;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}



//controller -> service -> repository -> userEntity