package com.example.Login_System2.application.service;

public interface PasswordHasher {
    String hash(String password);
    boolean matches(String password, String hashedPassword);
}
