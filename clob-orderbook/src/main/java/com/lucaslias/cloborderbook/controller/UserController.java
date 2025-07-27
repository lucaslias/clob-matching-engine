package com.lucaslias.cloborderbook.controller;

import com.lucaslias.cloborderbook.domain.dto.CreateUserRequest;
import com.lucaslias.cloborderbook.domain.dto.LoginRequest;
import com.lucaslias.cloborderbook.domain.dto.UserResponse;
import com.lucaslias.cloborderbook.service.UserService;
import com.lucaslias.cloborderbook.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "User Management", description = "Endpoints for user registration, authentication, and profile access")
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register a new user", description = "Creates a new user account in the system.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> createUser(@RequestBody @Valid CreateUserRequest request) {
        ApiResponse<String> response = userService.createUser(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @Operation(summary = "User login", description = "Authenticates a user and returns a token.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody @Valid LoginRequest request) {
        ApiResponse<String> response = userService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @Operation(summary = "Get current user", description = "Returns details of the currently authenticated user.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        ApiResponse<UserResponse> response = userService.getProfile();
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
