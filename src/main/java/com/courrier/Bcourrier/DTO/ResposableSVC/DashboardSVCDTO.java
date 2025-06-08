package com.courrier.Bcourrier.DTO.ResposableSVC;


import lombok.Data;

import java.util.List;

@Data
public class DashboardSVCDTO {
    private long arriveeEnCours;
    private long arriveeArchive;
    private long departEnCours;
    private long departArchive;
    private List<String> monthlyLabels;
    private List<Integer> monthlyArrivees;
    private List<Integer> monthlyDeparts;

}
