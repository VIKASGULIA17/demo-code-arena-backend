package com.vikas.demo.entity;

import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import lombok.Data;

import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;

}
