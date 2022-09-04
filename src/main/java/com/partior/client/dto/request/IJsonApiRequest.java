package com.partior.client.dto.request;

import com.google.gson.Gson;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IJsonApiRequest {

    public String url();



    public  <T> T requestParam();

    List processResponse(List list);

}
