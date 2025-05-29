package com.courrier.Bcourrier.DTO.AdminSI;

import lombok.Data;

// DTO/AdminSI/AdminSIModifyUserDTO.java
@Data
public class AdminSIModifyUserDTO {
    private Long id;          // Employee ID
    private String role;      // New role (string or enum name)
    private Long serviceId;   // New service ID
    private Boolean active;   // true or false (for modify only)
}
