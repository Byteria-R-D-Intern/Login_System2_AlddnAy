package com.example.Login_System2.api.dto;

import com.example.Login_System2.domain.model.Notification;
import com.example.Login_System2.domain.model.TaskLog;

public final class DtoMapper {

    private DtoMapper() {}

    public static TaskLogDto toTaskLogDto(TaskLog log) {
        TaskLogDto dto = new TaskLogDto();
        dto.setId(log.getId());
        if (log.getTask() != null) {
            dto.setTaskId(log.getTask().getId());
            dto.setTaskTitle(log.getTask().getTitle());
        }
        if (log.getActor() != null) {
            dto.setActorId(log.getActor().getId());
            dto.setActorName(log.getActor().getName());
        }
        if (log.getTarget() != null) {
            dto.setTargetId(log.getTarget().getId());
            dto.setTargetName(log.getTarget().getName());
        }
        dto.setAction(log.getAction());
        dto.setMessage(log.getMessage());
        dto.setChanges(log.getChanges());
        dto.setMetadata(log.getMetadata());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }

    public static NotificationDto toNotificationDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setType(n.getType());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setReadFlag(n.isReadFlag());
        dto.setReadAt(n.getReadAt());
        dto.setCreatedAt(n.getCreatedAt());
        if (n.getTaskLog() != null) {
            dto.setTaskLogId(n.getTaskLog().getId());
            if (n.getTaskLog().getTask() != null) {
                dto.setTaskId(n.getTaskLog().getTask().getId());
            }
        }
        dto.setData(n.getData());
        return dto;
    }
}


