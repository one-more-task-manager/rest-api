package com.danyatheworst.task_manager.task;

import com.danyatheworst.task_manager.exceptions.EntityNotFoundException;
import com.danyatheworst.task_manager.exceptions.ForbiddenException;
import com.danyatheworst.task_manager.todolist.TodolistService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@AllArgsConstructor
@Service
public class TaskService {
    private final TodolistService todolistService;
    private final TaskRepository taskRepository;

    public List<Task> findAllByTodolistId(Long todolistId, Long userId) {
        this.todolistService.findAndCheckOwnership(todolistId, userId);

        return this.taskRepository.findAllByTodolistId(todolistId);
    }

    public void save(RequestCreateTaskDto payload, Long todolistId, Long userId) {
        this.todolistService.findAndCheckOwnership(todolistId, userId);
        this.taskRepository.save(new Task(payload.getTitle(), todolistId));
    }

    public void delete(Long id, Long todolistId, Long userId) {
        this.todolistService.findAndCheckOwnership(todolistId, userId);

        Task task = this.taskRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("task with id " + id + " does not exist"));
        boolean belongs = task.getTodolistId().equals(todolistId);
        if (!belongs) {
            throw new ForbiddenException("task with id " + id + " does not belong to user with id" + userId);
        }
        this.taskRepository.deleteById(id);
    }
}
