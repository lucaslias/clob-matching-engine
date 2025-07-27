package com.lucaslias.cloborderbook.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Optional;

public class ResponseHandler {

    private ResponseHandler() {}

    public static <T> ResponseEntity<ApiResponse<T>> handleResponse(T body) {
        if (body == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Resource not found"));
        }

        if (body instanceof Collection && CollectionUtils.isEmpty((Collection<?>) body)) {
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(ApiResponse.error(HttpStatus.NO_CONTENT, "No content available"));
        }
        return ResponseEntity.ok(ApiResponse.success(body));
    }

    public static <T> ResponseEntity<ApiResponse<T>> handleOptionalResponse(Optional<T> optional) {
        return optional.map(ResponseHandler::handleResponse)
                .orElseGet(() ->
                        ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Resource not found"))
                );
    }
}

