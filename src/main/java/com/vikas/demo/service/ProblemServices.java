package com.vikas.demo.service;

import com.vikas.demo.entity.Problem;
import com.vikas.demo.repository.ProblemRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j // Lombok handles the logger automatically now
public class ProblemServices {

    @Autowired
    private ProblemRepository problemRepository;

    public Boolean saveProblem(Problem problem) {
        try {
            problem.setCreated_at(LocalDateTime.now());
            problemRepository.save(problem);
            return true;
        } catch (Exception e) {
            // Logs the exact error trace to your console without crashing
            log.error("Problem not saved due to an error: ", e);
            return false;
        }
    }

    public List<Problem> fetchAllProblems() {
        return problemRepository.findAll();
    }

    public Optional<Problem> fetchProblem(String objectStringId) {
        ObjectId objectId = new ObjectId(objectStringId);
        return problemRepository.findById(objectId);
    }

    public Boolean saveMultipleProblems(List<Problem> problems) {
        try {
            // Set the creation time for every problem in the list
            problems.forEach(problem -> problem.setCreated_at(LocalDateTime.now()));

            // saveAll is a built-in MongoRepository feature for bulk inserts
            problemRepository.saveAll(problems);
            return true;
        } catch (Exception e) {
            log.error("Multiple problems not saved due to an error: ", e);
            return false;
        }
    }
}