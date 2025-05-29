package com.courrier.Bcourrier.DTO.AdminBC;

import com.courrier.Bcourrier.Entities.Confidentialité;
import com.courrier.Bcourrier.Entities.ServiceIntern;
import com.courrier.Bcourrier.Entities.Urgence;
import lombok.Data;

import java.util.List;

@Data
public class AjouterDTO {
    private List<Urgence> urgences;
    private List<Confidentialité> confidentialites;
    private List<ServiceIntern> services;
}
