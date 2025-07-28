package com.example.Login_System2.api.dto.TaskDto;

import com.example.Login_System2.domain.model.Status;
import com.example.Login_System2.domain.model.Priority;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private int id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private int ownerId;


}
