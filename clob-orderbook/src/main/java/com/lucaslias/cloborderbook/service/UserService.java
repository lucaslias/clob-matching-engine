package com.lucaslias.cloborderbook.service;

import com.lucaslias.cloborderbook.domain.dto.CreateUserRequest;
import com.lucaslias.cloborderbook.domain.dto.UserResponse;
import com.lucaslias.cloborderbook.domain.dto.UserWalletResponse;
import com.lucaslias.cloborderbook.domain.model.User;
import com.lucaslias.cloborderbook.repository.UserRepository;
import com.lucaslias.cloborderbook.utils.ApiResponse;
import com.lucaslias.cloborderbook.utils.JwtTokenUtil;
import com.lucaslias.cloborderbook.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public ApiResponse<String> createUser(CreateUserRequest request) {
        try{
            if (userRepository.existsByUsername(request.getUsername())) {
                return ApiResponse.error(HttpStatus.CONFLICT, "Username is already in use");
            }

            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .gender(request.getGender())
                    .build();

            User saved = userRepository.save(user);

            String token = jwtTokenUtil.generateToken(saved.getId());

            return ApiResponse.success(HttpStatus.CREATED, token);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to crate user: " + e.getMessage());
        }
    }

    public ApiResponse<String> login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtTokenUtil.generateToken(user.getId());
        return ApiResponse.success(HttpStatus.OK, token);
    }

    public ApiResponse<UserResponse> getProfile() {
        Long userId = SecurityUtils.getCurrentUserId();

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "User not found");
        }
        User user = optionalUser.get();

        List<UserWalletResponse> wallets = user.getWallet().stream()
                .map(wallet -> new UserWalletResponse(wallet.getCurrency(), wallet.getBalance()))
                .toList();

        UserResponse response = new UserResponse(
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getGender(),
                user.getCreatedAt(),
                wallets
        );

        return ApiResponse.success(response);
    }

}
