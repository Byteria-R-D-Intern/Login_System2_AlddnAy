package com.example.Login_System2.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.example.Login_System2.domain.model.TaskLogAction;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TaskLogDto {
    private int id;
    private Integer taskId;
    private String taskTitle;
    private Integer actorId;
    private String actorName;
    private Integer targetId;
    private String targetName;
    private TaskLogAction action;
    private String message;
    private JsonNode changes;
    private JsonNode metadata;
    private LocalDateTime createdAt;
}


