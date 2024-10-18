package com.danyatheworst.task_manager.user;

import com.danyatheworst.task_manager.auth.dto.JwtUserDetailsDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@RequestMapping("/users/")
public class UserController {
    @GetMapping("me")
    public ResponseEntity<JwtUserDetailsDto> get(
            @AuthenticationPrincipal JwtUserDetailsDto user
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
