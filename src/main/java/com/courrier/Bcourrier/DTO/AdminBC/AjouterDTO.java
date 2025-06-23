package com.courrier.Bcourrier.DTO.AdminBC;

import com.courrier.Bcourrier.Entities.Confidentialite;
import com.courrier.Bcourrier.Entities.Employe;
import com.courrier.Bcourrier.Entities.ServiceIntern;
import com.courrier.Bcourrier.Entities.Urgence;
import lombok.Data;

import java.util.List;

@Data
public class AjouterDTO {
    private List<Urgence> urgences;
    private List<Confidentialite> confidentialites;
    private List<ServiceIntern> services;
    private List<EmployeDTO> employes;

}
