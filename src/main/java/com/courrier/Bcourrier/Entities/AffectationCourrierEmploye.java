package com.courrier.Bcourrier.Entities;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "affectationcourrieremploye")
public class AffectationCourrierEmploye {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "dateAffection", nullable = false)
    private Date dateAffection;

    @Column(name = "heureAffectation", nullable = false)
    private String heureAffectation;

    @Column(name = "dateFinExecution", nullable = false)
    private Date dateFinExecution;

    @Column(name = "instruction", nullable = false)
    private String instruction;

    @Column(name = "type_affectation", nullable = false)
    private String typeAffectation;

    @Column(name = "date_consultation", nullable = false)
    private Date dateConsultation;

    @Column(name = "heure_consultation", nullable = false)
    private String heureConsultation;

    @ManyToOne
    @JoinColumn(name = "courrier", referencedColumnName = "id")
    private Courrier courrier;

    @ManyToOne
    @JoinColumn(name = "employe", referencedColumnName = "id")
    private Employe employe;

    // Getters and Setters
}

