package com.example.Login_System2.api.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.example.Login_System2.application.usecase.TaskUseCase;
import com.example.Login_System2.domain.model.Task;
import com.example.Login_System2.domain.model.User;
import com.example.Login_System2.api.util.ControllerUtil;

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
    
    @PostMapping
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
        String role = ControllerUtil.getRoleFromAuthentication(authentication);

        System.out.println("UserId: " + userId);
        System.out.println("Role: " + role);
        System.out.println("Request ownerId: " + request.getOwnerId());

        Task newTask = new Task();
        newTask.setTitle(request.getTitle());
        newTask.setDescription(request.getDescription());
        newTask.setStatus(request.getStatus());
        newTask.setPriority(request.getPriority());
        User owner = new User();
        owner.setId(request.getOwnerId());
        newTask.setOwner(owner);
        
        System.out.println("Task owner ID: " + newTask.getOwner().getId());
    
        Optional<Task> created = taskUseCase.createTask(userId, role, newTask);
    
        if (created.isPresent()) {
            System.out.println("Task başarıyla oluşturuldu! Task ID: " + created.get().getId());
            try {
                TaskResponse response = ControllerUtil.toTaskResponse(created.get());
                System.out.println("Response DTO başarıyla oluşturuldu!");
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                System.out.println("Response DTO oluşturulurken hata: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body("Response oluşturulurken hata: " + e.getMessage());
            }
        } else {
            System.out.println("Task oluşturulamadı!");
            return ResponseEntity.status(403).body("Görev oluşturma yetkiniz yok veya hata oluştu.");
        }
    }

    @GetMapping("/{taskId}")
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
        String role = ControllerUtil.getRoleFromAuthentication(authentication);

        System.out.println("UserId: " + userId);
        System.out.println("Role: " + role);
        System.out.println("TaskId: " + taskId);

        Optional<Task> task = taskUseCase.getTask(userId, role, taskId);

        if (task.isPresent()) {
            TaskResponse response = ControllerUtil.toTaskResponse(task.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body("Görev bulunamadı.");
        }
    }


    @GetMapping
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
        @RequestParam(required = false) Integer ownerId,
        @Parameter(description = "Görev başlığında arama yapılacak metin", example = "rapor")
        @RequestParam(required = false) String title) {

        // Debug için log
        System.out.println("=== GET ALL TASKS ===");
        System.out.println("Authentication: " + authentication);
        System.out.println("Principal: " + (authentication != null ? authentication.getPrincipal() : "NULL"));

        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication bulunamadı!");
        }

        int userId = (Integer) authentication.getPrincipal();
        String role = ControllerUtil.getRoleFromAuthentication(authentication);

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

        List<Task> tasks = taskUseCase.getAllTasks(userId, role, ownerId, statusEnum, priorityEnum, title);
        
        List<TaskResponse> responses = tasks.stream()
            .map(ControllerUtil::toTaskResponse)
            .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{taskId}")
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
        String role = ControllerUtil.getRoleFromAuthentication(authentication);

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
            TaskResponse response = ControllerUtil.toTaskResponse(updated.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(403).body("Görev güncelleme yetkiniz yok veya görev bulunamadı.");
        }
    }

    @DeleteMapping("/{taskId}")
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
        String role = ControllerUtil.getRoleFromAuthentication(authentication);

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



}