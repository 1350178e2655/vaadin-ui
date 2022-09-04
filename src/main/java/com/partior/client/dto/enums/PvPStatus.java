package com.partior.client.dto.enums;

public enum PvPStatus {

    FUNDING("FUNDING"),

    COMPLETED("COMPLETED"),

    FAILED("FAILED");

    private final String status;

    PvPStatus(String status) {
        this.status = status;
    }

    public boolean equalsName(String other) {
        return status.equals(other);
    }

    public String toString() {
        return this.status;
    }

}
