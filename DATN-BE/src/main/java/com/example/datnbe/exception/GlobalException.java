package com.example.datnbe.exception;

import com.example.datnbe.dto.response.ApiResponse;
import com.example.datnbe.dto.response.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ApiResponse> handle(RuntimeException e) {
        ApiResponse apiResponse = ApiResponse.builder()
                .code(400)
                .message(e.getMessage())
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }
}
