// src/main/java/com/courrier/Bcourrier/DTO/RH/RhDashboardDTO.java
package com.courrier.Bcourrier.DTO.RH;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RhDashboardDTO {
    private long totalEmployes;
    private long totalCourriersArchives;
    private long totalCourriersTraites;
    private long totalNonTraites;
    private List<Map<String, Object>> last3Archives;
    private Map<String, Long> monthlyTrend;
}
