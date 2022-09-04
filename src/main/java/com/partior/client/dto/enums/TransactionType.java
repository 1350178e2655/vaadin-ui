package com.partior.client.dto.enums;

public enum TransactionType  {

    WITHDRAWAL("WITHDRAWAL"),

    DEPOSIT("DEPOSIT"),

    TRANSFER("TRANSFER");

    private final String transactionType;

    TransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public boolean equalsName(String other) {
        return transactionType.equals(other);
    }

    public String toString() {
        return this.transactionType;
    }


}