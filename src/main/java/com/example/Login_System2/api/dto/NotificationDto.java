package com.example.Login_System2.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.example.Login_System2.domain.model.NotificationType;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NotificationDto {
    private int id;
    private NotificationType type;
    private String title;
    private String message;
    private boolean readFlag;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    private Integer taskLogId;
    private Integer taskId;
    private JsonNode data;
}


