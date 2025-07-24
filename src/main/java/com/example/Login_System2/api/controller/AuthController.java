package com.example.Login_System2.api.controller;

import com.example.Login_System2.api.dto.*;
import com.example.Login_System2.application.usecase.UserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserUseCase userUseCase;
    public AuthController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }



    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request ){
        String email = request.getEmail();
        String password = request.getPassword();
        String name = request.getName();
        String surname = request.getSurname();
        String roleStr = request.getRole();
        return userUseCase.register(email,name,surname, password, roleStr)
            .map(token -> ResponseEntity.ok(Map.of("token", token, "message", "User registered successfully")))
            .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("message", "Kayıt başarısız veya kullanıcı zaten mevcut")));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        return userUseCase.login(email, password)
            .map(token -> ResponseEntity.ok(Map.of("token", token, "message", "Login successful")))
            .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("message", "Invalid credentials")));
    }
}
