package com.example.datnbe.base.controller;

import com.example.datnbe.base.entity.User;
import com.example.datnbe.base.entity.VerificationToken;
import com.example.datnbe.base.repository.UserRepository;
import com.example.datnbe.base.repository.ValidateTokenRepo;
import com.example.datnbe.base.service.AuthService;
import com.example.datnbe.dto.request.LoginRequest;
import com.example.datnbe.dto.request.ResetPassRequest;
import com.example.datnbe.dto.response.ApiResponse;
import com.example.datnbe.dto.response.AuthResponse;
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
    ValidateTokenRepo validateTokenRepo;
    UserRepository userRepository;

    @PostMapping("login")
    ApiResponse<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("logout")
    public ApiResponse<AuthResponse> logout(HttpServletRequest request) {
        return authService.logout(request);
    }

    @GetMapping("/new-accessToken")
    public ApiResponse<AuthResponse> newAccessToken(@RequestParam String token) {
        return authService.getNewToken(token);
    }

    @PostMapping("/get-new-password")
    public ApiResponse resetPassword(@RequestParam String username) {
        authService.getNewPassword(username);
        return ApiResponse.builder()
                .code(200)
                .message("Get new  password successfully")
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse resetPassword(@RequestBody ResetPassRequest request) {
        return authService.resetPassword(request);
    }

    @GetMapping("/verify")
    public ApiResponse verifyTokenFromMail(@ParameterObject String token) {
        VerificationToken verificationToken = validateTokenRepo.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ApiResponse.builder()
                    .code(400)
                    .message("Invalid Token")
                    .build();
        }
        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        validateTokenRepo.delete(verificationToken);

        return ApiResponse.builder()
                .code(200)
                .message("Verify Token Success")
                .build();
    }
}
