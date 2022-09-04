package com.partior.client.dto;

import com.partior.client.dto.enums.Currency;
import com.partior.client.dto.enums.ProposalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountOwnerOnboardingResponse {

    private  String centralBank;
    private  String rtgsAccountId;
    private  Currency currency;
    private  Boolean isLocal;
    private  String bank;
    private  String bic;
    private  String shortName;
    private  String sponsor;
    private  ProposalStatus status;

    public AccountOwnerOnboardingResponse(String centralBank, String rtgsAccountId, Currency currency, Boolean isLocal, String bank, String bic, String shortName, String sponsor, ProposalStatus status) {
        this.centralBank = centralBank;
        this.rtgsAccountId = rtgsAccountId;
        this.currency = currency;
        this.isLocal = isLocal;
        this.bank = bank;
        this.bic = bic;
        this.shortName = shortName;
        this.sponsor = sponsor;
        this.status = status;
    }


}
