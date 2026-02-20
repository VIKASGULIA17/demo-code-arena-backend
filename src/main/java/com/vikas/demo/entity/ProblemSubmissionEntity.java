package com.vikas.demo.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "Submission")
public class ProblemSubmissionEntity {

    @Id
    private ObjectId submissionId;

    @Indexed
    private String  username; //to link
    @Indexed
    private ObjectId problemId; //to link

    private String userCode;
    private String language; //python ,cpp,js,java
    private LocalDateTime submittedAt;
    private Double time;
    private Double memory;
    private String status; //ACCEPTED, WRONG_ANSWER, TIME_LIMIT_EXCEEDED, COMPILATION_ERROR.
    private String slug; // to show/share submission code

}
