package com.courrier.Bcourrier.Entities;

import com.courrier.Bcourrier.Enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Employe")

public class Employe {


                @Id
                @GeneratedValue(strategy = GenerationType.IDENTITY)
                private int id;

                @Column(name = "email", nullable = true)
                private String email;

                @Column(name = "nom", nullable = true)
                private String nom;


                @Column(name = "prenom", nullable = true)
                private String prenom;

                @Column(name = "password", nullable = true)
                private String password;


                @Enumerated(EnumType.STRING)
                @Column(name = "role", nullable = true)
                private Role role;

                @Column(name = "matricule", nullable = true)
                private String matricule;

                @Column(name = "CIN", nullable = true)
                private String cin;

                @Column(name = "dateNaissance", nullable = true)
                private LocalDate dateNaissance;

                @ManyToOne
                @JoinColumn(name = "service", referencedColumnName = "id")
                private ServiceIntern service;

                @Column(name = "telephone", nullable = true)
                private String telephone;

                @Column(name = "login", nullable = true)
                private String login;

                @Column(name = "check_email", nullable = true)
                private boolean checkEmail = false;

                @Column(name = "active", nullable = true)
                private boolean active = false;

                @Column(name = "Q1", nullable = true)
                private String q1;

                @Column(name = "R1", nullable = true)
                private String r1;

                @Column(name = "Q2", nullable = true)
                private String q2;

                @Column(name = "R2", nullable = true)
                private String r2;

                @Column(name = "Q3", nullable = true)
                private String q3;

                @Column(name = "R3", nullable = true)
                private String r3;

                @Column(name = "EmailNotifications")
                private boolean EmailNotifications = false;

                @Column(name = "SmsNotifications")
                private boolean SmsNotifications = false;

                @Column(name = "PushNotofications")
                private boolean PushNotifications = false;


                private String verificationToken;


}