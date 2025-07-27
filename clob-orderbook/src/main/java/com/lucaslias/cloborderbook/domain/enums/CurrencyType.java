package com.lucaslias.cloborderbook.domain.enums;

import lombok.Getter;

@Getter
public enum CurrencyType {
    BTC("BTC", "Bitcoin"),
    BRL("BRL", "Real Brasileiro"),
    ETH("ETH", "Ethereum"),
    USD("USD", "Dólar Americano");

    private final String code;
    private final String description;

    CurrencyType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        return code;
    }

    public static CurrencyType fromCode(String code) {
        for (CurrencyType value : CurrencyType.values()) {
            if (value.code.equalsIgnoreCase(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid currency code: " + code);
    }
}
