package com.example.Login_System2.api.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.example.Login_System2.application.usecase.TaskUseCase;
import com.example.Login_System2.domain.model.Task;
import com.example.Login_System2.domain.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import com.example.Login_System2.api.dto.TaskDto.*;
import com.example.Login_System2.domain.model.Status;
import com.example.Login_System2.domain.model.Priority;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@RequestMapping("/api/tasks")
@Data
@AllArgsConstructor
@Tag(name = "Task Management", description = "Görev yönetimi için API endpoint'leri")
public class TaskController {

    private final TaskUseCase taskUseCase;
    
    @PostMapping("/tasks")
    @Operation(
        summary = "Yeni görev oluştur",
        description = "Kullanıcılar yeni görev oluşturabilir. USER sadece kendi görevlerini oluşturabilir."
    )
    public ResponseEntity<?> createTask(
        @RequestBody @Valid TaskRequest request,
        Authentication authentication) {

        // Debug için log
        System.out.println("=== CREATE TASK ===");
        System.out.println("Authentication: " + authentication);
        System.out.println("Principal: " + (authentication != null ? authentication.getPrincipal() : "NULL"));

        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication bulunamadı!");
        }

        int userId = (Integer) authentication.getPrincipal();
        String role = getRoleFromAuthentication(authentication);

        System.out.println("UserId: " + userId);
        System.out.println("Role: " + role);

        Task newTask = new Task();
        newTask.setTitle(request.getTitle());
        newTask.setDescription(request.getDescription());
        newTask.setStatus(request.getStatus());
        newTask.setPriority(request.getPriority());
        User owner = new User();
        owner.setId(request.getOwnerId());
        newTask.setOwner(owner);
    
        Optional<Task> created = taskUseCase.createTask(userId, role, newTask);
    
        if (created.isPresent()) {
            TaskResponse response = toResponseDTO(created.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(403).body("Görev oluşturma yetkiniz yok veya hata oluştu.");
        }
    }

    @GetMapping("/tasks/{taskId}")
    @Operation(
        summary = "Belirli bir görevi getir",
        description = "Görev ID'sine göre görev detaylarını getirir. USER sadece kendi görevlerini görebilir."
    )
    public ResponseEntity<?> getTask(
        @Parameter(description = "Görev ID'si", example = "1")
        @PathVariable int taskId,
        Authentication authentication) {

        // Debug için log
        System.out.println("=== GET TASK ===");
        System.out.println("Authentication: " + authentication);
        System.out.println("Principal: " + (authentication != null ? authentication.getPrincipal() : "NULL"));

        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication bulunamadı!");
        }

        int userId = (Integer) authentication.getPrincipal();
        String role = getRoleFromAuthentication(authentication);

        System.out.println("UserId: " + userId);
        System.out.println("Role: " + role);
        System.out.println("TaskId: " + taskId);

        Optional<Task> task = taskUseCase.getTask(userId, role, taskId);

        if (task.isPresent()) {
            TaskResponse response = toResponseDTO(task.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body("Görev bulunamadı.");
        }
    }

    @GetMapping("/tasks")
    @Operation(
        summary = "Tüm görevleri listele",
        description = "Filtreleme seçenekleri ile görevleri listeler. USER sadece kendi görevlerini görebilir."
    )
    public ResponseEntity<?> getAllTasks(
        Authentication authentication,
        @Parameter(description = "Görev durumu (YAPILACAK, DEVAM_EDIYOR, BITTI)", example = "YAPILACAK")
        @RequestParam(required = false) String status,
        @Parameter(description = "Görev önceliği (LOW, MEDIUM, HIGH)", example = "HIGH")
        @RequestParam(required = false) String priority,
        @Parameter(description = "Sahip kullanıcı ID'si", example = "1")
        @RequestParam(required = false) Integer ownerId) {

        // Debug için log
        System.out.println("=== GET ALL TASKS ===");
        System.out.println("Authentication: " + authentication);
        System.out.println("Principal: " + (authentication != null ? authentication.getPrincipal() : "NULL"));

        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication bulunamadı!");
        }

        int userId = (Integer) authentication.getPrincipal();
        String role = getRoleFromAuthentication(authentication);

        System.out.println("UserId: " + userId);
        System.out.println("Role: " + role);

        // USER sadece kendi görevlerini görebilir
        if (role.equals("USER")) {
            ownerId = userId;
        }
            
        Status statusEnum = null;
        Priority priorityEnum = null;
        
        if (status != null) {
            try {
                statusEnum = Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Geçersiz status değeri: " + status);
            }
        }
        
        if (priority != null) {
            try {
                priorityEnum = Priority.valueOf(priority.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Geçersiz priority değeri: " + priority);
            }
        }

        List<Task> tasks = taskUseCase.getAllTasks(userId, role, ownerId, statusEnum, priorityEnum);
        
        List<TaskResponse> responses = tasks.stream()
            .map(this::toResponseDTO)
            .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/tasks/{taskId}")
    @Operation(
        summary = "Görevi güncelle",
        description = "Mevcut görevi günceller. USER sadece kendi görevlerini güncelleyebilir."
    )
    public ResponseEntity<?> updateTask(
        @Parameter(description = "Görev ID'si", example = "1")
        @PathVariable int taskId,
        @RequestBody @Valid TaskUpdateRequest request,
        Authentication authentication) {
        
        // Debug için log
        System.out.println("=== UPDATE TASK ===");
        System.out.println("Authentication: " + authentication);
        System.out.println("Principal: " + (authentication != null ? authentication.getPrincipal() : "NULL"));

        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication bulunamadı!");
        }

        int userId = (Integer) authentication.getPrincipal();
        String role = getRoleFromAuthentication(authentication);

        System.out.println("UserId: " + userId);
        System.out.println("Role: " + role);
        System.out.println("TaskId: " + taskId);

        Task updatedTask = new Task();
        updatedTask.setTitle(request.getTitle());
        updatedTask.setDescription(request.getDescription());
        updatedTask.setStatus(request.getStatus());
        updatedTask.setPriority(request.getPriority());

        Optional<Task> updated = taskUseCase.updateTask(userId, role, taskId, updatedTask);

        if (updated.isPresent()) {
            TaskResponse response = toResponseDTO(updated.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(403).body("Görev güncelleme yetkiniz yok veya görev bulunamadı.");
        }
    }

    @DeleteMapping("/tasks/{taskId}")
    @Operation(
        summary = "Görevi sil",
        description = "Görevi kalıcı olarak siler. USER sadece kendi görevlerini silebilir."
    )
    public ResponseEntity<?> deleteTask(
        @PathVariable int taskId,
        Authentication authentication) {
        
        // Debug için log
        System.out.println("=== DELETE TASK ===");
        System.out.println("Authentication: " + authentication);
        System.out.println("Principal: " + (authentication != null ? authentication.getPrincipal() : "NULL"));

        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication bulunamadı!");
        }

        int userId = (Integer) authentication.getPrincipal();
        String role = getRoleFromAuthentication(authentication);

        System.out.println("UserId: " + userId);
        System.out.println("Role: " + role);
        System.out.println("TaskId: " + taskId);

        boolean deleted = taskUseCase.deleteTask(userId, role, taskId);

        if (deleted) {
            return ResponseEntity.ok("Görev silindi.");
        } else {
            return ResponseEntity.status(403).body("Görev silme yetkiniz yok veya görev bulunamadı.");
        }
    }


    private String getRoleFromAuthentication(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .findFirst()
            .map(GrantedAuthority::getAuthority)
            .map(auth -> auth.replace("ROLE_", ""))
            .orElse("USER");
    }

    private TaskResponse toResponseDTO(Task task) {
        TaskResponse dto = new TaskResponse();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setOwnerId(task.getOwner().getId());
        return dto;
    }
}