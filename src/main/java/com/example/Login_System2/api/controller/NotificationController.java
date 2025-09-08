package com.example.Login_System2.api.controller;

import com.example.Login_System2.application.usecase.NotificationUseCase;
import com.example.Login_System2.domain.model.Notification;
import com.example.Login_System2.api.dto.NotificationDto;
import com.example.Login_System2.api.dto.DtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
@Tag(name = "Notifications", description = "Bildirimler için API")
public class NotificationController {

    private final NotificationUseCase notificationUseCase;

    @GetMapping
    @Operation(summary = "Tüm bildirimlerim")
    public ResponseEntity<?> myNotifications(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body("Auth yok");
        int userId = (Integer) authentication.getPrincipal();
        List<NotificationDto> list = notificationUseCase.getMyNotifications(userId)
                .stream().map(DtoMapper::toNotificationDto)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/unread")
    @Operation(summary = "Okunmamış bildirimlerim")
    public ResponseEntity<?> unreadNotifications(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body("Auth yok");
        int userId = (Integer) authentication.getPrincipal();
        List<NotificationDto> list = notificationUseCase.getUnreadNotifications(userId)
                .stream().map(DtoMapper::toNotificationDto)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "Bildirim okundu işaretle")
    public ResponseEntity<?> markRead(@PathVariable int id, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body("Auth yok");
        int userId = (Integer) authentication.getPrincipal();
        Optional<Notification> updated = notificationUseCase.markAsRead(id, userId);
        return updated.<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(403).body("İzin yok veya bulunamadı"));
    }
}




