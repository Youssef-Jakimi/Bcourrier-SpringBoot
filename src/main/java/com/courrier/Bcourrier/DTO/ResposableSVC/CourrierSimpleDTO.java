package com.courrier.Bcourrier.DTO.ResposableSVC;


import lombok.Data;

@Data
public class CourrierSimpleDTO {
    private Long id;
    private String objet;
    private String description;
    private String type; // ARRIVEE or DEPART
    private String dateArrive;
    private String dateRegistre;
    private boolean archiver;
    private String signataire;
    private String urgence;
}

