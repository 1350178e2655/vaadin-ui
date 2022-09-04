package com.partior.client.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AccountOwnerDto {


    private String shortName;
    private String bic;
    private String sponsorParty;
    private String centralBankParty;
    private String currency;
    private String rtgsAccountId;
    private boolean local;
    private String bank;
    private String sponsor;
    private boolean isLocal;


    public AccountOwnerDto(String shortName, String bic, String sponsorParty, String centralBankParty, String currency, String rtgsAccountId ) {
        this(shortName,  bic,  sponsorParty,  centralBankParty,  currency,  rtgsAccountId,false);
    }
    public AccountOwnerDto(String shortName, String bic, String sponsorParty, String centralBankParty, String currency, String rtgsAccountId, boolean isLocal) {
        this.shortName = shortName;
        this.bic = bic;
        this.sponsorParty = sponsorParty;
        this.centralBankParty = centralBankParty;
        this.currency = currency;
        this.rtgsAccountId = rtgsAccountId;
        this.local = isLocal;
    }


    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
        this.isLocal = local;
    }

}
