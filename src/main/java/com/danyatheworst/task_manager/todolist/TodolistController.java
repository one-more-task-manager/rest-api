package com.danyatheworst.task_manager.todolist;

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
@RequestMapping("/todolists")
public class TodolistController {
    private final TodolistService todolistService;

    @GetMapping("")
    public ResponseEntity<List<Todolist>> get(
            @AuthenticationPrincipal JwtUserDetailsDto user
    ) {
        List<Todolist> todolists = this.todolistService.findAll(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(todolists);
    }

    @GetMapping("{id}")
    public ResponseEntity<Todolist> get(
            @PathVariable @NotNull @Positive Long id,
            @AuthenticationPrincipal JwtUserDetailsDto user
    ) {
        Todolist todolist = this.todolistService.findAndCheckOwnership(id, user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(todolist);
    }

    @PostMapping("")
    public ResponseEntity<Void> create(
            @RequestBody @Valid RequestCreateTodolistDto payload,
            @AuthenticationPrincipal JwtUserDetailsDto user
    ) {
        this.todolistService.save(payload, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<Void> update(
            @PathVariable @NotNull @Positive Long id,
            @RequestBody @Valid RequestUpdateTodolistDto payload,
            @AuthenticationPrincipal JwtUserDetailsDto user
    ) {
        this.todolistService.update(id, payload, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(
            @PathVariable @NotNull @Positive Long id,
            @AuthenticationPrincipal JwtUserDetailsDto user
    ) {
        this.todolistService.delete(id, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
