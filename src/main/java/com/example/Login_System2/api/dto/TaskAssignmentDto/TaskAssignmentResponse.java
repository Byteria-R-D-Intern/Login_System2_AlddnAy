package com.example.Login_System2.api.dto.TaskAssignmentDto;

import com.example.Login_System2.domain.model.AssignmentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignmentResponse {
    private int id;
    private int taskId;
    private String taskTitle;
    private int assignedById;
    private String assignedByName;
    private int assignedToId;
    private String assignedToName;
    private AssignmentStatus status;
    private LocalDateTime assignedAt;
    private LocalDateTime respondedAt;
    private String message;
}
