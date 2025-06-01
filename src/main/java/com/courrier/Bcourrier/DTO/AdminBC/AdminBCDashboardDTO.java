package com.courrier.Bcourrier.DTO.AdminBC;


import com.courrier.Bcourrier.Entities.Courrier;
import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class AdminBCDashboardDTO {
    private long totalCourriersArrivee;
    private long totalCourriersDepart;
    private long totalArriveeArchives;
    private long totalDepartArchives;
    private List<Map<String, Object>> last3Courriers;
    private List<String> monthlyLabels;      // e.g. ["Jan", "Feb", ...]
    private List<Integer> monthlyArrivees;   // counts of ARRIVEE per month
    private List<Integer> monthlyDeparts;
}

