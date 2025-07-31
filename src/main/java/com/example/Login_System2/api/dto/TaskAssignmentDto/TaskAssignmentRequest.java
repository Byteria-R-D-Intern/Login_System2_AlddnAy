package com.example.Login_System2.api.dto.TaskAssignmentDto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignmentRequest {
    
    @NotNull
    private Integer taskId;
    
    @NotNull
    private Integer assignedToId;
    
    private String message; // Opsiyonel atama mesajı
}
