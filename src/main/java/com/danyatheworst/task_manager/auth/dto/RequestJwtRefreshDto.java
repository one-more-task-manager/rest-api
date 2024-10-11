package com.danyatheworst.task_manager.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RequestJwtRefreshDto {
    @NotNull
    private String refreshToken;
}
