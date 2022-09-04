package com.partior.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TransactionResponseDto {

    private String transactionId;
    private String workflowId;
    private String commandId;
    private long effectiveAt;
}
