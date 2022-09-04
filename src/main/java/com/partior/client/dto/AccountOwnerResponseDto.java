package com.partior.client.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountOwnerResponseDto {


    private String shortName;
    private String bic;
    private String sponsorParty;
    private String centralBankParty;
    private String currency;
    private String rtgsAccountId;
    private boolean local;
    private String accountId;
    private String bank;

    public AccountOwnerResponseDto(String shortName, String bic, String sponsorParty, String centralBankParty, String currency, String rtgsAccountId ) {
        this(shortName,  bic,  sponsorParty,  centralBankParty,  currency,  rtgsAccountId,false);
    }
    public AccountOwnerResponseDto(String shortName, String bic, String sponsorParty, String centralBankParty, String currency, String rtgsAccountId, boolean isLocal) {
        this.shortName = shortName;
        this.bic = bic;
        this.sponsorParty = sponsorParty;
        this.centralBankParty = centralBankParty;
        this.currency = currency;
        this.rtgsAccountId = rtgsAccountId;
        this.local = isLocal;
    }


    public void setLocal(boolean local) {
        this.local = local;
    }

}
