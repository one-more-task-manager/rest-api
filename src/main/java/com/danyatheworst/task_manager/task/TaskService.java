package com.danyatheworst.task_manager.task;

import com.danyatheworst.task_manager.exceptions.EntityNotFoundException;
import com.danyatheworst.task_manager.exceptions.ForbiddenException;
import com.danyatheworst.task_manager.task.dto.RequestCreateTaskDto;
import com.danyatheworst.task_manager.task.dto.UpdateTaskDto;
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

    public Task findAndCheckOwnership(Long taskId, Long todolistId) {
        Task task = this.taskRepository
                .findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("task with id " + taskId + " does not exist"));
        boolean belongs = task.getTodolistId().equals(todolistId);
        if (!belongs) {
            throw new ForbiddenException("task with id " + taskId + " does not belong to todolist with id" + todolistId);
        }

        return task;
    }

    public void save(RequestCreateTaskDto payload, Long todolistId, Long userId) {
        this.todolistService.findAndCheckOwnership(todolistId, userId);
        this.taskRepository.save(new Task(payload.getTitle(), todolistId));
    }

    public void update(UpdateTaskDto payload) {
        this.todolistService.findAndCheckOwnership(payload.getTodolistId(), payload.getUserId());
        Task task = this.findAndCheckOwnership(payload.getTaskId(), payload.getTodolistId());

        if (payload.getRequestPayload().getTitle() != null) {
            task.setTitle(payload.getRequestPayload().getTitle());
        }
        if (payload.getRequestPayload().getIsDone() != null) {
            task.setIsDone(payload.getRequestPayload().getIsDone());
        }

        this.taskRepository.save(task);
    }

    public void delete(Long taskId, Long todolistId, Long userId) {
        this.todolistService.findAndCheckOwnership(todolistId, userId);
        this.findAndCheckOwnership(taskId, todolistId);

        this.taskRepository.deleteById(taskId);
    }
}
