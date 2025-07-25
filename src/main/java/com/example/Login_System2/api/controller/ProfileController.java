package com.example.Login_System2.api.controller;

import org.springframework.web.bind.annotation.*;
import com.example.Login_System2.application.usecase.ProfileUseCase;
import com.example.Login_System2.api.dto.ProfileRequest;
import com.example.Login_System2.api.dto.ProfileResponse;
import com.example.Login_System2.infrastructure.Service.TokenUtil;
import com.example.Login_System2.infrastructure.Service.Jwtutil;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileUseCase profileUseCase;
    private final Jwtutil jwtUtil;
    private final TokenUtil tokenUtil;

    public ProfileController(ProfileUseCase profileUseCase, Jwtutil jwtUtil, TokenUtil tokenUtil) {
        this.profileUseCase = profileUseCase;
        this.jwtUtil = jwtUtil;
        this.tokenUtil = tokenUtil;
    }

    @PostMapping("/profile")
    public ResponseEntity<?> createProfile(@RequestBody ProfileRequest request,
                                           @RequestHeader("Authorization") String authHeader) {
        TokenUtil.TokenValidationStatus status = tokenUtil.validationToken(authHeader, jwtUtil);
        if (status != TokenUtil.TokenValidationStatus.VALID) {
            return ResponseEntity.status(401).body("Token hatası: " + status.name());
        }
        String token = authHeader.replace("Bearer ", "");
        int currentUserId = jwtUtil.extractUserId(token);
        String currentUserRole = jwtUtil.extractUserRole(token);
        ProfileResponse response = profileUseCase.createProfile(request, currentUserId, currentUserRole);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getOwnProfile(@RequestHeader("Authorization") String authHeader) {
        TokenUtil.TokenValidationStatus status = tokenUtil.validationToken(authHeader, jwtUtil);
        if (status != TokenUtil.TokenValidationStatus.VALID) {
            return ResponseEntity.status(401).body("Token hatası: " + status.name());
        }
        String token = authHeader.replace("Bearer ", "");
        int currentUserId = jwtUtil.extractUserId(token);
        String currentUserRole = jwtUtil.extractUserRole(token);
        ProfileResponse response = profileUseCase.getProfileByUserId(currentUserId, currentUserId, currentUserRole);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateOwnProfile(@RequestBody ProfileRequest request, @RequestHeader("Authorization") String authHeader) {
        TokenUtil.TokenValidationStatus status = tokenUtil.validationToken(authHeader, jwtUtil);
        if (status != TokenUtil.TokenValidationStatus.VALID) {
            return ResponseEntity.status(401).body("Token hatası: " + status.name());
        }
        String token = authHeader.replace("Bearer ", "");
        int currentUserId = jwtUtil.extractUserId(token);
        String currentUserRole = jwtUtil.extractUserRole(token);
        ProfileResponse response = profileUseCase.updateProfileByUserId(currentUserId, request, currentUserId, currentUserRole);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteOwnProfile(@RequestHeader("Authorization") String authHeader) {
        TokenUtil.TokenValidationStatus status = tokenUtil.validationToken(authHeader, jwtUtil);
        if (status != TokenUtil.TokenValidationStatus.VALID) {
            return ResponseEntity.status(401).body("Token hatası: " + status.name());
        }
        String token = authHeader.replace("Bearer ", "");
        int currentUserId = jwtUtil.extractUserId(token);
        String currentUserRole = jwtUtil.extractUserRole(token);
        profileUseCase.deleteProfileByUserId(currentUserId, currentUserId, currentUserRole);
        return ResponseEntity.ok("Profil silindi.");
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProfiles(@RequestHeader("Authorization") String authHeader) {
        TokenUtil.TokenValidationStatus status = tokenUtil.validationToken(authHeader, jwtUtil);
        if (status != TokenUtil.TokenValidationStatus.VALID) {
            return ResponseEntity.status(401).body("Token hatası: " + status.name());
        }
        String token = authHeader.replace("Bearer ", "");
        String currentUserRole = jwtUtil.extractUserRole(token);
        return ResponseEntity.ok(profileUseCase.getAllProfiles(currentUserRole));
    }
}
