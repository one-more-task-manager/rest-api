package com.danyatheworst.task_manager.task;

import com.danyatheworst.task_manager.auth.dto.JwtUserDetailsDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/todolists/{todolistId}/tasks")
public class TaskController {
    private final TaskService taskService;

    @GetMapping("")
    public ResponseEntity<List<Task>> get(
            @PathVariable @NotNull @Positive Long todolistId,
            @AuthenticationPrincipal JwtUserDetailsDto user
    ) {
        List<Task> tasks = this.taskService.findAllByTodolistId(todolistId, user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @PostMapping("")
    public ResponseEntity<Void> create(
            @RequestBody @Valid RequestCreateTaskDto payload,
            @PathVariable @NotNull @Positive Long todolistId,
            @AuthenticationPrincipal JwtUserDetailsDto user
    ) {
        this.taskService.save(payload, todolistId, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("{taskId}")
    public ResponseEntity<Void> delete(
            @PathVariable @NotNull @Positive Long todolistId,
            @PathVariable @NotNull @Positive Long taskId,
            @AuthenticationPrincipal JwtUserDetailsDto user
    ) {
        this.taskService.delete(taskId, todolistId, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
