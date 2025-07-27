package com.lucaslias.cloborderbook.repository;

import com.lucaslias.cloborderbook.domain.enums.CurrencyType;
import com.lucaslias.cloborderbook.domain.enums.OrderStatus;
import com.lucaslias.cloborderbook.domain.enums.OrderType;
import com.lucaslias.cloborderbook.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Optional<Order> findTopByTypeAndBaseCurrencyAndQuoteCurrencyAndPriceLessThanEqualAndStatusOrderByPriceAsc(
            OrderType type,
            CurrencyType baseCurrency,
            CurrencyType quoteCurrency,
            BigDecimal price,
            OrderStatus status
    );

    Optional<Order> findTopByTypeAndBaseCurrencyAndQuoteCurrencyAndPriceGreaterThanEqualAndStatusOrderByPriceDesc(
            OrderType type,
            CurrencyType baseCurrency,
            CurrencyType quoteCurrency,
            BigDecimal price,
            OrderStatus status
    );

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId " +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND (:type IS NULL OR o.type = :type)")
    List<Order> findByUserWithFilters(@Param("userId") Long userId,
                                      @Param("status") OrderStatus status,
                                      @Param("type") OrderType type);


}
