package com.courrier.Bcourrier.DTO.AdminSI;

import lombok.Data;

@Data
public class AdminSIUserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String login;
    private String role;
    private String service;
    private boolean active;
    private boolean checkEmail; // if you want to show verification status
}
