package com.partior.client.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

@Builder
@Data
@NoArgsConstructor
public class PvPLeg {

    private  CBDCAccount fromAccount;

    private  CBDCAccount toAccount;

    private  BigDecimal amount;

 // public final Optional<LockedCBDCToken.ContractId> committedToken;

    public PvPLeg(CBDCAccount fromAccount, CBDCAccount toAccount, BigDecimal amount
    //              ,Optional<LockedCBDCToken.ContractId> committedToken
    ) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
    //    this.committedToken = committedToken;

    }

}
