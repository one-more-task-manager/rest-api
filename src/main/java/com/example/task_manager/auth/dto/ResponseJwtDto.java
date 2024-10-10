package com.example.task_manager.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseJwtDto {
    private String accessToken;
    private String refreshToken;
}
