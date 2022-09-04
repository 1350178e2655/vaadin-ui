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
public class RequestTransferDto {
  private String fromBank;
  private String fromAccountId;
  private String toBank;
  private String toAccountId;
  private Currency currency;
  private BigDecimal amount;

  public RequestTransferDto(String fromBank, String fromAccountId, String toBank, String toAccountId, Currency currency, BigDecimal amount) {
    this.fromBank = fromBank;
    this.fromAccountId = fromAccountId;
    this.toBank = toBank;
    this.toAccountId = toAccountId;
    this.currency = currency;
    this.amount = amount;
  }
}
