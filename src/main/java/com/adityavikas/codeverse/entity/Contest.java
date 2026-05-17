package com.adityavikas.codeverse.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "contest")
@NoArgsConstructor
public class Contest {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId contestId;

    @NonNull
    private String contestName;

    private String contestDescription;
    private LocalDateTime startTime;
    private int duration;
    private LocalDateTime endTime; //necesary for (1- finding leaderboard,flipping status in problem)
//    @DBRef
    private List<ObjectId> registeredUsers = new ArrayList<>();
    @Transient
    private String contestStatus;
    private LocalDateTime createdAt;
    private Boolean leaderBoardGenerated=false; //it will flip after the contest if over

    @JsonGetter("contestStatus")
    public String getContestStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) return "Upcoming";
        else if (now.isBefore(endTime)) return "Ongoing";
        else return "Finished";
    }

    private List<ObjectId> editorAccessId=new ArrayList<>();


}