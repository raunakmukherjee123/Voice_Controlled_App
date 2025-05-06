package com.example.VoiceTasker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "task")
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userId", length = 255)
    private Long userId;

    @Column(name = "operation", length = 255)
    private String operation;

    @Column(name = "task", columnDefinition = "TEXT")
    private String task;

    @Column(name = "urgency")
    private String urgency;

    @Column(name = "datetime")
    private String datetime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
