package com.courrier.Bcourrier.DTO;

import lombok.Data;

// Crée une classe DTO
@Data
public class PasswordResetRequest {
    private String token;
    private String newPassword;


}
