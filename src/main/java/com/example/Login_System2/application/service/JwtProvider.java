package com.example.Login_System2.application.service;

public interface JwtProvider {
   public String generateToken(int userId, String Role);
   public boolean validateToken(String Token);
   public int  extractUserId(String Token);
   public String extractUserRole(String Token);
   public boolean isTokenExpired(String Token);

}
