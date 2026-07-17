package com.bloom.bloomschool.auth.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GenericResponse {

    private static final String SUCCESS = "Success";

    public <T> ResponseEntity<ApiResponse<T>> response(T body, HttpStatus status) {
        return new ResponseEntity<>(
                ApiResponse.<T>builder()
                        .message(SUCCESS)
                        .body(body)
                        .build(),
                status
        );
    }
}
