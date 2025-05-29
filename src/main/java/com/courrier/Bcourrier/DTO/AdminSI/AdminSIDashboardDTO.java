package com.courrier.Bcourrier.DTO.AdminSI;

import lombok.Data;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
@Data
public class AdminSIDashboardDTO {
    private long totalEmployees;
    private long toActivateEmployees;
    private long totalServices;
    private long totalRoles;
    private Map<String, Long> employeesPerRole; // For chart
}