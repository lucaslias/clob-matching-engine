package com.lucaslias.cloborderbook.repository;

import com.lucaslias.cloborderbook.domain.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {
}
