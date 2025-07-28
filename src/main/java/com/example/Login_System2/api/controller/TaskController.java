package com.example.Login_System2.api.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.Login_System2.application.usecase.TaskUseCase;
import com.example.Login_System2.domain.model.Task;
import com.example.Login_System2.domain.model.User;
import com.example.Login_System2.infrastructure.Service.Jwtutil;
import com.example.Login_System2.infrastructure.Service.TokenUtil;

import jakarta.validation.Valid;

import com.example.Login_System2.api.dto.TaskDto.*;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@RequestMapping("/api/tasks")
@Data
@AllArgsConstructor
public class TaskController {

    private final TaskUseCase taskUseCase;
    private final Jwtutil jwtUtil;
    private final TokenUtil tokenUtil;
    
    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(
        @RequestBody @Valid TaskRequest request,
        @RequestHeader("Authorization") String authHeader){

            TokenUtil.TokenValidationStatus status = tokenUtil.validationToken(authHeader, jwtUtil);
            if (status != TokenUtil.TokenValidationStatus.VALID) {
                return ResponseEntity.status(401).body("Geçersiz veya süresi dolmuş token! (" + status + ")");
            }

            String token = authHeader.substring(7);
            int userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractUserRole(token);

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
