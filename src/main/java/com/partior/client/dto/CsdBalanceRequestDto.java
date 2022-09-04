package com.partior.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class CsdBalanceRequestDto {
    private String bankParty;
    private String accountId;
    private String isin;

    public CsdBalanceRequestDto(String bankParty){
        this.bankParty = bankParty;
    }

}
