package com.partior.client.dto;


import com.partior.client.dto.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BalanceResponseDto {
  private String bankName;
  private String accountId;
  private Currency currency;
  private BigDecimal amount;
  private Map<String, BalanceResponseDto> balances;

  public BalanceResponseDto(String accountId, Currency currency, BigDecimal amount) {
    this.accountId = accountId;
    this.currency = currency;
    this.amount = amount;
  }
}
