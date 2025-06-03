package com.courrier.Bcourrier.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class RegisterRequest {
    private String email;
    private String login;
    private String CIN;
    private LocalDate dateNaissance;
    private String matricule;
    private String nom;
    private String prenom;
    private String telephone;
}
