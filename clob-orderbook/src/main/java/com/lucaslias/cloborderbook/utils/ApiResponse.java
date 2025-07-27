package com.lucaslias.cloborderbook.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(force = true)
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    boolean success;
    HttpStatus code;
    String error;
    T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.of(true, HttpStatus.OK, null, data);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, T data) {
        return ApiResponse.of(true, status, null, data);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String errorMessage) {
        return ApiResponse.of(false, status, errorMessage, null);
    }

    public boolean hasError() {
        return !success;
    }
}

