package com.courrier.Bcourrier.DTO.AdminBC;

import lombok.Data;

@Data
public class AdminBCUpdateCourrierDTO {
    private Long courrierId;
    private Long serviceId;
    private String urgence;           // e.g., "URGENT", "NORMAL"
    private String confidentialite;   // e.g., "SECRET", "ROUTINE"
}
