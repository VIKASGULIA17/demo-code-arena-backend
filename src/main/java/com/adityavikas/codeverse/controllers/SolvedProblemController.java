package com.adityavikas.codeverse.controllers;
import com.adityavikas.codeverse.repository.SolvedProblemRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/solvedProblem")
@AllArgsConstructor
public class SolvedProblemController {

    private final SolvedProblemRepository solvedProblemRepository;



}
