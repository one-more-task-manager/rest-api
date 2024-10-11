package com.danyatheworst.task_manager.todolist;

import com.danyatheworst.task_manager.auth.dto.JwtUserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/todolists")
public class TodolistsController {
    private final TodolistRepository todolistRepository;

    @GetMapping("")
    public ResponseEntity<List<Todolist>> get(
            @AuthenticationPrincipal JwtUserDetailsDto user
    ) {
        List<Todolist> todolists = this.todolistRepository.findAllByUserId(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(todolists);
    }
}
