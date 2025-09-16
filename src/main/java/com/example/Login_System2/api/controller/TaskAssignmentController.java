package com.example.Login_System2.api.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;
import com.example.Login_System2.application.usecase.TaskAssignmentUseCase;
import com.example.Login_System2.domain.model.TaskAssignment;
import com.example.Login_System2.api.dto.TaskAssignmentDto.*;
import com.example.Login_System2.api.util.ControllerUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/task-assignments")
@AllArgsConstructor
@Tag(name = "Task Assignment", description = "Görev atama işlemleri")
public class TaskAssignmentController {

    private final TaskAssignmentUseCase taskAssignmentUseCase;

    @PostMapping
    @Operation(
        summary = "Görev atama",
        description = "MANAGER ve ADMIN görev atayabilir. USER atama yapamaz."
    )
    public ResponseEntity<?> assignTask(
        @RequestBody @Valid TaskAssignmentRequest request,
        Authentication authentication
    ){
        if(authentication == null)
            return ResponseEntity.status(401).body("Authentication bulunamadı!");

        int userId = (Integer) authentication.getPrincipal();
        String role = ControllerUtil.getRoleFromAuthentication(authentication);

        Optional<TaskAssignment> assignment = taskAssignmentUseCase.assignTask(
            userId, role, request.getTaskId(), request.getAssignedToId(), request.getMessage());

        if(assignment.isPresent()){
            TaskAssignmentResponse response = toResponseDTO(assignment.get());
            return ResponseEntity.ok(response);
        }else
            return ResponseEntity.status(400).body("Görev atama yetkiniz yok veya hata oluştu.");

    }

    @GetMapping("/my")
    @Operation(
        summary = "Kendi atamalarımı görme",
        description = "Kullanıcının kendine atanan görevleri listeler."
    )
    public ResponseEntity<?> getMyAssignments(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication bulunamadı!");
        }

        int userId = (Integer) authentication.getPrincipal();
        List<TaskAssignment> assignments = taskAssignmentUseCase.getMyAssignments(userId);
        
        List<TaskAssignmentResponse> responses = assignments.stream()
            .map(this::toResponseDTO)
            .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/task/{taskId}")
    @Operation(
        summary = "Görevin atamalarını görme",
        description = "Belirli bir görevin tüm atamalarını listeler."
    )
    public ResponseEntity<?> getTaskAssignments(
        @Parameter(description = "Görev ID'si", example = "1")
        @PathVariable int taskId,
        Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication bulunamadı!");
        }

        int userId = (Integer) authentication.getPrincipal();
        String role = ControllerUtil.getRoleFromAuthentication(authentication);

        List<TaskAssignment> assignments = taskAssignmentUseCase.getTaskAssignments(taskId, userId, role);
        
        List<TaskAssignmentResponse> responses = assignments.stream()
            .map(this::toResponseDTO)
            .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{assignmentId}/respond")
    @Operation(
        summary = "Atamaya yanıt verme",
        description = "Atanan kişi görevi kabul veya red edebilir."
    )
    public ResponseEntity<?> respondToAssignment(
        @Parameter(description = "Atama ID'si", example = "1")
        @PathVariable int assignmentId,
        @RequestBody @Valid AssignmentResponseRequest request,
        Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication bulunamadı!");
        }

        int userId = (Integer) authentication.getPrincipal();
        
        Optional<TaskAssignment> assignment = taskAssignmentUseCase.respondToAssignment(
            userId, assignmentId, request.getResponse(), request.getMessage());

        if (assignment.isPresent()) {
            TaskAssignmentResponse response = toResponseDTO(assignment.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(403).body("Atama yanıtlama yetkiniz yok veya hata oluştu.");
        }
    }

    /*@PutMapping("/{assignmentId}/cancel")
    @Operation(
        summary = "Atamayı iptal et",
        description = "ADMIN/MANAGER veya atayan kişi atamayı iptal edebilir."
    )
    public ResponseEntity<?> cancelAssignment(
        @Parameter(description = "Atama ID'si", example = "1")
        @PathVariable int assignmentId,
        @RequestParam(required = false) String reason,
        Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication bulunamadı!");
        }

        int userId = (Integer) authentication.getPrincipal();
        String role = ControllerUtil.getRoleFromAuthentication(authentication);

        Optional<TaskAssignment> assignment = taskAssignmentUseCase.cancelAssignment(userId, role, assignmentId, reason);
        if (assignment.isPresent()) {
            TaskAssignmentResponse response = toResponseDTO(assignment.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(403).body("Atama iptal yetkiniz yok veya atama bulunamadı.");
        }
    }*/


    private TaskAssignmentResponse toResponseDTO(TaskAssignment assignment) {
        TaskAssignmentResponse dto = new TaskAssignmentResponse();
        dto.setId(assignment.getId());
        dto.setTaskId(assignment.getTask().getId());
        dto.setTaskTitle(assignment.getTask().getTitle());
        dto.setAssignedById(assignment.getAssignedBy().getId());
        dto.setAssignedByName(assignment.getAssignedBy().getName() + " " + assignment.getAssignedBy().getSurname());
        dto.setAssignedToId(assignment.getAssignedTo().getId());
        dto.setAssignedToName(assignment.getAssignedTo().getName() + " " + assignment.getAssignedTo().getSurname());
        dto.setStatus(assignment.getStatus());
        dto.setAssignedAt(assignment.getAssignedAt());
        dto.setRespondedAt(assignment.getRespondedAt());
        dto.setMessage(assignment.getMessage());
        return dto;
    }
}
