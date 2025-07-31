package com.example.Login_System2.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task_assignments")
public class TaskAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "assigned_by", nullable = false)
    private User assignedBy; // Görevi atayan kişi
    
    @ManyToOne
    @JoinColumn(name = "assigned_to", nullable = false)
    private User assignedTo; // Görevi alan kişi
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status; // PENDING, ACCEPTED, REJECTED
    
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;
    
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
    
    @Column(columnDefinition = "TEXT")
    private String message; // Atama mesajı

}


