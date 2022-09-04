package com.partior.client.dto;


import com.partior.client.dto.enums.Currency;
import com.partior.client.dto.enums.ProposalStatus;
import com.partior.client.dto.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RtgsTransactionResponseProposalDto {

    private CBDCAccount account;
    private BigDecimal amount;
    private Currency currency;
    private ProposalStatus status;
    private TransactionType type;


}
