package com.example.task_manager.auth;

import com.example.task_manager.auth.dto.JwtUserDetailsDto;
import com.example.task_manager.auth.dto.RequestSignInDto;
import com.example.task_manager.auth.dto.ResponseJwtDto;
import com.example.task_manager.auth.jwt.JwtService;
import com.example.task_manager.exceptions.InvalidCredentialsException;
import com.example.task_manager.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public ResponseJwtDto authenticate(RequestSignInDto payload) {
        try {
            Authentication auth = new UsernamePasswordAuthenticationToken(payload.getUsername(), payload.getPassword());
            Authentication authResult = this.authenticationManager.authenticate(auth);
            User user = (User) authResult.getPrincipal();
            JwtUserDetailsDto jwtUserDetailsDto = new JwtUserDetailsDto(
                    user.getId(), user.getUsername(), user.getAuthorities()
            );
            String access = this.jwtService.generateAccessToken(jwtUserDetailsDto);
            String refresh = this.jwtService.generateRefreshToken(jwtUserDetailsDto);
            return new ResponseJwtDto(access, refresh);
        } catch (InternalAuthenticationServiceException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }
}