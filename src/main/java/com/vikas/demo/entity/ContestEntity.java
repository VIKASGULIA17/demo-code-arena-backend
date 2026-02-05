package com.vikas.demo.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Data;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "contests")
public class ContestEntity {

    @Id
    private ObjectId contestId;

    @NonNull
    private String contestName;
    @NonNull
    private String contestDescription;
    private int duration;
    private LocalDateTime startTime;

    private List<ObjectId> registeredUserIds=new ArrayList<>();


//  private List<ProblemEntity> problemList;
    @Transient
    private String contestStatus;


    @JsonGetter("contestStatus")
    public String getContestStatus(){
        LocalDateTime now=LocalDateTime.now();
        LocalDateTime endTime=startTime.plusMinutes(duration);

        if(now.isBefore(startTime)){
            return "Upcoming";
        }else if(now.isAfter(startTime) && now.isBefore(endTime)){
            return "Ongoing";
        }else{
            return "Finished";
        }
    }


//    contest id ,contest name, contest duration ,contest starting time ,contest registrations
//    ,description,problem list ,prize,status


}
