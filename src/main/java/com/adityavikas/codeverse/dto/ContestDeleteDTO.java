package com.adityavikas.codeverse.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContestDeleteDTO {
    private Boolean shouldDeleteContestProblemToo;
}
