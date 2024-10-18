package com.danyatheworst.task_manager.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RequestSignInDto {

    @NotBlank(message = "Invalid username or password")
    @Size(min = 6, max = 50, message = "Invalid email or password")
    @Email
    private final String email;

    @Size(min = 6, max = 50, message = "Invalid email or password")
    private final String password;

    public RequestSignInDto(String email, String password) {
        this.email = email.trim();
        this.password = password;
    }
}