package com.danyatheworst.task_manager.auth;

import com.danyatheworst.task_manager.auth.dto.RequestJwtRefreshDto;
import com.danyatheworst.task_manager.auth.dto.RequestSignInDto;
import com.danyatheworst.task_manager.auth.dto.RequestSignUpDto;
import com.danyatheworst.task_manager.auth.dto.ResponseJwtDto;
import com.danyatheworst.task_manager.auth.jwt.JwtService;
import com.danyatheworst.task_manager.exceptions.InvalidCredentialsException;
import com.danyatheworst.task_manager.exceptions.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody @Valid RequestSignUpDto payload) {
        this.registrationService.handleNewUser(payload);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ResponseJwtDto> signIn(
            @RequestBody @Valid RequestSignInDto payload,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        ResponseJwtDto responseJwtDto = this.authenticationService.authenticate(payload);
        return ResponseEntity.status(HttpStatus.OK).body(responseJwtDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseJwtDto> refresh(
            @RequestBody @Valid RequestJwtRefreshDto payload,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        try {
            this.jwtService.validate(payload.getRefreshToken());
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(e.getMessage());
        }
        String accessToken = this.jwtService.generateAccessTokenFromRefreshToken(payload.getRefreshToken());
        ResponseJwtDto responseJwtDto = new ResponseJwtDto(accessToken, payload.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(responseJwtDto);
    }
}