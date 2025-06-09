package com.courrier.Bcourrier.DTO.AdminBC;

import lombok.Data;

import java.time.LocalDate;
@Data
public class ConsulterCourrierEmployeDTO {
        private int id;
        private LocalDate dateRegistre;
        private String object;
        private String employeNom;
        private String employePrenom;
        private String employeCIN;


    public ConsulterCourrierEmployeDTO(int id, LocalDate dateRegistre, String object, String nom,String prenom, String cin) {
        this.id = id;
        this.dateRegistre = dateRegistre;
        this.employeNom = nom;
        this.employePrenom = prenom;
        this.employeCIN = cin;
        this.object = object;
    }
}
