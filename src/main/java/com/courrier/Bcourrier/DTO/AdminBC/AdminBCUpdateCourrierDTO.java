package com.courrier.Bcourrier.DTO.AdminBC;

import lombok.Data;

@Data
public class AdminBCUpdateCourrierDTO {
    private int courrierId;
    private Long serviceId;
    private Long urgence;           // e.g., "URGENT", "NORMAL"
    private Long confidentialite;   // e.g., "SECRET", "ROUTINE"
}
