package com.adityavikas.codeverse.middleware;

import com.adityavikas.codeverse.entity.User;
import com.adityavikas.codeverse.repository.UserRepository;
import com.adityavikas.codeverse.services.UserDetailsServiceImpl;
import com.adityavikas.codeverse.services.UserService;
import com.adityavikas.codeverse.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class Middlewares {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  UserService userService;

    public User getUserByJwt(String authorizationHeader){
        try {
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                String jwt = authorizationHeader.substring(7);
                String userId = jwtUtils.extractUserId(jwt);
                if (userId != null && !userId.isEmpty()) {
                    return userService.getUserById(userId);
                }
            }
        } catch (Exception e) {
            // Token is expired, invalid, or user not found
        }
        return null;
    }

    public String getUserIdByJwt(String authorizationHeader){
        try {
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                String jwt = authorizationHeader.substring(7);
                return jwtUtils.extractUserId(jwt);
            }
        } catch (Exception e) {
            // Token is expired or invalid
        }
        return null;
    }

    public String getUserNameByJwt(String authorizationHeader){
        try {
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                String jwt = authorizationHeader.substring(7);
                String userId = jwtUtils.extractUserId(jwt);
                if (userId != null && !userId.isEmpty()) {
                    User user = userService.getUserById(userId);
                    if (user != null) {
                        return user.getUsername();
                    }
                }
            }
        } catch (Exception e) {
            // Token is expired, invalid, or user not found
        }
        return null;
    }

}
