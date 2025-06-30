package com.courrier.Bcourrier.DTO.AdminBC;

import com.courrier.Bcourrier.Entities.*;
import lombok.Data;

import java.util.List;

@Data
public class AjouterDTO {
    private List<Urgence> urgences;
    private List<Confidentialite> confidentialites;
    private List<ServiceIntern> services;
    private List<EmployeDTO> employes;
    private List<Integer> numRegister;
    private List<VoieExpedition> voieExpeditions;

}
