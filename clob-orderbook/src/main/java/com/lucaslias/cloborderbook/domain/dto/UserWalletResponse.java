package com.lucaslias.cloborderbook.domain.dto;

import com.lucaslias.cloborderbook.domain.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UserWalletResponse {
    private CurrencyType currency;
    private BigDecimal balance;
}
