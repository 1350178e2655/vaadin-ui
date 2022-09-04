package com.partior.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RtgsIntegration{
    private String cbdc;
    private String rtgs;
    private String operatingHours;
    private String status;

}
