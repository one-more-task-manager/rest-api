package com.danyatheworst.task_manager.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RequestSignUpDto {

    @NotBlank(message = "Login should be between 2 and 50 characters")
    @Email
    @Size(min = 6, max = 50, message = "Login should be between 2 and 50 characters")
    private final String email;

    @Size(min = 6, max = 50, message = "Password should be between 6 and 50 characters")
    private final String password;

    public RequestSignUpDto(String email, String password) {
        this.email = email.trim();
        this.password = password;
    }
}