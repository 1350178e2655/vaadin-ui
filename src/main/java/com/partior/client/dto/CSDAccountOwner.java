package com.partior.client.dto;

import com.partior.client.dto.enums.Currency;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CSDAccountOwner {

   private String csdAdmin;

   private String currency;

   private String bank;

   private String bic;

   private String shortName;

}
