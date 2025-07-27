package com.lucaslias.cloborderbook.domain.dto;

import com.lucaslias.cloborderbook.domain.enums.CurrencyType;
import com.lucaslias.cloborderbook.domain.enums.OrderStatus;
import com.lucaslias.cloborderbook.domain.enums.OrderType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderDTO {
    private Long id;
    private OrderType type;
    private CurrencyType baseCurrency;
    private CurrencyType quoteCurrency;
    private BigDecimal amount;
    private BigDecimal price;
    private OrderStatus status;
    private LocalDateTime createdAt;
}
