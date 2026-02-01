package com.vikas.demo.service;

import com.vikas.demo.entity.ContestEntity;
import com.vikas.demo.repository.ContestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Component
public class ContestServices {

    @Autowired
    private ContestRepository contestRepository;

    public ResponseEntity<?> addContest(ContestEntity contestEntity){
        try {
            contestRepository.save(contestEntity);

            return new ResponseEntity<>(contestEntity, HttpStatus.CREATED);
        }
        catch( Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<?> getAllContests(){

        List<ContestEntity> contestList=contestRepository.findAll();
        if(!contestList.isEmpty()){
            return new ResponseEntity<>(contestList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
