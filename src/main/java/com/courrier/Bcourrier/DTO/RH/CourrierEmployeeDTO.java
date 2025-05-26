package com.courrier.Bcourrier.DTO.RH;
import lombok.Data;

@Data

public class CourrierEmployeeDTO {
        private Long courrierId;
        private String matricule;
        private String cin;
        private String objet;
        private String employe;
        private String service;
        private String dateArchivage;
        private String statut;
        private String attachmentName;
        private String downloadUrl;
    }


