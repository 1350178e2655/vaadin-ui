package com.partior.client.dto;

import com.partior.client.util.JsonClientWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PvPProposal {

    private PvPIntent sendIntention;

    private PvPIntent receiveIntention;

    private String counterpartyBank;

    private List<String> signatories;
    
}
