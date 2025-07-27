package com.lucaslias.cloborderbook.service;

import com.lucaslias.cloborderbook.domain.enums.OrderStatus;
import com.lucaslias.cloborderbook.domain.enums.OrderType;
import com.lucaslias.cloborderbook.domain.model.Order;
import com.lucaslias.cloborderbook.domain.model.Trade;
import com.lucaslias.cloborderbook.domain.model.UserWallet;
import com.lucaslias.cloborderbook.repository.OrderRepository;
import com.lucaslias.cloborderbook.repository.TradeRepository;
import com.lucaslias.cloborderbook.repository.UserWalletRepository;
import com.lucaslias.cloborderbook.utils.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderMatchingService {

    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;
    private final UserWalletRepository userWalletRepository;
    private final UserWalletService userWalletService;

    @Transactional
    public ApiResponse<Trade> matchBuyOrder(Order buyOrder) {
        try {
            Optional<Order> optionalSellOrder = orderRepository
                    .findTopByTypeAndBaseCurrencyAndQuoteCurrencyAndPriceLessThanEqualAndStatusOrderByPriceAsc(
                            OrderType.SELL,
                            buyOrder.getBaseCurrency(),
                            buyOrder.getQuoteCurrency(),
                            buyOrder.getPrice(),
                            OrderStatus.OPEN
                    );

            if (optionalSellOrder.isEmpty()) {
                return ApiResponse.error(HttpStatus.NOT_FOUND, "No matching sell order found.");
            }

            Order sellOrder = optionalSellOrder.get();

            buyOrder.setStatus(OrderStatus.EXECUTED);
            sellOrder.setStatus(OrderStatus.EXECUTED);
            orderRepository.save(buyOrder);
            orderRepository.save(sellOrder);

            String currencyTrade = String.format("%s/%s", buyOrder.getBaseCurrency(), buyOrder.getQuoteCurrency());

            Trade trade = Trade.builder()
                    .buyOrder(buyOrder)
                    .sellOrder(sellOrder)
                    .amount(buyOrder.getAmount())
                    .price(sellOrder.getPrice())
                    .currencyTrade(currencyTrade)
                    .build();
            tradeRepository.save(trade);

            transferAssetsAfterMatch(buyOrder, sellOrder);

            return ApiResponse.success(trade);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process buy order matching: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<Trade> matchSellOrder(Order sellOrder) {
        try {
            Optional<Order> optionalBuyOrder = orderRepository
                    .findTopByTypeAndBaseCurrencyAndQuoteCurrencyAndPriceGreaterThanEqualAndStatusOrderByPriceDesc(
                            OrderType.BUY,
                            sellOrder.getBaseCurrency(),
                            sellOrder.getQuoteCurrency(),
                            sellOrder.getPrice(),
                            OrderStatus.OPEN
                    );

            if (optionalBuyOrder.isEmpty()) {
                return ApiResponse.error(HttpStatus.NOT_FOUND, "No matching buy order found.");
            }

            Order buyOrder = optionalBuyOrder.get();

            buyOrder.setStatus(OrderStatus.EXECUTED);
            sellOrder.setStatus(OrderStatus.EXECUTED);
            orderRepository.save(buyOrder);
            orderRepository.save(sellOrder);

            String currencyTrade = String.format("%s/%s", buyOrder.getBaseCurrency(), buyOrder.getQuoteCurrency());
            Trade trade = Trade.builder()
                    .buyOrder(buyOrder)
                    .sellOrder(sellOrder)
                    .amount(sellOrder.getAmount())
                    .price(buyOrder.getPrice())
                    .currencyTrade(currencyTrade)
                    .build();
            tradeRepository.save(trade);

            transferAssetsAfterMatch(buyOrder, sellOrder);

            return ApiResponse.success(trade);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process sell order matching: " + e.getMessage());
        }
    }

    @Transactional
    public void transferAssetsAfterMatch(Order buyOrder, Order sellOrder) {
        BigDecimal tradedAmount = sellOrder.getAmount();
        BigDecimal tradedPrice = buyOrder.getPrice();
        BigDecimal totalCost = tradedAmount.multiply(tradedPrice);

        Long buyerId = buyOrder.getUser().getId();
        Long sellerId = sellOrder.getUser().getId();

        UserWallet buyerBaseWallet = userWalletRepository
                .findByUserIdAndCurrency(buyerId, buyOrder.getBaseCurrency())
                .orElseGet(() -> UserWallet.builder()
                        .user(buyOrder.getUser())
                        .currency(buyOrder.getBaseCurrency())
                        .balance(BigDecimal.ZERO)
                        .build());

        UserWallet sellerQuoteWallet = userWalletRepository
                .findByUserIdAndCurrency(sellerId, sellOrder.getQuoteCurrency())
                .orElseGet(() -> UserWallet.builder()
                        .user(sellOrder.getUser())
                        .currency(sellOrder.getQuoteCurrency())
                        .balance(BigDecimal.ZERO)
                        .build());

        buyerBaseWallet.setBalance(buyerBaseWallet.getBalance().add(tradedAmount));
        sellerQuoteWallet.setBalance(sellerQuoteWallet.getBalance().add(totalCost));

        userWalletRepository.saveAll(List.of( buyerBaseWallet,sellerQuoteWallet));
    }

}
