package com.example.Login_System2.api.dto.TaskDto;

import com.example.Login_System2.domain.model.Status;
import com.example.Login_System2.domain.model.Priority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor 
public class TaskUpdateRequest {
    
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private Status status;
    @NotNull
    private Priority priority;
    
}
