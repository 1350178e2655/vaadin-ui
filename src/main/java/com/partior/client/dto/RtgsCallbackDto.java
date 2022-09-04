package com.partior.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RtgsCallbackDto {

    private String response;
    private String receipt;


    public RtgsCallbackDto(String response, String receipt){
        this.response = response;
        this.receipt  = receipt;
    }
}
