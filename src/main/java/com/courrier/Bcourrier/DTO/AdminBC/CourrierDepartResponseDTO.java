package com.courrier.Bcourrier.DTO.AdminBC;

import com.courrier.Bcourrier.Entities.ServiceIntern;
import com.courrier.Bcourrier.Enums.Confidentialite;
import com.courrier.Bcourrier.Enums.Urgence;
import lombok.Data;

import java.util.List;

@Data
public class CourrierDepartResponseDTO {
    private List<CourrierDepartDTO> courriers;
    private List<ServiceIntern> services;
    private List<Urgence> urgences;
    private List<Confidentialite> confidentialites;
}
