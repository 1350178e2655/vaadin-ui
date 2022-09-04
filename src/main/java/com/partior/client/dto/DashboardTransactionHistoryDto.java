package com.partior.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DashboardTransactionHistoryDto {

    private String transactionId;

    private String accountId;
    private String txnType;
    private String currency;

    private String payor;

    private String payee;

    private Double quantity;
    private String status;
    private String timeStamp;
    private String toAccountId;
    private String timestamp;


}
