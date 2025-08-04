package com.example.Login_System2.api.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import com.example.Login_System2.domain.model.Task;
import com.example.Login_System2.domain.model.TaskAssignment;
import com.example.Login_System2.domain.model.User;
import com.example.Login_System2.api.dto.TaskDto.TaskResponse;
import com.example.Login_System2.api.dto.TaskAssignmentDto.TaskAssignmentResponse;
import com.example.Login_System2.api.dto.UserResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ControllerUtil {
    
    // Authentication'dan role çıkarma
    public static String getRoleFromAuthentication(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .findFirst()
            .map(GrantedAuthority::getAuthority)
            .map(auth -> auth.replace("ROLE_", ""))
            .orElse("USER");
    }
    
    // Task -> TaskResponse dönüştürme
    public static TaskResponse toTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setOwnerId(task.getOwner().getId());
        
        // Owner bilgisi
        UserResponse ownerResponse = toUserResponse(task.getOwner());
        response.setOwner(ownerResponse);
        
        // Atama bilgileri
        List<TaskAssignmentResponse> assignmentResponses = task.getAssignments().stream()
            .map(ControllerUtil::toTaskAssignmentResponse)
            .collect(Collectors.toList());
        response.setAssignments(assignmentResponses);
        
        // Current assignee bilgisi
        if (task.getCurrentAssignee() != null) {
            UserResponse currentAssigneeResponse = toUserResponse(task.getCurrentAssignee());
            response.setCurrentAssignee(currentAssigneeResponse);
        }
        
        return response;
    }
    
    // TaskAssignment -> TaskAssignmentResponse dönüştürme
    public static TaskAssignmentResponse toTaskAssignmentResponse(TaskAssignment assignment) {
        TaskAssignmentResponse response = new TaskAssignmentResponse();
        response.setId(assignment.getId());
        response.setTaskId(assignment.getTask().getId());
        response.setTaskTitle(assignment.getTask().getTitle());
        
        // AssignedTo bilgisi
        UserResponse assignedToResponse = toUserResponse(assignment.getAssignedTo());
        response.setAssignedTo(assignedToResponse);
        
        // AssignedBy bilgisi
        UserResponse assignedByResponse = toUserResponse(assignment.getAssignedBy());
        response.setAssignedBy(assignedByResponse);
        
        response.setStatus(assignment.getStatus());
        response.setAssignedAt(assignment.getAssignedAt());
        response.setRespondedAt(assignment.getRespondedAt());
        response.setMessage(assignment.getMessage());
        
        return response;
    }
    
    // User -> UserResponse dönüştürme
    public static UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().toString());
        return response;
    }
}
