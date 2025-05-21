package com.courrier.Bcourrier.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
@Entity
@Data
@Table(name = "affectationservice")
public class AffectationService {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Column(name = "date_Affectation", nullable = false)
        private Date dateAffectation;

        @Column(name = "date_FinAffectation", nullable = false)
        private Date dateFinAffectation;

        @ManyToOne
        @JoinColumn(name = "service", referencedColumnName = "id")
        private ServiceIntern service;

        @ManyToOne
        @JoinColumn(name = "employe", referencedColumnName = "id")
        private Employe employe;

}
