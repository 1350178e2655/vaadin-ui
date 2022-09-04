package com.partior.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data

public class AccountOwnerSummaryDto {
  private String partyString;
  private BigDecimal totalBalance;
  private BigDecimal totalDeposits;
  private BigDecimal totalWithdrawals;
  private BigDecimal totalSent;
  private BigDecimal totalReceived;
}
