package com.danyatheworst.task_manager.todolist;

import com.danyatheworst.task_manager.exceptions.EntityNotFoundException;
import com.danyatheworst.task_manager.exceptions.ForbiddenException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@AllArgsConstructor
@Service
public class TodolistService {
    private final TodolistRepository todolistRepository;

    public List<Todolist> findAll(Long userId) {
        return this.todolistRepository.findAllByUserId(userId);
    }

    public Todolist findAndCheckOwnership(Long id, Long userId) {
        Todolist todolist = this.todolistRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("todolist with id " + id + " does not exist"));
        boolean belongs = todolist.getUserId().equals(userId);
        if (!belongs) {
            throw new ForbiddenException("todolist with id " + id + " does not belong to user with id" + userId);
        }
        return todolist;
    }

    public void save(RequestCreateTodolistDto payload, Long userId) {
        this.todolistRepository.save(new Todolist(payload.getTitle(), userId));
    }

    public void update(Long id, RequestUpdateTodolistDto payload, Long userId) {
        Todolist todolist = this.findAndCheckOwnership(id, userId);
        todolist.setTitle(payload.getTitle());
        this.todolistRepository.save(todolist);
    }

    public void delete(Long id, Long userId) {
        Todolist todolist = this.findAndCheckOwnership(id, userId);
        this.todolistRepository.delete(todolist);
    }
}
