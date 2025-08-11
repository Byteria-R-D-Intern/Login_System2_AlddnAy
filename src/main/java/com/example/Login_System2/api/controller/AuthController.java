package com.example.Login_System2.api.controller;

import com.example.Login_System2.api.dto.UserResponse;
import com.example.Login_System2.api.dto.AuthDto.LoginRequest;
import com.example.Login_System2.api.dto.AuthDto.RegisterRequest;
import com.example.Login_System2.application.usecase.UserUseCase;
import com.example.Login_System2.domain.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.Login_System2.infrastructure.Service.Jwtutil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Kullanıcı kimlik doğrulama ve yönetimi için API endpoint'leri")
public class AuthController {

    private final UserUseCase userUseCase;
    private final Jwtutil jwtUtil;
    public AuthController(UserUseCase userUseCase,Jwtutil jwtUtil) {
        this.userUseCase = userUseCase;
        this.jwtUtil = jwtUtil;
    }



    @PostMapping("/register")
    @Operation(
        summary = "Kullanıcı kaydı",
        description = "Yeni kullanıcı kaydı oluşturur ve JWT token döner"
    )
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
    @Operation(
        summary = "Kullanıcı girişi",
        description = "E-posta ve şifre ile kullanıcı girişi yapar ve JWT token döner"
    )
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        return userUseCase.login(email, password)
            .map(token -> ResponseEntity.ok(Map.of("token", token, "message", "Login successful")))
            .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("message", "Invalid credentials")));
    }

    @GetMapping("/users")
    @Operation(summary = "Kullanıcıları listele (ADMIN)")
    public ResponseEntity<?> listUsers(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String role = jwtUtil.extractUserRole(token);
        if (!"ADMIN".equals(role)) return ResponseEntity.status(403).build();

        List<User> users = userUseCase.listAllUsers(role);
        List<UserResponse> responses = users.stream()
            .map(u -> new UserResponse(u.getId(), u.getEmail(), u.getRole().toString()))
            .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Kullanıcı getir (ADMIN)")
    public ResponseEntity<?> getUser(@PathVariable int id,
                                    @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String role = jwtUtil.extractUserRole(token);
        if (!"ADMIN".equals(role)) return ResponseEntity.status(403).build();

        return userUseCase.getUserById(role, id)
            .map(u -> ResponseEntity.ok(new UserResponse(u.getId(), u.getEmail(), u.getRole().toString())))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}")
    @Operation(
        summary = "Kullanıcı güncelleme",
        description = "Kullanıcı bilgilerini günceller. Sadece ADMIN yetkisi gerekir."
    )
    public ResponseEntity<?> updateUser(
        @PathVariable int id,
        @RequestBody User updateUser,
        @RequestHeader("Authorization") String authHeader){

        String token = authHeader.substring(7);
        int requesterId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractUserRole(token);

        if (!"ADMIN".equals(role))
            return ResponseEntity.status(403).body("Yetkisiz erişim !!!");

        return userUseCase.updateUser(requesterId, role, id, updateUser)
            .map(tokenResponse -> ResponseEntity.ok(Map.of("token", tokenResponse, "message", "User updated successfully")))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    @Operation(
        summary = "Kullanıcı silme",
        description = "Kullanıcıyı kalıcı olarak siler. Sadece ADMIN yetkisi gerekir."
    )
    public ResponseEntity<?> deleteUser(
        @PathVariable int id,
        @RequestHeader("Authorization") String authHeader){

        String token = authHeader.substring(7);
        int requesterId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractUserRole(token);

        if (!"ADMIN".equals(role))
            return ResponseEntity.status(403).body("Yetkisiz erişim !!!");

        boolean deleted = userUseCase.deleteUser(requesterId, role, id);

        if(deleted)
            return ResponseEntity.ok("Kullanıcı silindi.");
        else
            return ResponseEntity.notFound().build();

    }
}
