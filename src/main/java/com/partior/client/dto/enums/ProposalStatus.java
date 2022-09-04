package com.partior.client.dto.enums;

public enum ProposalStatus {

    NOTSTARTED("NOTSTARTED"),

    INITIATED("INITIATED"),

    ACCEPTED("ACCEPTED"),

    CANCELLED("CANCELLED");

        private final String status;

        ProposalStatus(String status) {
            this.status = status;
        }

        public boolean equalsName(String other) {
            return status.equals(other);
        }

        public String toString() {
            return this.status;
        }


}
