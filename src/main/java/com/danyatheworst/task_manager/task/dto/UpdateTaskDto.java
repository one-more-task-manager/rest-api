package com.danyatheworst.task_manager.task.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateTaskDto {
    private final Long taskId;
    private final RequestUpdateTaskDto requestPayload;
    private final Long todolistId;
    private final Long userId;
}
