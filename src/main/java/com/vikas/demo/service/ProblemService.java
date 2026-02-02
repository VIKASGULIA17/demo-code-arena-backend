package com.vikas.demo.service;

import com.vikas.demo.entity.Problem;
import com.vikas.demo.repository.ProblemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProblemService {

    private static final Logger logger = LoggerFactory.getLogger(ProblemService.class);

    @Autowired
    private ProblemRepository problemRepository;

    public Boolean saveProblem(Problem problem){
        try{
            problem.setCreated_at(LocalDateTime.now());
            problemRepository.save(problem);
        } catch (Exception e) {
            logger.error("Problem not saved");
            return false;
        }
        return true;
    }

    public List<Problem> fetchAllProblems(){
        return problemRepository.findAll();
    }

}
