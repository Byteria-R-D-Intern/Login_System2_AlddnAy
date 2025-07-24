package com.example.Login_System2.infrastructure.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import com.example.Login_System2.application.service.JwtProvider;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.security.Key;

@Component
public class Jwtutil implements JwtProvider {

    private final String SECRET_KEY = "my-super-secret-key-which-should-be-long-enough";
    private final long EXPIRATION_TIME = 1000 * 60 * 15 ;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    @Override
    public String generateToken(int userId , String role){
        Map<String, Object> claims = new HashMap<>();
        claims.put("id",userId);
        claims.put("role", role);

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(String.valueOf(userId))
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
            
    }
    @Override
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    @Override
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            // Token geçersizse, süresi dolmuş kabul edebilirsin veya false dönebilirsin
            return true;
        }
    }

    @Override
    public int extractUserId(String token){
        Claims claims = extractAllClaims(token);
        return (int) claims.get("id");
    }

    @Override
    public String extractUserRole(String token){
        Claims claims = extractAllClaims(token);
        return (String) claims.get("role");
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }   
    
}
