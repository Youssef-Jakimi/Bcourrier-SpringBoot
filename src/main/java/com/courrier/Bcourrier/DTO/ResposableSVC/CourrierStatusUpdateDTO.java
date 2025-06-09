package com.courrier.Bcourrier.DTO.ResposableSVC;


import lombok.Data;

@Data
public class CourrierStatusUpdateDTO {
    private int courrierId;
    private String newStatus;
}

