package com.example.datnbe.base.controller;

import com.example.datnbe.base.entity.User;
import com.example.datnbe.base.entity.VerificationToken;
import com.example.datnbe.base.repository.UserRepository;
import com.example.datnbe.base.repository.ValidateTokenRepo;
import com.example.datnbe.base.service.AuthService;
import com.example.datnbe.dto.request.LoginRequest;
import com.example.datnbe.dto.request.ResetPassRequest;
import com.example.datnbe.dto.request.VerifyOTPRequest;
import com.example.datnbe.dto.response.ApiResponse;
import com.example.datnbe.dto.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Tag(name = "auth-controller")
@RequestMapping("auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;

    @Operation(summary = "Login by Username and Password", description = "Login by Username and Password")
    @PostMapping("/login")
    ApiResponse<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @Operation(summary = "Logout", description = "Logout")
    @PostMapping("logout")
    public ApiResponse<AuthResponse> logout(HttpServletRequest request) {
        return authService.logout(request);
    }

    @GetMapping("/new-accessToken")
    @Operation(summary = "Get new Access Token", description = "Get new Access Token")
    public ApiResponse<AuthResponse> newAccessToken(@RequestParam String token) {
        return authService.getNewToken(token);
    }

    @Operation(description = "Get new password", summary = "Get new Password")
    @PostMapping("/get-new-password")
    public ApiResponse resetPassword(@RequestParam String username) {
        authService.getNewPassword(username);
        return ApiResponse.builder()
                .code(200)
                .message("Get new  password successfully")
                .build();
    }

    @Operation(summary = "Reset your password", description = "Reset your password")
    @PostMapping("/reset-password")
    public ApiResponse resetPassword(@RequestBody ResetPassRequest request) {
        return authService.resetPassword(request);
    }

    @Operation(summary = "Verify your OTP", description = "Verify your OTP")
    @PostMapping("/verify-otp")
    public ApiResponse verifyOTP(@RequestBody VerifyOTPRequest request) {
        return authService.verifyOTP(request);
    }

    @Operation(summary = "Get new OTP", description = "Get new OTP")
    @GetMapping("/get-new-otp")
    public ApiResponse getNewOTP(@RequestParam String username) {
        return authService.getNewOTP(username);
    }
}
