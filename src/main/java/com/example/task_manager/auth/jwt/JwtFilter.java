package com.example.task_manager.auth.jwt;

import com.example.task_manager.ErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null) {
            filterChain.doFilter(request, response);
            return;
        }
        if (bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring(7);
            try {
                this.jwtService.validate(accessToken);
            } catch (JwtException exception) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                ErrorResponseDto errorResponseDto = new ErrorResponseDto(exception.getMessage());
                response.getWriter().write(this.objectMapper.writeValueAsString(errorResponseDto));
                return;
            }
            //get roles from token for @PreAuthorize if it's needed
        }

        filterChain.doFilter(request, response);
    }
}