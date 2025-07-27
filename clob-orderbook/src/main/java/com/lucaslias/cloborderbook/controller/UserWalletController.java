package com.lucaslias.cloborderbook.controller;

import com.lucaslias.cloborderbook.domain.dto.UserResponse;
import com.lucaslias.cloborderbook.domain.dto.UserWalletRequest;
import com.lucaslias.cloborderbook.service.UserWalletService;
import com.lucaslias.cloborderbook.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "User Wallet", description = "Endpoints for wallet credit and debit operations")
@RequestMapping("api/wallet")
@RequiredArgsConstructor
public class UserWalletController {

    private final UserWalletService walletService;

    @Operation(summary = "Credit to wallet", description = "Adds an amount to the user's wallet.")
    @PostMapping("/credit")
    public ResponseEntity<ApiResponse<UserResponse>> addWallet(@RequestBody @Valid UserWalletRequest request) {
        ApiResponse<UserResponse> response = walletService.addWallet(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @Operation(summary = "Debit from wallet", description = "Subtracts an amount from the user's wallet.")
    @PostMapping("/debit")
    public ResponseEntity<ApiResponse<UserResponse>> debitWallet(@RequestBody @Valid UserWalletRequest request) {
        ApiResponse<UserResponse> response = walletService.debitWallet(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

}
