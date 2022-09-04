package com.partior.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class LockedCBDCToken {

    private CBDCToken cbdcToken;

    private List<String> lockedFor;

    public LockedCBDCToken(CBDCToken cbdcToken, List<String> lockedFor) {
        this.cbdcToken = cbdcToken;
        this.lockedFor = lockedFor;
    }

}
