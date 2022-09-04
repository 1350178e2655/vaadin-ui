package com.partior.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DvPTradeDto {

    private String tradeId;
    private String initiatingBank;

    private String leg1Currency;
    private String leg1FromAccountId;
    private String leg1toAccountId;
    private BigDecimal leg1Amount;

    private String isin;
    private String leg2FromCsdAccountId;
    private String leg2toCsdAccountId;
    private BigDecimal leg2Quantity;
}
