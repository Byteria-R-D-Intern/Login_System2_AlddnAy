package com.example.Login_System2.api.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import com.example.Login_System2.application.usecase.ProfileUseCase;
import com.example.Login_System2.domain.model.UserProfile;
import com.example.Login_System2.infrastructure.Service.Jwtutil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.Login_System2.api.dto.ProfileDto.UserProfileRequest;
import com.example.Login_System2.api.dto.ProfileDto.UserProfileResponse;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Data;


@RestController
@RequestMapping("/api/user/profile")
@Data
@AllArgsConstructor
@Tag(name = "User Profile Management", description = "Kullanıcı profil yönetimi için API endpoint'leri")
public class ProfileController {

    private final ProfileUseCase profileUseCase;
    private final Jwtutil jwtUtil;


    // CREATE
    @PostMapping("/{userId}")
    @Operation(
        summary = "Profil oluşturma",
        description = "Kullanıcı için yeni profil oluşturur. USER sadece kendi profili için oluşturabilir."
    )
    public ResponseEntity<?> createProfile(
            @PathVariable int userId,
            @RequestBody @Valid UserProfileRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        int requestId = jwtUtil.extractUserId(token);
        String requestRole = jwtUtil.extractUserRole(token);

        UserProfile newProfile = new UserProfile();
        newProfile.setAdres(request.getAdres());
        newProfile.setTelefon(request.getTelefon());
        newProfile.setBirthDate(request.getBirthDate());

        Optional<UserProfile> created = profileUseCase.createProfile(requestId, requestRole, userId, newProfile);

        if (created.isPresent()) {
            UserProfileResponse response = toResponseDTO(created.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(403).body("Profil oluşturma yetkiniz yok veya profil zaten var.");
        }
    }

    // READ
    @GetMapping("/{userId}")
    @Operation(
        summary = "Profil görüntüleme",
        description = "Kullanıcı profilini getirir. USER sadece kendi profilini görebilir."
    )
    public ResponseEntity<?> getProfile(
            @Parameter(description = "Kullanıcı ID'si", example = "1")
            @PathVariable int userId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        int requestId = jwtUtil.extractUserId(token);
        String requestRole = jwtUtil.extractUserRole(token);

        Optional<UserProfile> profile = profileUseCase.getProfile(requestId, requestRole, userId);

        if (profile.isPresent()) {
            UserProfileResponse response = toResponseDTO(profile.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(403).body("Profil görüntüleme yetkiniz yok veya profil bulunamadı.");
        }
    }

    // UPDATE
    @PutMapping("/{userId}")
    @Operation(
        summary = "Profil güncelleme",
        description = "Kullanıcı profilini günceller. USER sadece kendi profilini güncelleyebilir."
    )
    public ResponseEntity<?> updateProfile(
            @Parameter(description = "Kullanıcı ID'si", example = "1")
            @PathVariable int userId,
            @RequestBody @Valid UserProfileRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        int requestId = jwtUtil.extractUserId(token);
        String requestRole = jwtUtil.extractUserRole(token);

        UserProfile updatedProfile = new UserProfile();
        updatedProfile.setAdres(request.getAdres());
        updatedProfile.setTelefon(request.getTelefon());
        updatedProfile.setBirthDate(request.getBirthDate());

        Optional<UserProfile> updated = profileUseCase.updateProfile(requestId, requestRole, userId, updatedProfile);

        if (updated.isPresent()) {
            UserProfileResponse response = toResponseDTO(updated.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(403).body("Profil güncelleme yetkiniz yok veya profil bulunamadı.");
        }
    }

    // DELETE
    @DeleteMapping("/{userId}")
    @Operation(
        summary = "Profil silme",
        description = "Kullanıcı profilini kalıcı olarak siler. Sadece ADMIN yetkisi gerekir."
    )
    public ResponseEntity<?> deleteProfile(
            @Parameter(description = "Kullanıcı ID'si", example = "1")
            @PathVariable int userId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        int requestId = jwtUtil.extractUserId(token);
        String requestRole = jwtUtil.extractUserRole(token);

        boolean deleted = profileUseCase.deleteProfile(requestId, requestRole, userId);

        if (deleted) {
            return ResponseEntity.ok("Profil silindi.");
        } else {
            return ResponseEntity.status(403).body("Profil silme yetkiniz yok veya profil bulunamadı.");
        }
    }

    @GetMapping
    @Operation(summary = "Tüm profilleri listele (ADMIN)")
    public ResponseEntity<?> listProfiles(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String role = jwtUtil.extractUserRole(token);
        if (!"ADMIN".equals(role)) return ResponseEntity.status(403).build();

        List<UserProfile> profiles = profileUseCase.listAllProfiles(role);
        List<UserProfileResponse> responses = profiles.stream()
            .map(this::toResponseDTO)
            .toList();
        return ResponseEntity.ok(responses);
    }

    // Domain modelden response DTO'ya dönüştürme yardımcı metodu
    private UserProfileResponse toResponseDTO(UserProfile profile) {
        UserProfileResponse dto = new UserProfileResponse();
        dto.setId(profile.getId());
        dto.setAdres(profile.getAdres());
        dto.setTelefon(profile.getTelefon());
        dto.setBirthDate(profile.getBirthDate());
        dto.setUserId(profile.getUser().getId());
        return dto;
    }
}
