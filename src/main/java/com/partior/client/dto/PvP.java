package com.partior.client.dto;


import com.partior.client.dto.enums.PvPStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PvP {

    private PvPLeg leg1;
    private PvPLeg leg2;
    private String status;
    private String tradeId;
    private String contractId;
    private String rates;



    public PvP(PvPLeg leg1, PvPLeg leg2, String status) {
        this.leg1 = leg1;
        this.leg2 = leg2;
        this.status = status;
    }
}
