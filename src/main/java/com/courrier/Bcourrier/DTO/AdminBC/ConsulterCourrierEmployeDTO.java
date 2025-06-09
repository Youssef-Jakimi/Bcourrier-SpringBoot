package com.courrier.Bcourrier.DTO.AdminBC;

import lombok.Data;

import java.time.LocalDate;
@Data
public class ConsulterCourrierEmployeDTO {
        private LocalDate dateRegistre;
        private String object;
        private String employeNom;
        private String employeCIN;


    public ConsulterCourrierEmployeDTO(LocalDate dateRegistre, String object, String nom, String cin) {
        this.dateRegistre = dateRegistre;
        this.employeNom = nom;
        this.employeCIN = cin;
        this.object = object;
    }
}
