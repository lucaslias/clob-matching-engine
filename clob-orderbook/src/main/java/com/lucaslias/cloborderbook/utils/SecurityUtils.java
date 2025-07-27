package com.lucaslias.cloborderbook.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Usuário não autenticado.");
        }
        return Long.parseLong(auth.getPrincipal().toString());
    }
}
