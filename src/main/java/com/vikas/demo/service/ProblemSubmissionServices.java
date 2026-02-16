package com.vikas.demo.service;

import com.vikas.demo.entity.ProblemSubmissionEntity;
import com.vikas.demo.repository.ProblemSubmissionRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class ProblemSubmissionServices {

    @Autowired
    private ProblemSubmissionRepository problemSubmissionRepository;

    public ResponseEntity<?> getAllSubmissionOfUser(String username){
        try{
            List<ProblemSubmissionEntity> problemSubmissionList=problemSubmissionRepository.findByUsername(username);

            if(problemSubmissionList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }else{
                return new ResponseEntity<>(problemSubmissionList,HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<?> getSubmission(ObjectId submissionId){
        try{
            ProblemSubmissionEntity entry=problemSubmissionRepository.findBySubmissionId(submissionId);

            if(entry!=null){
                return new ResponseEntity<>(entry,HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<?> createSubmission(ProblemSubmissionEntity newEntry){

        try{
            if(newEntry!=null){
                newEntry.setSubmittedAt(LocalDateTime.now());
                String shortId = UUID.randomUUID().toString().substring(0, 20);
                newEntry.setSlug(shortId);
                problemSubmissionRepository.save(newEntry);
                return new ResponseEntity<>(newEntry,HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("Submission Failed\n"+ newEntry ,HttpStatus.OK );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
