package com.partior.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionReceiptDto {


 private String assetIssuer;
 private String currency;
 private String externalReference;

 private String fromAccountId;
 private String fromParty;
 private String quantity;
 private String status;
 private String statusReason;
 private String toAccountId;
 private String toParty;
 private String txnType;
 private String timestamp;

}
