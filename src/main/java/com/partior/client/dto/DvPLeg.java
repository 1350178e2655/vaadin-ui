package com.partior.client.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
public class DvPLeg {

    private  CBDCAccount fromAccount;

    private  CBDCAccount toAccount;

    private  BigDecimal amount;

 // public final Optional<LockedCBDCToken.ContractId> committedToken;

    public DvPLeg(CBDCAccount fromAccount, CBDCAccount toAccount, BigDecimal amount
                  //              ,Optional<LockedCBDCToken.ContractId> committedToken
    ) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
    //    this.committedToken = committedToken;

    }

}
