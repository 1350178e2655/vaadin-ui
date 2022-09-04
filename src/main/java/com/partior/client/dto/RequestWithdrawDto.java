package com.partior.client.dto;


import com.partior.client.dto.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RequestWithdrawDto {
    private String accountId;
    private String bankParty;
    private BigDecimal amount;
    private Currency currency;
    private String centralBank;


    public RequestWithdrawDto(String accountId, String bankParty, BigDecimal amount, Currency currency) {
        this.accountId = accountId;
        this.bankParty = bankParty;
        this.amount = amount;
        this.currency = currency;

    }


}
