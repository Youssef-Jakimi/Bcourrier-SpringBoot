// src/main/java/com/courrier/Bcourrier/DTO/EmployeeListDTO.java
package com.courrier.Bcourrier.DTO.RH;

import lombok.Data;

@Data
public class EmployeeListDTO {
    private String fullName;
    private String email;
    private String login;
    private String role;
    private String service;
    private boolean active;
    private String employeeSince; // as formatted date string
}
