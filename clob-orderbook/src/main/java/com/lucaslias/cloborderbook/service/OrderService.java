package com.lucaslias.cloborderbook.service;

import com.lucaslias.cloborderbook.domain.dto.OrderDTO;
import com.lucaslias.cloborderbook.domain.dto.OrderResponseWrapper;
import com.lucaslias.cloborderbook.domain.enums.CurrencyType;
import com.lucaslias.cloborderbook.domain.model.Trade;
import com.lucaslias.cloborderbook.domain.model.User;
import com.lucaslias.cloborderbook.domain.dto.OrderRequest;
import com.lucaslias.cloborderbook.domain.enums.OrderStatus;
import com.lucaslias.cloborderbook.domain.enums.OrderType;
import com.lucaslias.cloborderbook.domain.model.Order;
import com.lucaslias.cloborderbook.domain.model.UserWallet;
import com.lucaslias.cloborderbook.repository.OrderRepository;
import com.lucaslias.cloborderbook.repository.UserWalletRepository;
import com.lucaslias.cloborderbook.utils.ApiResponse;
import com.lucaslias.cloborderbook.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserWalletRepository userWalletRepository;
    private final OrderRepository orderRepository;
    private final OrderMatchingService orderMatchingService;

    public ApiResponse<String> createOrder(OrderRequest request) {
        if (request.type() == OrderType.BUY) {
            return createBuyOrder(request);
        } else if (request.type() == OrderType.SELL) {
            return createSellOrder(request);
        } else {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid order type.");
        }
    }

    @Transactional
    private ApiResponse<String> createBuyOrder(OrderRequest request) {

        try{
            Long userId = SecurityUtils.getCurrentUserId();

            BigDecimal totalCost = request.amount().multiply(request.price()); // total = amount * price

            UserWallet wallet = userWalletRepository
                    .findByUserIdAndCurrency(userId, request.quoteCurrency())
                    .orElse(null);

            if (wallet == null) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Wallet not found for currency " + request.quoteCurrency());
            }

            if (wallet.getBalance().compareTo(totalCost) < 0) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Insufficient funds in wallet");
            }

            wallet.setBalance(wallet.getBalance().subtract(totalCost));
            userWalletRepository.save(wallet);

            Order order = Order.builder()
                    .user(User.builder().id(userId).build())
                    .type(OrderType.BUY)
                    .baseCurrency(request.baseCurrency())
                    .quoteCurrency(request.quoteCurrency())
                    .amount(request.amount())
                    .price(request.price())
                    .status(OrderStatus.OPEN)
                    .build();

            orderRepository.save(order);

            ApiResponse<Trade> trade = orderMatchingService.matchBuyOrder(order);

            if (trade.hasError()) {
                return ApiResponse.success(HttpStatus.CREATED, "Order created successfully");
            }

            return ApiResponse.success(HttpStatus.OK, "Order created and matched successfully");
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to crate buy order: " + e.getMessage());
        }
    }

    @Transactional
    private ApiResponse<String> createSellOrder(OrderRequest request) {
        try{
            Long userId = SecurityUtils.getCurrentUserId();

            BigDecimal amountToSell = request.amount();

            UserWallet wallet = userWalletRepository
                    .findByUserIdAndCurrency(userId, request.baseCurrency())
                    .orElse(null);

            if (wallet == null) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST,
                        "Wallet not found for currency " + request.baseCurrency());
            }

            if (wallet.getBalance().compareTo(amountToSell) < 0) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST,
                        "Insufficient balance to sell " + amountToSell + " " + request.baseCurrency());
            }

            wallet.setBalance(wallet.getBalance().subtract(amountToSell));
            userWalletRepository.save(wallet);

            Order order = Order.builder()
                    .user(User.builder().id(userId).build())
                    .type(OrderType.SELL)
                    .baseCurrency(request.baseCurrency())
                    .quoteCurrency(request.quoteCurrency())
                    .amount(request.amount())
                    .price(request.price())
                    .status(OrderStatus.OPEN)
                    .build();

            orderRepository.save(order);

            ApiResponse<Trade> trade = orderMatchingService.matchSellOrder(order);

            if (trade.hasError()) {
                return ApiResponse.success(HttpStatus.CREATED, "Order created successfully");
            }

            return ApiResponse.success(HttpStatus.OK, "Order created and matched successfully");
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to crate sell order: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<String> cancelOrder(Long orderId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Order not found.");
        }

        if (!order.getUser().getId().equals(currentUserId)) {
            return ApiResponse.error(HttpStatus.FORBIDDEN, "You can only cancel your own orders.");
        }

        if (order.getStatus() != OrderStatus.OPEN) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Only open orders can be cancelled.");
        }

        CurrencyType refundCurrency;
        BigDecimal refundAmount;

        if (order.getType() == OrderType.BUY) {
            refundCurrency = order.getQuoteCurrency();
            refundAmount = order.getAmount().multiply(order.getPrice());
        } else {
            refundCurrency = order.getBaseCurrency();
            refundAmount = order.getAmount();
        }

        UserWallet wallet = userWalletRepository
                .findByUserIdAndCurrency(currentUserId, refundCurrency)
                .orElse(null);

        if (wallet == null) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Wallet not found for refund.");
        }

        wallet.setBalance(wallet.getBalance().add(refundAmount));
        userWalletRepository.save(wallet);

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return ApiResponse.success(HttpStatus.OK, "Order cancelled successfully and funds refunded.");
    }

    public OrderResponseWrapper getUserOrdersWithFilters(OrderStatus status, OrderType type) {
        Long userId = SecurityUtils.getCurrentUserId();

        List<Order> orders = orderRepository.findByUserWithFilters(userId, status, type);

        List<OrderDTO> buyOrders = new ArrayList<>();
        List<OrderDTO> sellOrders = new ArrayList<>();

        for (Order order : orders) {
            OrderDTO dto = mapToDTO(order);
            if (order.getType() == OrderType.BUY) {
                buyOrders.add(dto);
            } else {
                sellOrders.add(dto);
            }
        }

        return OrderResponseWrapper.builder()
                .buyOrders(buyOrders)
                .sellOrders(sellOrders)
                .build();
    }

    public OrderResponseWrapper getAllOrdersWithFilters(OrderStatus status, OrderType type, CurrencyType baseCurrency, CurrencyType quoteCurrency) {
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null)
                predicates.add(cb.equal(root.get("status"), status));

            if (type != null)
                predicates.add(cb.equal(root.get("type"), type));

            if (baseCurrency != null)
                predicates.add(cb.equal(root.get("baseCurrency"), baseCurrency));

            if (quoteCurrency != null)
                predicates.add(cb.equal(root.get("quoteCurrency"), quoteCurrency));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Order> allOrders = orderRepository.findAll(spec);

        List<OrderDTO> buyOrders = allOrders.stream()
                .filter(order -> order.getType() == OrderType.BUY)
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        List<OrderDTO> sellOrders = allOrders.stream()
                .filter(order -> order.getType() == OrderType.SELL)
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new OrderResponseWrapper(buyOrders, sellOrders);
    }

    private OrderDTO mapToDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .type(order.getType())
                .baseCurrency(order.getBaseCurrency())
                .quoteCurrency(order.getQuoteCurrency())
                .amount(order.getAmount())
                .price(order.getPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }

}
