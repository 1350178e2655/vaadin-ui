package com.partior.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DisableAccountOwnerResponseDto {
    private String bic;
    private String shortName;
    private String centralBankParty;
    private boolean disable;
}
