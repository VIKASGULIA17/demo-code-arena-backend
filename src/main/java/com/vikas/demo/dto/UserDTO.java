package com.vikas.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserDTO {

    @Schema(description = "the user's username")
    private String username;
    private String password;

}
