package com.danyatheworst.task_manager.task;

import com.danyatheworst.task_manager.todolist.TodolistService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TaskService {
    private final TodolistService todolistService;
    private final TaskRepository taskRepository;

    public void save(RequestCreateTaskDto payload, Long todolistId, Long userId) {
        this.todolistService.find(todolistId, userId);
        this.taskRepository.save(new Task(payload.getTitle(), todolistId));
    }

}
