package com.lucaslias.cloborderbook.domain.dto;

import com.lucaslias.cloborderbook.domain.enums.CurrencyType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

//Opcao de forma mais concisa, imutável e declarativa de criar classes que existem apenas para transportar dados
public record UserWalletRequest(
        @NotNull CurrencyType currency,
        @NotNull @DecimalMin(value = "0.01")BigDecimal balance
) {}
