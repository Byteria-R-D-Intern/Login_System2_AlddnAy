package com.example.Login_System2.domain.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task_logs")
public class TaskLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    // Kime yapıldı (opsiyonel)
    @ManyToOne
    @JoinColumn(name = "target_id")
    private User target;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskLogAction action;

    // İlgili yorum (opsiyonel)
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private TaskComment comment;

    // Serbest mesaj/açıklama 
    @Column(name = "details", columnDefinition = "TEXT")
    private String message;

    // Değişen alanlar (JSONB)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "changes", columnDefinition = "jsonb")
    private JsonNode changes;

    // Ek bağlam (JSONB)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private JsonNode metadata;

    

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}


