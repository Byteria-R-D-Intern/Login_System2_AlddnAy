package com.example.Login_System2.web.controller;

import com.example.Login_System2.application.usecase.TaskLogUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/logs")
@AllArgsConstructor
public class AdminLogViewController {

    private final TaskLogUseCase taskLogUseCase;

    @GetMapping
    public String logs(Model model) {
        model.addAttribute("logs", taskLogUseCase.getAllLogs());
        return "logs";
    }
}


