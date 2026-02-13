package com.vikas.demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "UserBadge")
public class UserBadges {

    @Id
    private String id;
    private String name;
    private String iconKey;
    private String description;

}
