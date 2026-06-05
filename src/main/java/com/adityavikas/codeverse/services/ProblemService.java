package com.adityavikas.codeverse.services;

import com.adityavikas.codeverse.dto.ContestProblemDTO;
import com.adityavikas.codeverse.dto.ContestProblemResponseDTO;
import com.adityavikas.codeverse.dto.ProblemDTO;
import com.adityavikas.codeverse.dto.TestcaseDTO;
import com.adityavikas.codeverse.entity.Contest;
import com.adityavikas.codeverse.entity.Problem;
import com.adityavikas.codeverse.entity.ProblemDetails;
import com.adityavikas.codeverse.entity.Testcase;
import com.adityavikas.codeverse.repository.ContestRepository;
import com.adityavikas.codeverse.repository.ProblemDetailRepository;
import com.adityavikas.codeverse.repository.ProblemRepository;
import com.adityavikas.codeverse.repository.TestcaseRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProblemService {

    private static final Logger logger = LoggerFactory.getLogger(ProblemService.class);


    private final ProblemRepository problemRepository;

    private final ModelMapper modelMapper;

    private final TestcaseRepository testcaseRepository;

    private final ProblemDetailService problemDetailService;

    private final TestcaseService testcaseService;

    private final ContestRepository contestRepository;

    private final ProblemDetailRepository problemDetailRepository;

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

    public Optional<Problem> fetchProblem(String objectStringId){
        ObjectId objectId = new ObjectId(objectStringId);
        return problemRepository.findById(objectId);
    }

    public boolean deleteProblem(String problemId){
        ObjectId objectId = new ObjectId(problemId);
        try{
            problemRepository.deleteById(objectId);
            return true;
        }
        catch (Exception e) {
            logger.error("testcase not deleted");
            return false;
        }
    }

    @Transactional
    public boolean deleteCompleteProblem(String problemId){
        try{
            // from problem
            boolean isDeleted1 = deleteProblem(problemId);
            // from testcase collection
            boolean isDeleted2 = testcaseService.deleteTestcase(problemId);
            // from problemDetails collection
            boolean isDeleted3 = problemDetailService.deleteProblemDetails(problemId);

            return isDeleted2 && isDeleted1 && isDeleted3;
        }
        catch(Exception e){
            logger.error("problem not deleted");
            return false;
        }
    }

    public ObjectId getProblemIdBySlugName(String slug){
        try{
            return problemRepository.findBySlug(slug).getId();
        }
        catch (Exception e){
            logger.error("Problem Id not fetched by slug name");
            return null;
        }
    }

    @Transactional
    public boolean addEntireProblem(ProblemDTO problemDTO) {
        try {
            Problem problem = modelMapper.map(problemDTO, Problem.class);
            problem.setTopicTags(Arrays.stream(problemDTO.getTopicTags().split(",")).toList());
            problem.setContestId(null);
            problem.setIsContestProblem(false);
            problem.setProblemOrder(0);
            problem.setIsVisible(true);
            problem.setCreated_at(LocalDateTime.now());

            Boolean isProblemSaved = saveProblem(problem);

            ObjectId problemId = getProblemIdBySlugName(problemDTO.getSlug());

            boolean isAllTestcaseSaved = true;
            for (TestcaseDTO testcaseDTO : problemDTO.getTestCases()) {
                Testcase testcase = modelMapper.map(testcaseDTO, Testcase.class);
                boolean isTestcaseSaved = testcaseService.addTestcase(testcase, problemId.toString());
                isAllTestcaseSaved = isAllTestcaseSaved && isTestcaseSaved;
            }

            ProblemDetails problemDetails = modelMapper.map(problemDTO, ProblemDetails.class);
            problemDetails.setProblemId(problemId);

            boolean isProblemDetailsSaved = problemDetailService.problemDetailsAdded(problemDetails);

            if (!isProblemDetailsSaved || !isAllTestcaseSaved) {
                logger.error("ProblemDetails or testcases failed, rolling back problem");
                problemRepository.deleteById(problemId);
                problemRepository.deleteById(problemId);
                testcaseRepository.deleteByProblemId(problemId);
                return false;
            }

            return isProblemDetailsSaved && isAllTestcaseSaved && isProblemSaved;


        } catch (Exception e) {
            logger.error("Problem not added completely", e);
            return false;
        }
    }
    public boolean addContestProblem(ObjectId contestId,ContestProblemDTO contestProblemDTO) {

        try {
            Contest contest = contestRepository.findById(contestId).orElse(null);
            if (contest == null) return false;

            Problem problem = modelMapper.map(contestProblemDTO, Problem.class);
            problem.setContestId(contestId);
            problem.setIsContestProblem(true);
            problem.setIsVisible(false);

            Boolean isProblemSaved = saveProblem(problem);
            ObjectId problemId = getProblemIdBySlugName(contestProblemDTO.getSlug());

            boolean isAllTestcaseSaved = true;
            for (TestcaseDTO testcaseDTO : contestProblemDTO.getTestCases()) {
                Testcase testcase = modelMapper.map(testcaseDTO, Testcase.class);
                boolean isTestcaseSaved = testcaseService.addTestcase(testcase, problemId.toString());
                isAllTestcaseSaved = isAllTestcaseSaved && isTestcaseSaved;
            }

            ProblemDetails problemDetails = modelMapper.map(contestProblemDTO, ProblemDetails.class);
            problemDetails.setProblemId(problemId);

            boolean isProblemDetailsSaved = problemDetailService.problemDetailsAdded(problemDetails);
            return isProblemDetailsSaved && isAllTestcaseSaved && isProblemSaved;
        } catch (Exception e) {
            logger.error("Problem not added completely", e);
            return false;
        }


    }

    @Transactional
    public boolean updateContestProblem(ObjectId contestId, ObjectId problemId, ContestProblemDTO contestProblemDTO) {
        try {
            Contest contest = contestRepository.findById(contestId).orElse(null);
            if (contest == null) return false;

            Problem existingProblem = problemRepository.findById(problemId).orElse(null);
            if (existingProblem == null || !contestId.equals(existingProblem.getContestId())) return false;

            existingProblem.setSno(contestProblemDTO.getSno());
            existingProblem.setTitle(contestProblemDTO.getTitle());
            existingProblem.setSlug(contestProblemDTO.getSlug());
            if (contestProblemDTO.getTopicTags() != null) {
                existingProblem.setTopicTags(Arrays.stream(contestProblemDTO.getTopicTags().split(",")).toList());
            }
            existingProblem.setDifficulty(contestProblemDTO.getDifficulty());
            existingProblem.setAcceptanceRate((int) contestProblemDTO.getAcceptanceRate());
            existingProblem.setInputType(contestProblemDTO.getInputType());
            existingProblem.setReturnType(contestProblemDTO.getReturnType());
            existingProblem.setFunctionName(contestProblemDTO.getFunctionName());
            existingProblem.setProblemOrder(contestProblemDTO.getProblemOrder());

            problemRepository.save(existingProblem);

            ProblemDetails existingDetails = problemDetailRepository.findByProblemId(problemId);
            if (existingDetails == null) {
                existingDetails = new ProblemDetails();
                existingDetails.setProblemId(problemId);
            }
            existingDetails.setDescription(contestProblemDTO.getDescription());
            existingDetails.setEditorial(contestProblemDTO.getEditorial());
            existingDetails.setTemplates(contestProblemDTO.getTemplates());
            existingDetails.setSolutions(contestProblemDTO.getSolutions());
            existingDetails.setTimeComplexity(contestProblemDTO.getTimeComplexity());
            existingDetails.setSpaceComplexity(contestProblemDTO.getSpaceComplexity());
            existingDetails.setAlgorithmSteps(contestProblemDTO.getAlgorithmSteps());
            problemDetailRepository.save(existingDetails);

            testcaseRepository.deleteByProblemId(problemId);
            boolean isAllTestcaseSaved = true;
            for (TestcaseDTO testcaseDTO : contestProblemDTO.getTestCases()) {
                Testcase testcase = modelMapper.map(testcaseDTO, Testcase.class);
                boolean isTestcaseSaved = testcaseService.addTestcase(testcase, problemId.toString());
                isAllTestcaseSaved = isAllTestcaseSaved && isTestcaseSaved;
            }

            return isAllTestcaseSaved;
        } catch (Exception e) {
            logger.error("Problem not updated completely", e);
            return false;
        }
    }

    public List<ContestProblemResponseDTO> getContestProblems(ObjectId contestId) {
        try {
            List<Problem> problems = problemRepository.findByContestId(contestId);
            List<ContestProblemResponseDTO> response = new ArrayList<>();

            for (Problem problem : problems) {
                ContestProblemResponseDTO dto = modelMapper.map(problem, ContestProblemResponseDTO.class);

                ProblemDetails details = problemDetailRepository.findByProblemId(problem.getId());

                List<Testcase> testcases = testcaseRepository.findAllByProblemId(problem.getId());

                dto.setTestCases(testcases);

                response.add(dto);
            }
            //providing result in sorted order based on contest order
            return response.stream()
                    .sorted(Comparator.comparingInt  (ContestProblemResponseDTO::getProblemOrder))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error fetching contest problems", e);
            return List.of();
        }
    }

    public List<ContestProblemDTO> getContestProblemsForEditor(ObjectId contestId) {
        try {
            List<Problem> problems = problemRepository.findByContestId(contestId);
            List<ContestProblemDTO> response = new ArrayList<>();

            for (Problem problem : problems) {
                ContestProblemDTO dto = modelMapper.map(problem, ContestProblemDTO.class);

                ProblemDetails details = problemDetailRepository.findByProblemId(problem.getId());
                if (details != null) {
                    dto.setDescription(details.getDescription());
                    dto.setEditorial(details.getEditorial());
                    dto.setTemplates(details.getTemplates());
                    dto.setSolutions(details.getSolutions());
                    dto.setTimeComplexity(details.getTimeComplexity());
                    dto.setSpaceComplexity(details.getSpaceComplexity());
                    dto.setAlgorithmSteps(details.getAlgorithmSteps());
                }

                List<TestcaseDTO> testcaseDTOs = testcaseRepository.findAllByProblemId(problem.getId())
                        .stream()
                        .map(testcase -> modelMapper.map(testcase, TestcaseDTO.class))
                        .collect(Collectors.toList());
                dto.setTestCases(testcaseDTOs);

                response.add(dto);
            }

            return response.stream()
                    .sorted(Comparator.comparingInt(ContestProblemDTO::getProblemOrder))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error fetching contest problems", e);
            return List.of();
        }
    }

}
