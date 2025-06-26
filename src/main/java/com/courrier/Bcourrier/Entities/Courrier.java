package com.courrier.Bcourrier.Entities;

import com.courrier.Bcourrier.Enums.StatutCourrier;
import com.courrier.Bcourrier.Enums.TypeCourrier;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "courrier")
public class Courrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "object", nullable = true)
    private String object;

    @Enumerated(EnumType.STRING)
    private TypeCourrier type;

    @Column(name = "mot_des", nullable = true)
    private String motDes;

    @Column(name = "date_Arrive", nullable = true)
    private LocalDate dateArrive;

    @Column(name = "date_Traitement", nullable = true)
    private LocalDate dateTraitement;


    @Column(name = "numero_Registre", nullable = true)
    private int numeroRegistre;

    @Column(name = "date_Registre", nullable = true)
    private LocalDate dateRegistre;

    @Column(name = "signataire", nullable = true)
    private String signataire;

    @Column(name = "archiver", nullable = true)
    private boolean archiver;

    @ManyToOne
    @JoinColumn(name = "service", referencedColumnName = "id")
    private ServiceIntern service;

    @ManyToOne
    @JoinColumn(name = "employe", referencedColumnName = "id")
    private Employe employe;


    @Column(name = "nature", nullable = true)
    private String nature;


    @ManyToOne
    @JoinColumn(name = "reponseA", referencedColumnName = "id", nullable = true)
    private Courrier reponseA;

    @ManyToOne
    @JoinColumn(name = "confidentialite", referencedColumnName = "id")
    private Confidentialite degreConfiden;

    @ManyToOne
    @JoinColumn(name = "urgence", referencedColumnName = "id")
    private Urgence urgence;

    @Column(name = "attachment_path")
    private String attachmentPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = true)
    private StatutCourrier StatutCourrier;

    // Getters and Setters
}
