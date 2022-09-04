package com.partior.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CSDAccount {
    
    private CSDAccountOwner accountOwner;

    private String accountId;
}
