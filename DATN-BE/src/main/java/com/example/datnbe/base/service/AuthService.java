package com.example.datnbe.base.service;


import com.example.datnbe.Enum.ProviderEnum;
import com.example.datnbe.base.entity.User;
import com.example.datnbe.base.repository.UserRepository;
import com.example.datnbe.dto.request.LoginRequest;
import com.example.datnbe.dto.response.ApiResponse;
import com.example.datnbe.dto.response.AuthResponse;
import com.example.datnbe.exception.LoginInfoNotCorrect;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    @Value("${jwt.access.expire}")
    @NonFinal
    int accessExpire;

    @Value("${jwt.refresh.expire}")
    @NonFinal
    int refreshExpire;

    @NonFinal
    @Value("${jwt.access.expire}")
    int accessExpiration;

    @NonFinal
    @Value("${jwt.refresh.expire}")
    int refreshExpiration;

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtServiceImpl jwtServiceImpl;
    TokenService tokenService;

    public ApiResponse<AuthResponse> login(LoginRequest authRequest) {
        boolean checkUsername = userRepository.existsByUsername(authRequest.getUsername());
        if (!checkUsername) {
            throw new LoginInfoNotCorrect("Login info is not correct");
        }

        User user = userRepository.findByUsernameAndProvider(authRequest.getUsername(), ProviderEnum.LOCAL).orElse(null);
        boolean checkPassword = passwordEncoder.matches(authRequest.getPassword(), user.getPassword());
        String accessToken = "";
        String refreshToken = "";
        if (checkPassword) {
            accessToken = jwtServiceImpl.generateToken(user, true, accessExpire);
            refreshToken = jwtServiceImpl.generateToken(user, false, refreshExpire);

            tokenService.saveTokenInRedis(user.getUsername(), accessToken, accessExpiration);
            tokenService.saveTokenInRedis(user.getUsername(), refreshToken, refreshExpiration);
            log.info("User's Token in Redis: {}", tokenService.getTokenFromRedis(user.getUsername()));

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            return ApiResponse.<AuthResponse>builder()
                    .code(200)
                    .message("Success")
                    .result(authResponse)
                    .build();
        } else {
            throw new LoginInfoNotCorrect("Login info is not correct");
        }

    }

    public ApiResponse logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String username = jwtServiceImpl.extractUsername(token);
            tokenService.deleteTokenFromRedis(username);
        }
        return ApiResponse.builder()
                .code(200)
                .message("Log-out successfully")
                .build();
    }
}
