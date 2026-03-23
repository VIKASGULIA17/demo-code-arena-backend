package com.vikas.demo.service;

import com.vikas.demo.entity.User;
import com.vikas.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if (user != null) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUserName())
                    .password(user.getPassword()) // Plain text from DB
                    .roles(user.getRole().toArray(new String[0])) // e.g., "ADMIN", "USER"
                    .build();
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }

    public UserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(new org.bson.types.ObjectId(userId));
        if (user != null) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUserId().toString())
                    .password(user.getPassword())
                    .roles(user.getRole().toArray(new String[0]))
                    .build();
        }
        throw new UsernameNotFoundException("User not found with id: " + userId);
    }



}
