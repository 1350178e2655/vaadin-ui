package com.partior.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@NoArgsConstructor
@Data
public class PvpContract {

    private String contractId;

    private  PvP data;

    private  Optional<String> agreementText;

    private  Optional<Tuple2<String, List<String>>> key;

    private  Set<String> signatories;

    private  Set<String> observers;

}
