package com.lucaslias.cloborderbook.controller;

import com.lucaslias.cloborderbook.domain.dto.OrderRequest;
import com.lucaslias.cloborderbook.domain.dto.OrderResponseWrapper;
import com.lucaslias.cloborderbook.domain.enums.CurrencyType;
import com.lucaslias.cloborderbook.domain.enums.OrderStatus;
import com.lucaslias.cloborderbook.domain.enums.OrderType;
import com.lucaslias.cloborderbook.service.OrderService;
import com.lucaslias.cloborderbook.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Order Controller", description = "Endpoints for managing buy/sell orders, matching, and cancellation")
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create a new order", description = "Submits a new buy or sell order.")
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createOrder(@RequestBody @Valid OrderRequest request) {
        ApiResponse<String> response = orderService.createOrder(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @Operation(summary = "Cancel order", description = "Cancels an order and refunds the user's wallet.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> cancelOrder(@PathVariable Long id) {
        ApiResponse<String> response = orderService.cancelOrder(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @Operation(summary = "Get user orders", description = "Returns all orders from the current user, optionally filtered by status and type.")
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<OrderResponseWrapper>> getUserOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) OrderType type) {
        OrderResponseWrapper response = orderService.getUserOrdersWithFilters(status, type);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get all orders", description = "Returns all orders filtered by status, type, baseCurrency, and quoteCurrency.")
    @GetMapping
    public ResponseEntity<ApiResponse<OrderResponseWrapper>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) OrderType type,
            @RequestParam(required = false) CurrencyType baseCurrency,
            @RequestParam(required = false) CurrencyType quoteCurrency) {
        OrderResponseWrapper response = orderService.getAllOrdersWithFilters(status, type, baseCurrency, quoteCurrency);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
