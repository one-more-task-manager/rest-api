package com.danyatheworst.task_manager.auth.jwt;

import com.danyatheworst.task_manager.ErrorResponseDto;
import com.danyatheworst.task_manager.auth.dto.JwtUserDetailsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

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
                Claims claims = this.jwtService.validate(accessToken);
                JwtUserDetailsDto jwtUserDetailsDto = this.extractTokenPayload(claims);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        jwtUserDetailsDto,
                        null, jwtUserDetailsDto.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException exception) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                ErrorResponseDto errorResponseDto = new ErrorResponseDto(exception.getMessage());
                response.getWriter().write(this.objectMapper.writeValueAsString(errorResponseDto));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private JwtUserDetailsDto extractTokenPayload(Claims claims) {
        Integer id = (Integer) claims.get("id");
        String username = (String) claims.get("username");

        Collection<Map<String, String>> authoritiesMap = (Collection<Map<String, String>>) claims.get("authorities");

        Collection<? extends GrantedAuthority> authorities = authoritiesMap
                .stream()
                .map(auth -> new SimpleGrantedAuthority(auth.get("authority")))
                .toList();
        return new JwtUserDetailsDto((long) id, username, authorities);
    }
}