package com.courrier.Bcourrier.DTO.AdminBC;

import lombok.Data;

@Data

public class EmployeDTO {
    private int id;
    private String Nom;
    private String Prenom;

    public EmployeDTO(int id, String nom, String prenom) {
        this.id = id;
        this.Nom = nom;
        this.Prenom = prenom;

    }


    // Getters and setters
}

