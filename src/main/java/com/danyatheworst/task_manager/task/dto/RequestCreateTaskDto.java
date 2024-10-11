package com.danyatheworst.task_manager.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestCreateTaskDto {
    @NotBlank(message = "Invalid task title")
    @Size(min = 1, max = 50, message = "Task title should be between 1 and 50 characters")
    private String title;

    public RequestCreateTaskDto(String title) {
        this.title = title.trim();
    }
}