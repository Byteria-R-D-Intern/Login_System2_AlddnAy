package com.example.Login_System2.api.dto.TaskCommentDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCommentRequest {
    
    @NotNull(message = "Task ID boş olamaz")
    private Integer taskId;
    
    @NotBlank(message = "Yorum boş olamaz")
    private String comment;
}