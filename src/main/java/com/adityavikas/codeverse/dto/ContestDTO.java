package com.adityavikas.codeverse.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ContestDTO {
    private String contestName;
    private String contestDescription;
    private LocalDateTime startTime;
    private int duration;

}