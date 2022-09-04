package com.partior.client.dto;



import com.partior.client.dto.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class BalanceRequestDto {
    private String bankParty;
    private String accountId;
    private Currency currency;

    public BalanceRequestDto(String bankParty) {
        this.bankParty = bankParty;
    }

}

