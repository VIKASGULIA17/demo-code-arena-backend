package com.adityavikas.codeverse.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "driverCode")
public class DriverCode {

    @Id
    private ObjectId driverCodeId;

    @Indexed(unique = true)
    private String category;

    private String javascript;
    private String python;
    private String cpp;
    private String java;


}
