package com.courrier.Bcourrier.DTO.ResposableSVC;


import com.courrier.Bcourrier.Enums.StatutCourrier;
import lombok.Data;

import java.util.List;

@Data
public class CourrierSimpleDTO {
    private int id;
    private String objet;
    private String description;
    private String type; // ARRIVEE or DEPART
    private String dateArrive;
    private String dateRegistre;
    private boolean archiver;
    private String signataire;
    private String urgence;
    private List<StatutCourrier> statusCourrierList;
}

