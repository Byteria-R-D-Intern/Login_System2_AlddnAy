package com.example.Login_System2.api.controller;

import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import com.example.Login_System2.application.usecase.TaskCommentUseCase;
import com.example.Login_System2.domain.model.TaskComment;
import com.example.Login_System2.api.dto.TaskCommentDto.TaskCommentRequest;
import com.example.Login_System2.api.dto.TaskCommentDto.TaskCommentResponse;
import com.example.Login_System2.api.util.ControllerUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/task-comments")
@Data
@AllArgsConstructor
@Tag(name = "Task Comments", description = "Görev yorumları için API endpoint'leri")
public class TaskCommentController {

    private final TaskCommentUseCase taskCommentUseCase;

    @PostMapping
    @Operation(
        summary = "Göreve yorum ekle",
        description = "Kullanıcılar görevlere yorum ekleyebilir. USER sadece kendi görevlerine yorum yapabilir."
    )
    public ResponseEntity<?> addComment(
        @RequestBody @Valid TaskCommentRequest request,
        Authentication authentication) {

        System.out.println("=== ADD COMMENT ===");
        System.out.println("Authentication: " + authentication);
        System.out.println("Principal: " + (authentication != null ? authentication.getPrincipal() : "NULL"));

        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication bulunamadı!");
        }

        int userId = (Integer) authentication.getPrincipal();
        String role = ControllerUtil.getRoleFromAuthentication(authentication);

        System.out.println("UserId: " + userId);
        System.out.println("Role: " + role);
        System.out.println("TaskId: " + request.getTaskId());
        System.out.println("Comment: " + request.getComment());

        Optional<TaskComment> created = taskCommentUseCase.addComment(userId, role, request.getTaskId(), request.getComment());

        if (created.isPresent()) {
            TaskCommentResponse response = toCommentResponseDTO(created.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(403).body("Yorum ekleme yetkiniz yok veya hata oluştu.");
        }
    }

    @GetMapping("/task/{taskId}")
    @Operation(
        summary = "Görevin yorumlarını getir",
        description = "Görev ID'sine göre yorumları listeler. USER sadece kendi görevlerinin yorumlarını görebilir."
    )
    public ResponseEntity<?> getTaskComments(
        @Parameter(description = "Görev ID'si", example = "1")
        @PathVariable int taskId,
        Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication bulunamadı!");
        }

        int userId = (Integer) authentication.getPrincipal();
        String role = ControllerUtil.getRoleFromAuthentication(authentication);

        List<TaskComment> comments = taskCommentUseCase.getTaskComments(taskId, userId, role);
        
        List<TaskCommentResponse> responses = comments.stream()
            .map(this::toCommentResponseDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{commentId}")
    @Operation(
        summary = "Yorumu sil",
        description = "Yorumu kalıcı olarak siler. USER sadece kendi yorumlarını silebilir."
    )
    public ResponseEntity<?> deleteComment(
        @Parameter(description = "Yorum ID'si", example = "1")
        @PathVariable int commentId,
        Authentication authentication) {
        
        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication bulunamadı!");
        }

        int userId = (Integer) authentication.getPrincipal();
        String role = ControllerUtil.getRoleFromAuthentication(authentication);

        boolean deleted = taskCommentUseCase.deleteComment(userId, role, commentId);

        if (deleted) {
            return ResponseEntity.ok("Yorum silindi.");
        } else {
            return ResponseEntity.status(403).body("Yorum silme yetkiniz yok veya yorum bulunamadı.");
        }
    }

    private TaskCommentResponse toCommentResponseDTO(TaskComment comment) {
        TaskCommentResponse response = new TaskCommentResponse();
        response.setId(comment.getId());
        response.setTaskId(comment.getTask().getId());
        response.setTaskTitle(comment.getTask().getTitle());
        response.setUser(ControllerUtil.toUserResponse(comment.getUser()));
        response.setComment(comment.getComment());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        return response;
    }
}
