package com.partior.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DashboardAccountOverviewDto {

    private String accountType;
    private String country;



    private String bic;
    private String bankName;
    private String balance;
    private String deposit;
    private String withdrawals;
    private String sent;
    private String received;


    public DashboardAccountOverviewDto(String accountType, String bic, String bankName, String balance,
                                       String deposit, String withdrawals, String sent, String received) {
        this.accountType = accountType;
        this.bic = bic;
        this.bankName = bankName;
        this.balance = balance;
        this.deposit = deposit;
        this.withdrawals = withdrawals;
        this.sent = sent;
        this.received = received;
    }
}
