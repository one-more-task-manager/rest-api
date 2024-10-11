package com.danyatheworst.task_manager.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class RequestUpdateTaskDto {
    @NotBlank(message = "Invalid task title")
    @Size(min = 1, max = 50, message = "Invalid task title")
    private String title;

    private Boolean isDone;

    public RequestUpdateTaskDto(String title, boolean isDone) {
        this.title = title.trim();
        this.isDone = isDone;
    }
}
