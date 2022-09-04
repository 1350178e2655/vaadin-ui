package com.partior.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

@NoArgsConstructor
@Data
public class DvPCBDCLeg {

    private CBDCAccount fromAccount;

    private CBDCAccount toAccount;

    private BigDecimal amount;

    private String committedToken;
    
}
