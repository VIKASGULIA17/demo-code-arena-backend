package com.adityavikas.codeverse.controllers;
import com.adityavikas.codeverse.dto.ExecuteRequest;
import com.adityavikas.codeverse.dto.JdoodleResponseDTO;
import com.adityavikas.codeverse.utils.CodeExecutionUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @PostMapping("/runCode")
    private ResponseEntity<?> runUserCode(@RequestBody ExecuteRequest executeRequest){

//        codeExecutionUtil.printKey();

        JdoodleResponseDTO response=codeExecutionUtil.runJdoodleCode(executeRequest);

        if(response!=null){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

    }







//    @PostMapping("/add")
//    private ResponseEntity<?> createDriverCode(@RequestBody DriverCode driverCode){
//
//        Map<String,Boolean> response=new HashMap<>();
//
//        Boolean isDriverCodeAdded=driverCodeService.addDriverCode(driverCode);
//
//        if(isDriverCodeAdded){
//
//            response.put("Driver Code added successfully!",true);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//
//        }
//        else{
//            response.put("Error occurred while saving driver code",false);
//            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
//        }
//
//
//    }



}
