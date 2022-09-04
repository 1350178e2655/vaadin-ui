package com.partior.client.dto.request;

import com.google.gson.Gson;
import com.partior.client.dto.AccountOwnerResponseDto;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class RequestAccountOwners implements IJsonApiRequest{

    private String centralBankUser;

    public RequestAccountOwners(String centralBankUser){
        this.centralBankUser = centralBankUser;
    }



    public String url() {
        return "/api/admin/listAccountOwners/" + centralBankUser;
    }

    @Override
    public Map requestParam() {
        return null;
    }


    @Override
    public List processResponse(List list) {
        return list;
    }


    public List  processResponsec(String jsonResponse ) {

        log.info(jsonResponse);

        Gson gson = new Gson();

        List<AccountOwnerResponseDto> x =  gson.fromJson(jsonResponse,List.class );

        List<AccountOwnerResponseDto> list = new ArrayList<>();
        return list;
    }


}
