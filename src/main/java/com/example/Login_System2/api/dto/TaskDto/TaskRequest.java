package com.example.Login_System2.api.dto.TaskDto;

import com.example.Login_System2.domain.model.Status;
import com.example.Login_System2.domain.model.Priority;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private Status status;
    @NotNull
    private Priority priority;
    private LocalDate dueDate;
    @NotNull
    private Integer ownerId;
    
}
