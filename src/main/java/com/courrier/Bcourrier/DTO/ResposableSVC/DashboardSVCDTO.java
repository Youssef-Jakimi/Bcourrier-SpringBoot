package com.courrier.Bcourrier.DTO.ResposableSVC;


import lombok.Data;

@Data
public class DashboardSVCDTO {
    private long arriveeEnCours;
    private long arriveeArchive;
    private long departEnCours;
    private long departArchive;
}
