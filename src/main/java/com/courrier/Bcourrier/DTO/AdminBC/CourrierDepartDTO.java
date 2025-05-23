// src/main/java/com/courrier/Bcourrier/DTO/AdminBC/CourrierDepartDTO.java
package com.courrier.Bcourrier.DTO.AdminBC;

import lombok.Data;

@Data
public class CourrierDepartDTO {
    private int id;                  // Depart id
    private String object;           // Courrier object
    private String description;      // Courrier description
    private String dateDepart;       // You can adapt to match your schema (use Courrier dateRegistre or add a field)
    private int numeroRegistre;      // Courrier numeroRegistre
    private boolean archiver;        // Courrier archiver
    private String service;          // Only service name
    private String employe;          // Only employe name (prenom + nom)
    private String nomExpediteur;    // Depart specific
    private String voieExpedition;   // Depart specific
    private String nature;           // Courrier nature
    private String degreConfiden;    // Courrier degreConfiden
    private String urgence;          // Courrier urgence
    private String motDes;           // Courrier motDes
}
