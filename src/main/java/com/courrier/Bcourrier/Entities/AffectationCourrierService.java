package com.courrier.Bcourrier.Entities;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
@Data

@Entity
@Table(name = "affectationcourrierservice")
public class AffectationCourrierService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "dateAffection", nullable = false)
    private LocalDate dateAffection;

    @Column(name = "heureAffectation", nullable = false)
    private String heureAffectation;

    @Column(name = "dateFinExecution", nullable = false)
    private LocalDate dateFinExecution;

    @Column(name = "instruction", nullable = false)
    private String instruction;

    @Column(name = "type_affectation", nullable = false)
    private String typeAffectation;

    @Column(name = "date_consultation", nullable = false)
    private LocalDate dateConsultation;

    @Column(name = "heure_consultation", nullable = false)
    private String heureConsultation;

    @ManyToOne
    @JoinColumn(name = "courrier", referencedColumnName = "id")
    private Courrier courrier;

    @ManyToOne
    @JoinColumn(name = "service", referencedColumnName = "id")
    private ServiceIntern service;

    // Getters and Setters
}

