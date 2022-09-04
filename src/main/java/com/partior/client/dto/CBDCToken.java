package com.partior.client.dto;

import com.partior.client.dto.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@NoArgsConstructor
@Builder
@Data
public class CBDCToken {

    private String issuer;

    private CBDCAccount owner;

    private Currency currency;

    private BigDecimal amount;

    public CBDCToken(String issuer, CBDCAccount owner, Currency currency, BigDecimal amount) {
        this.issuer = issuer;
        this.owner = owner;
        this.currency = currency;
        this.amount = amount;
    }

}
