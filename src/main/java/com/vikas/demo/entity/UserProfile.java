package com.vikas.demo.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "profile")
public class UserProfile {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
//
    @Indexed(unique = true)
    private String userName;
    private String full_name;
    private String bio;
    private String avatar_link="https://i.pravatar.cc";
    private String school_name="";
    private String country="";
    private String website_link="";
//    @NotNull
    private int overall_rank=99999;
//    @NotNull
    private int contest_rank=99999;
//    private List<UserBadges> badges=new ArrayList<>();
    private List<String> badgeId=new ArrayList<>();
}


