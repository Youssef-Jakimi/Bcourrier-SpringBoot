package com.courrier.Bcourrier.Entities;

import com.courrier.Bcourrier.Enums.VoieExpedition;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
@Data

@Entity
@Table(name = "depart")
public class Depart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nom_expediteur", nullable = false)
    private String nomExpediteur;

    @Column(name = "date_expedition", nullable = false)
    private Date dateExpedition;


    @ManyToOne
    @JoinColumn(name = "courrier", referencedColumnName = "id")
    private Courrier courrier;

    @Enumerated(EnumType.STRING)
    @Column(name = "voie_expedition", nullable = false)
    private VoieExpedition voieExpedition;

    // Getters and Setters
}

