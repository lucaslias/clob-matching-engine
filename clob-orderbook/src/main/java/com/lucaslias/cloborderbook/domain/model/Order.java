package com.lucaslias.cloborderbook.domain.model;

import com.lucaslias.cloborderbook.domain.enums.CurrencyType;
import com.lucaslias.cloborderbook.domain.enums.OrderStatus;
import com.lucaslias.cloborderbook.domain.enums.OrderType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private OrderType type;

    @Column(name = "base_currency", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private CurrencyType  baseCurrency; //	A moeda que será negociada

    @Column(name = "quote_currency", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private CurrencyType quoteCurrency; //A moeda usada para avaliar/pagar a baseCurrency

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount; //A quantidade da baseCurrency a ser negociada

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal price; //O preço unitário da baseCurrency

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = OrderStatus.OPEN;
        }
    }
}
