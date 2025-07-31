package com.example.Login_System2.api.dto.TaskAssignmentDto;

import com.example.Login_System2.domain.model.AssignmentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponseRequest {
    
    @NotNull
    private AssignmentStatus response; // ACCEPTED veya REJECTED
    
    private String message; // Opsiyonel yanıt mesajı
}
