package com.example.Login_System2.api.dto.TaskCommentDto;

import com.example.Login_System2.api.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCommentResponse {
    
    private int id;
    private int taskId;
    private String taskTitle;
    private UserResponse user;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
