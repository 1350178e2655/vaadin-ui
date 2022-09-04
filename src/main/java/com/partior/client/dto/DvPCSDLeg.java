package com.partior.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

@NoArgsConstructor
@Data
public class DvPCSDLeg {

    private CSDAccount fromAccount;

    private CSDAccount toAccount;

    private String isin;

    private Double quantity;

    private String committedToken;
}
