package com.partior.client.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DvP {

    private String tradeId;

    private DvPCBDCLeg leg1;

    private DvPCSDLeg leg2;

    private String status;

    private String contractId;
}
