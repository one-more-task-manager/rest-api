package com.danyatheworst.task_manager.todolist;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TodolistService {
    private final TodolistRepository todolistRepository;

    public void save(RequestCreateTodolistDto payload, Long userId) {
        this.todolistRepository.save(new Todolist(payload.getTitle(), userId));
    }
}
