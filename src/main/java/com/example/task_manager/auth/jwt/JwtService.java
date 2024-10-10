package com.example.task_manager.auth.jwt;

import com.example.task_manager.auth.dto.JwtUserDetailsDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.access-token}")
    private int accessExpirationTimeMillis;

    @Value("${jwt.expiration.refresh-token}")
    private int refreshExpirationTimeMillis;

    public String generateAccessToken(JwtUserDetailsDto jwtUserDetailsDto) {
        Claims claims = Jwts.claims();
        claims.put("id", jwtUserDetailsDto.getId());
        claims.put("username", jwtUserDetailsDto.getUsername());
        claims.put("authorities", jwtUserDetailsDto.getAuthorities());

        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(this.accessExpirationTimeMillis)))
                .signWith(generateKey())
                .compact();
    }

    public String generateRefreshToken(JwtUserDetailsDto jwtUserDetailsDto) {
        Claims claims = Jwts.claims();
        claims.put("id", jwtUserDetailsDto.getId());
        claims.put("username", jwtUserDetailsDto.getUsername());
        claims.put("authorities", jwtUserDetailsDto.getAuthorities());

        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(this.refreshExpirationTimeMillis)))
                .signWith(generateKey())
                .compact();
    }

    public String generateAccessTokenFromRefreshToken(String refreshToken) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(this.generateKey())
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        Long id = claims.get("id", Long.class);
        String username = claims.get("username", String.class);
        Collection<? extends GrantedAuthority> authorities = claims.get("authorities", Collection.class);
        return generateAccessToken(new JwtUserDetailsDto(id, username, authorities));
    }

    public void validate(String token) {
        Jwts
                .parserBuilder()
                .setSigningKey(this.generateKey())
                .build()
                .parseClaimsJws(token);
    }

    private SecretKey generateKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(this.secret));
    }
}
