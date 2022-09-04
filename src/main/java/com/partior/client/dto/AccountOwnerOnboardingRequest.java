package com.partior.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountOwnerOnboardingRequest {

    private String bankParty;
    private String centralBankParty;

}
