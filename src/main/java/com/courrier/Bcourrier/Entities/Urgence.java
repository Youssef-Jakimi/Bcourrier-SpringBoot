package com.courrier.Bcourrier.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Urgence")
public class Urgence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nom", nullable = false)
    private String nom;
}
