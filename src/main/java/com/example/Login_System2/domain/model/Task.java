package com.example.Login_System2.domain.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;


    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<TaskAssignment> assignments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "current_assignee_id")
    private User currentAssignee; // Şu anki atanan kişi

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;
}
