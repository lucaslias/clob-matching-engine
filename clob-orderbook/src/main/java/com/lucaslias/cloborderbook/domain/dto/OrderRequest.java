package com.lucaslias.cloborderbook.domain.dto;

import com.lucaslias.cloborderbook.domain.enums.CurrencyType;
import com.lucaslias.cloborderbook.domain.enums.OrderType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderRequest(
        @NotNull OrderType type,
        @NotNull CurrencyType baseCurrency,
        @NotNull CurrencyType  quoteCurrency,
        @NotNull @DecimalMin("0.0001") BigDecimal amount,
        @NotNull @DecimalMin("0.0001") BigDecimal price
) {}
