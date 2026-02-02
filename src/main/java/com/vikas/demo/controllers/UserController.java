package com.vikas.demo.controllers;

import com.vikas.demo.dto.APIResponseDTO;
import com.vikas.demo.entity.User;
import com.vikas.demo.repository.UserRepository;
import com.vikas.demo.service.UserService;
import com.vikas.demo.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@Tag(name = "All User API's",description = "This is the user controller used to execute all user functionalities after authenticating through" +
        "the JWT token getting by successful login")
@RequestMapping("/user")
public class UserController {


    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "to check working based on authentication token")
    @GetMapping("/isWorking")
    public ResponseEntity<?> isWorking(){
        return ResponseEntity.ok("It is working !");
    }

    @Operation(summary = "this is used to retrieve the current user details using jwt token")
    @GetMapping("/currentUser")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest httpServletRequest){
        try{
            String authorizationHeader = httpServletRequest.getHeader("authorization");
            User user = null;

            if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){
                String jwt = authorizationHeader.substring(7);
                String username = jwtUtils.extractUsername(jwt);
                user = userRepository.findByUsername(username);
            }else{
                throw new Exception("Invalid Jwt token");
            }

            APIResponseDTO apiResponse = new APIResponseDTO(user.getUserName(),user.getEmailId(),user.getRole());

            return new ResponseEntity<>(apiResponse,HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>("jwt token is incorrect",HttpStatus.BAD_REQUEST);
        }
    }


}
