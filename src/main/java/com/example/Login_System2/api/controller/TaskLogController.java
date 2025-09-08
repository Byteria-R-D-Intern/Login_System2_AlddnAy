package com.example.Login_System2.api.controller;

import com.example.Login_System2.application.usecase.TaskLogUseCase;
import com.example.Login_System2.api.dto.TaskLogDto;
import com.example.Login_System2.api.dto.DtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-logs")
@AllArgsConstructor
@Tag(name = "Task Logs", description = "Görev logları için API")
public class TaskLogController {

    private final TaskLogUseCase taskLogUseCase;

    @GetMapping("/all")
    @Operation(summary = "Tüm loglar (admin)")
    public ResponseEntity<?> getAllLogs() {
        var dtos = taskLogUseCase.getAllLogs()
                .stream().map(DtoMapper::toTaskLogDto)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Görev logları")
    public ResponseEntity<?> getTaskLogs(@PathVariable int taskId) {
        List<TaskLogDto> list = taskLogUseCase.getTaskLogs(taskId)
                .stream().map(DtoMapper::toTaskLogDto)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/actor/{actorId}")
    @Operation(summary = "Kullanıcıya ait loglar")
    public ResponseEntity<?> getActorLogs(@PathVariable int actorId) {
        List<TaskLogDto> list = taskLogUseCase.getActorLogs(actorId)
                .stream().map(DtoMapper::toTaskLogDto)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(list);
    }
}




