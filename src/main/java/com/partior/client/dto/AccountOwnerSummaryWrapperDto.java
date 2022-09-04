package com.partior.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class AccountOwnerSummaryWrapperDto {
  private String centralBankParty;
  private List<AccountOwnerSummaryDto> accountOwnerSummaries;
}
