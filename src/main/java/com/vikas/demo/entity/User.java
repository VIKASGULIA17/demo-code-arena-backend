package com.vikas.demo.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "users")
public class User {

    @Id
    private ObjectId userId;

    @Indexed(unique = true)
    @NotNull
    private String userName;

    @NotNull
    private String emailId;
    private String password;
    private List<String> role;
    private List<ObjectId> registeredContest=new ArrayList<>();
    private LocalDateTime createdAt;

}
