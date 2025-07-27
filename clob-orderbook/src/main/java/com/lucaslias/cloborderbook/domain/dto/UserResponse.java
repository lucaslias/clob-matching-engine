package com.lucaslias.cloborderbook.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class UserResponse {
    private String name;
    private String email;
    private String phone;
    private String gender;
    private LocalDateTime createdAt;
    private List<UserWalletResponse> wallet;
}
