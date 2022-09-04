package com.partior.client.dto.enums;

public enum Currency {

    USD("USD"),

    EUR("EUR"),

    GBP("GBP"),

    INR("INR"),

    IDR("IDR"),

    SGD("SGD");


        private final String currency;

        Currency(String currency) {
            this.currency = currency;
        }

        public boolean equalsName(String other) {
            return currency.equals(other);
        }

        public String toString() {
            return this.currency;
        }


}
