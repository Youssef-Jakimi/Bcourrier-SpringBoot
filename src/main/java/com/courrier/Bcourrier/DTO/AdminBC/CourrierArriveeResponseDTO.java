package com.courrier.Bcourrier.DTO.AdminBC;
import com.courrier.Bcourrier.Entities.Confidentialite;
import com.courrier.Bcourrier.Entities.ServiceIntern;
import com.courrier.Bcourrier.Entities.Urgence;
import lombok.Data;

import java.util.List;

@Data
public class CourrierArriveeResponseDTO {
    private List<CourrierArriveeDTO> courriers;
    private List<ServiceIntern> services;
    private List<Urgence> urgences;
    private List<Confidentialite> confidentialites;
}



