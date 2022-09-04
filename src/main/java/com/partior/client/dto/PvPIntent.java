package com.partior.client.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class PvPIntent {
    private CBDCAccount account;

    private BigDecimal amount;
}
