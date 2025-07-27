package com.lucaslias.cloborderbook.repository;

import com.lucaslias.cloborderbook.domain.enums.CurrencyType;
import com.lucaslias.cloborderbook.domain.model.UserWallet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {

    List<UserWallet> findByUserId(Long userId);
    Optional<UserWallet> findByUserIdAndCurrency(Long userId, CurrencyType currency);
}
