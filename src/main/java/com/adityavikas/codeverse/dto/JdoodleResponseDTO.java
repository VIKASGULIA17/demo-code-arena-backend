package com.adityavikas.codeverse.dto;

// Response from JDoodle
public record JdoodleResponseDTO(
        String output,
        Integer statusCode,
        String cpuTime,
        String memory,
        String error
) {}