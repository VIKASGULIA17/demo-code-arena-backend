package com.vikas.demo.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "problem_details")
@Data
public class ProblemDetails {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    // THIS IS THE LINK TO YOUR MAIN PROBLEM
    @Indexed(unique = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId problemId;

    // The heavy Markdown text
    private String description;
    private String editorial; // The approaches/solution text

    // The code maps (Language -> Code String)
    private Map<String, String> templates;
    private Map<String, String> solutions;
}