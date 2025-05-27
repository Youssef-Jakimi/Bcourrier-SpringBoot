// src/main/java/com/courrier/Bcourrier/DTO/AdminBC/StatsDTO.java
package com.courrier.Bcourrier.DTO.AdminBC;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class StatsDTO {
    private int totalEmployes;
    private int totalServices;
    private int totalUrgentCourriers;
    private List<String> monthlyLabels;      // ex: ["Jan", "Fév", ...]
    private List<Integer> monthlyArrivees;   // one per month, same order as labels
    private List<Integer> monthlyDeparts;
    private Map<String, Integer> confidentialiteCounts; // ex: {"ROUTINE": 10, "SECRET": 7, ...}
    private Map<String, Map<String,Integer>> courriersByService;
    private Map<String, Integer> courriersByEmploye;    // ex: {"Alice": 5, ...}
    private double traitementMoyenJours;                // e.g. 3.5
}
