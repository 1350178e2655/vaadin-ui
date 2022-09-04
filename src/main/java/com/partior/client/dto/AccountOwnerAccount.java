package com.partior.client.dto;


import com.vaadin.flow.component.textfield.TextField;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountOwnerAccount {

    private String accountOwnerName;
    private String cbdcAccountNumber;
    private String rtgsAccountNumber;
    private String currency;

}
