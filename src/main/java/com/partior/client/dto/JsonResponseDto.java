package com.partior.client.dto;

import java.util.ArrayList;

public class JsonResponseDto {
    public ArrayList<Result> result;
    public int status;

    public static class Result{
        public String agreementText;
        public String contractId;
        public Key key;
        public ArrayList<String> observers;
        public Object payload;
        public ArrayList<String> signatories;
        public String templateId;
        public static class Key{
            public String _1;
            public String _2;
        }
    }
}
