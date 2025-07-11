package com.courrier.Bcourrier.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nom", nullable = false)
    private String nom;
    private Timestamp dateCreation;
    private LocalDateTime dateSuppression;
}
