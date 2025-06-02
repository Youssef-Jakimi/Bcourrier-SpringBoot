package com.courrier.Bcourrier.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String login;
    private String CIN;
    private Date dateNaissance;
    private String matricule;
    private String nom;
    private String prenom;
    private String telephone;
}
