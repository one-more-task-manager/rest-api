package com.danyatheworst.task_manager.todolist;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestUpdateTodolistDto {
    @Size(min = 1, max = 50, message = "Invalid todolist title")
    private String title;

    public RequestUpdateTodolistDto(String title) {
        this.title = title.trim();
    }
}