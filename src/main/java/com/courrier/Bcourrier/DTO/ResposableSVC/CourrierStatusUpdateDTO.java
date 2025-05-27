package com.courrier.Bcourrier.DTO.ResposableSVC;


import lombok.Data;

@Data
public class CourrierStatusUpdateDTO {
    private Long courrierId;
    private String newStatus;
}

