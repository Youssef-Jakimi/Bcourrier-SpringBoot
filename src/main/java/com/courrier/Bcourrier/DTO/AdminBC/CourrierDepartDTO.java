package com.courrier.Bcourrier.DTO.AdminBC;

import lombok.Data;

@Data
public class CourrierDepartDTO {
    private int id;                   // Depart id
    private String object;            // Courrier object
    private String description;       // Courrier description
    private String dateDepart;        // (Courrier dateRegistre or other field)
    private int numeroRegistre;       // Courrier numeroRegistre
    private boolean archiver;         // Courrier archiver
    private String statutCourrier;    // Consistent with arrivee DTO
    private String service;           // Only service name
    private String employe;           // Only employe name (prenom + nom)
    private String nomExpediteur;     // Depart specific
    private String voieExpedition;    // Depart specific
    private String nature;            // Courrier nature
    private String degreConfiden;     // Courrier degreConfiden
    private String urgence;           // Courrier urgence
    private String motDes;            // Courrier motDes
}
