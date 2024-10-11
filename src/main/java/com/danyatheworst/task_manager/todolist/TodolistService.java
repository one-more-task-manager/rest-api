package com.danyatheworst.task_manager.todolist;

import com.danyatheworst.task_manager.exceptions.EntityNotFoundException;
import com.danyatheworst.task_manager.exceptions.ForbiddenException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@AllArgsConstructor
@Service
public class TodolistService {
    private final TodolistRepository todolistRepository;

    public void find(Long id, Long userId) {
        Todolist todolist = this.todolistRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("todolist with id " + id + " does not exist"));
        if (!todolist.getUserId().equals(userId)) {
            throw new ForbiddenException("todolist with id " + id + " does not belong to user with " + userId);
        }
    }

    public void save(RequestCreateTodolistDto payload, Long userId) {
        this.todolistRepository.save(new Todolist(payload.getTitle(), userId));
    }

    public void delete(Long id, Long userId) {
        Optional<Todolist> todolist = this.todolistRepository.findById(id);
        if (todolist.isPresent()) {
            if (!todolist.get().getUserId().equals(userId)) {
                throw new ForbiddenException("todolist with id " + id + " does not belong to user with " + userId);
            }
            this.todolistRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("todolist with id " + id + " does not exist");
        }
    }
}
