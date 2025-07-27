package com.lucaslias.cloborderbook.service;

import com.lucaslias.cloborderbook.domain.dto.UserResponse;
import com.lucaslias.cloborderbook.domain.dto.UserWalletRequest;
import com.lucaslias.cloborderbook.domain.model.User;
import com.lucaslias.cloborderbook.domain.model.UserWallet;
import com.lucaslias.cloborderbook.repository.UserRepository;
import com.lucaslias.cloborderbook.repository.UserWalletRepository;
import com.lucaslias.cloborderbook.utils.ApiResponse;
import com.lucaslias.cloborderbook.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserWalletService {

    private final UserRepository userRepository;
    private final UserWalletRepository userWalletRepository;
    private final UserService userService;

    public ApiResponse<UserResponse> addWallet(UserWalletRequest request) {

        try{
            Long userId = SecurityUtils.getCurrentUserId();

            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                return ApiResponse.error(HttpStatus.NOT_FOUND, "User not found");
            }

            Optional<UserWallet> existingWalletOpt = userWalletRepository.findByUserIdAndCurrency(userId, request.currency());

            if (existingWalletOpt.isPresent()) {
                UserWallet existingWallet = existingWalletOpt.get();
                existingWallet.setBalance(existingWallet.getBalance().add(request.balance()));
                userWalletRepository.save(existingWallet);
            } else {
                UserWallet wallet = UserWallet.builder()
                        .user(optionalUser.get())
                        .currency(request.currency())
                        .balance(request.balance())
                        .build();

                userWalletRepository.save(wallet);
            }

            return userService.getProfile();
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed credit wallet: " + e.getMessage());
        }
    }

    public ApiResponse<UserResponse> debitWallet(UserWalletRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();

            UserWallet wallet = userWalletRepository
                    .findByUserIdAndCurrency(userId, request.currency())
                    .orElseThrow(() -> new RuntimeException("Carteira não encontrada para essa moeda."));

            if (wallet.getBalance().compareTo(request.balance()) < 0) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Saldo insuficiente para débito.");
            }

            BigDecimal newBalance = wallet.getBalance().subtract(request.balance());

            userWalletRepository.save(wallet);

            return userService.getProfile();
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed debit wallet: " + e.getMessage());
        }
    }
}