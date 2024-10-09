package com.example.task_manager.auth;

import com.example.task_manager.auth.dto.RequestSignUpDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final RegistrationService registrationService;

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody @Valid RequestSignUpDto signUpDto) {
        this.registrationService.handleNewUser(signUpDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}