package com.partior.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PvpIntegrationDto {

    private String domain;
    private String transaction;
    private String type;
    private String from;
    private String to;
    private String amount;
    private String currency;
    private String status;

    private PvP pvp;

 //   private String tradeId;



}