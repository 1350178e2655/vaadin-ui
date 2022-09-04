package com.partior.client.dto;


import com.partior.client.dto.enums.Currency;
import com.partior.client.dto.enums.ProposalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DepositResponseProposalDto {

    private CBDCAccount toAccount;
    private BigDecimal amount;
    private Currency currency;
    private ProposalStatus status;


}
