package com.adityavikas.codeverse.services;

import com.adityavikas.codeverse.dto.ContestDTO;
import com.adityavikas.codeverse.dto.ContestDeleteDTO;
import com.adityavikas.codeverse.dto.EditorAccessDTO;
import com.adityavikas.codeverse.entity.Contest;
import com.adityavikas.codeverse.entity.Problem;
import com.adityavikas.codeverse.entity.User;
import com.adityavikas.codeverse.middleware.Middlewares;
import com.adityavikas.codeverse.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ContestService {

    private final ContestRepository contestRepository;

    private final ProblemRepository problemRepository;

    private final ProblemDetailRepository problemDetailRepository;

    private final TestcaseRepository testcaseRepository;

    private final UserRepository userRepository;

    private final Middlewares middlewares;

    private final UserService userService;

    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(ContestService.class);

    public boolean addContest(ContestDTO contestDTO){
        try{
            Contest contest = modelMapper.map(contestDTO, Contest.class);
            contest.setEndTime(contestDTO.getStartTime().plusMinutes(contestDTO.getDuration()));
            contest.setLeaderBoardGenerated(false);
            contest.setCreatedAt(LocalDateTime.now());
            contestRepository.save(contest);
        } catch (Exception e) {
            log.error("Contest not added",e);
            return false;
        }
        return true;
    }

    public boolean deleteContest(User user, ObjectId contestId, ContestDeleteDTO contestDeleteDTO){
        try{
            boolean isAdmin=user.getRoles().contains("ADMIN");
            Boolean isEditor=contestRepository.existsByContestIdAndEditorAccessIdContaining(contestId,user.getUserId());

            Contest contest = contestRepository.findById(contestId).orElse(null);

            if(!isAdmin && !isEditor){
                return false; // doesnt have permission to delete the contest
            }

            if(contest==null){
                return false;
            }

//            if(contest.getStartTime().isBefore(LocalDateTime.now())){
//                //contest start date has passed
//                return false; //cant delete passed contest
//            }




            if(contestDeleteDTO.getShouldDeleteContestProblemToo()){
                //if true then delete problems too
                List<ObjectId> problemIds = problemRepository.findByContestId(contestId)
                        .stream()
                        .map(Problem::getId)
                        .toList();
                problemDetailRepository.deleteAllByProblemId(problemIds);
                testcaseRepository.deleteAllByProblemId(problemIds);
                problemRepository.deleteAllById(problemIds);
            }

            contestRepository.deleteById(contestId);
            return true;
        }
        catch (Exception e){
            logger.error("contest not deleted due to error",e);
            return false;
        }
    }

    public boolean updateContest(User user,ObjectId contestId,ContestDTO contest){
        try{
            Contest oldContest = contestRepository.findById(contestId).orElse(null);

            if(oldContest==null)return false;

            boolean isAdmin=user.getRoles().contains("ADMIN");
            Boolean isEditor=contestRepository.existsByContestIdAndEditorAccessIdContaining(contestId,user.getUserId());

            if(!isAdmin && !isEditor){
                return false;
            }

            if (contest.getContestName() != null && !contest.getContestName().isEmpty()) {
                oldContest.setContestName(contest.getContestName());
            }
            if (contest.getContestDescription() != null && !contest.getContestDescription().isEmpty()) {
                oldContest.setContestDescription(contest.getContestDescription());
            }

            if (contest.getStartTime() != null) {
                oldContest.setStartTime(contest.getStartTime());
            }
            if (contest.getDuration() != 0) {
                oldContest.setDuration(contest.getDuration());
            }
            oldContest.setEndTime(oldContest.getStartTime().plusMinutes(oldContest.getDuration()));

            contestRepository.save(oldContest);
            return true;


        } catch (Exception e) {
            log.error("contest not updated",e);
            return false;
        }
    }

    @Transactional
    public boolean registerInContest(String contestId,String authorizationHeader){
        ObjectId contestObjectId = new ObjectId(contestId);
        Contest contest = contestRepository.findById(contestObjectId).orElse(null);
        if(contest!=null){
            User user = middlewares.getUserByJwt(authorizationHeader);
            contest.getRegisteredUsers().add(user.getUserId());
            contestRepository.save(contest);
            user.getRegisteredContest().add(contestObjectId);
            userService.saveUser(user);
            return true;
        }
        else{
            log.error("Contest does'nt exist");
            return false;
        }
    }

    public List<Contest> getAllContest(){
        try{
            return contestRepository.findAll();
        } catch (Exception e) {
            logger.error("All contests not fetched due to error",e);
            return List.of();
        }
    }

    
    public boolean isRegisterInContest(String contestId,String userId){
        try{
            User user = userRepository.findById(new ObjectId(userId)).orElse(null);

            boolean isRegistered = false;

            if(user!=null){
                isRegistered = user.getRegisteredContest().stream().anyMatch(conn -> conn.equals(new ObjectId(contestId)));
            }
            if(isRegistered) return true;

            return false;
        } catch (Exception e) {
            log.error("Something wrong happened while searching whether user registered in contest or not");
            return false;
        }
    }


    public Boolean giveAccessToEditor(ObjectId contestId, EditorAccessDTO editorAccessDTO) {
        try {
            ObjectId userId = editorAccessDTO.userId();
            User user = userRepository.findById(userId).orElse(null);
            Contest contest = contestRepository.findById(contestId).orElse(null);

            if (user == null || contest == null) return false;

            Boolean isEditor = contestRepository.existsByContestIdAndEditorAccessIdContaining(contestId, userId);
            if (!isEditor) {
                contest.getEditorAccessId().add(userId);
                contestRepository.save(contest);

                if (!user.getRoles().contains("EDITOR")) {
                    user.getRoles().add("EDITOR");
                    userRepository.save(user);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Something wrong happened while giving access to editor", e);
            return false;
        }
    }
}
