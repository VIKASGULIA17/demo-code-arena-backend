package com.adityavikas.codeverse.controllers;
import com.adityavikas.codeverse.dto.ExecuteRequest;
import com.adityavikas.codeverse.dto.JdoodleResponseDTO;
import com.adityavikas.codeverse.middleware.Middlewares;
import com.adityavikas.codeverse.utils.CodeExecutionUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/CodeExecution")
@AllArgsConstructor
@Tag(name="run Code Controller (accessed right to All Users)",description = "This contains all the endpoints to handle the execution of code")
public class CodeExecution {

    private final CodeExecutionUtil codeExecutionUtil;

    private final Middlewares middlewares;

    @PostMapping("/runCode")
    private ResponseEntity<?> runUserCode(HttpServletRequest request,@RequestBody ExecuteRequest executeRequest){

        String authenticationHeader=request.getHeader("Authorization");

        if(authenticationHeader==null){
            return new ResponseEntity<>("Login to run Code",HttpStatus.UNAUTHORIZED);
        }

        String username=middlewares.getUserNameByJwt(authenticationHeader);

        if(username == null){
            return new ResponseEntity<>("Login to run Code",HttpStatus.UNAUTHORIZED);
        }

        JdoodleResponseDTO response=codeExecutionUtil.runJdoodleCode(executeRequest);

        if(response!=null){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

    }



}
