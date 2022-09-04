package com.partior.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class CsdBalanceResponseDto {
    private String accountId;
    private String isin;
    private BigDecimal quantity;

    public BigDecimal getQuantity(){

        return quantity!=null?quantity.setScale(0,BigDecimal.ROUND_DOWN):null;
    }
}
