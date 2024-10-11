package com.danyatheworst.task_manager.todolist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestCreateTodolistDto {
    @NotBlank(message = "Invalid todolist title")
    @Size(min = 1, max = 50, message = "Invalid todolist title")
    private String title;

    public RequestCreateTodolistDto(String title) {
        this.title = title.trim();
    }
}
