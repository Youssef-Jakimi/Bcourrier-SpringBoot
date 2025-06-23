package com.courrier.Bcourrier.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Question")
public class Question {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String nom;
        private LocalDateTime dateSuppression;
        private Timestamp dateCreation;

    // Getters and Setters
}

