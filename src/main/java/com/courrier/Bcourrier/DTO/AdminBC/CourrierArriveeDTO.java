package com.courrier.Bcourrier.DTO.AdminBC;


import lombok.Data;

@Data
public class CourrierArriveeDTO {
    private int id;
    private String object;
    private String description;
    private String dateArrive;
    private String dateTraitement;
    private int numeroRegistre;
    private String dateRegistre;
    private String signataire;
    private boolean archiver;
    private String service;  // Only service name
    private String employe;  // Only employe name (prenom + nom)
    private String nature;
    private String degreConfiden;
    private String urgence;
    private String motDes;
}

