package com.vikas.demo.service;

import com.vikas.demo.entity.ProblemDetails;
import com.vikas.demo.repository.ProblemDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProblemDetailsServices {

    @Autowired
    private ProblemDetailsRepository problemDetailsRepository;

    // Fetch details linked to the main problem's ID
    public Optional<ProblemDetails> getDetailsByProblemId(ObjectId problemId) {
        return problemDetailsRepository.findByProblemId(problemId);
    }

    // Insert a single ProblemDetails document
    public Boolean saveProblemDetails(ProblemDetails details) {
        try {
            problemDetailsRepository.save(details);
            return true;
        } catch (Exception e) {
            log.error("Error saving problem details: ", e);
            return false;
        }
    }

    // Insert multiple ProblemDetails documents at once (Bulk Insert)
    public Boolean saveMultipleProblemDetails(List<ProblemDetails> detailsList) {
        try {
            problemDetailsRepository.saveAll(detailsList);
            return true;
        } catch (Exception e) {
            log.error("Error saving multiple problem details: ", e);
            return false;
        }
    }
}