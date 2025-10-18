package com.example.datnbe.base.service;


import com.example.datnbe.Enum.ProviderEnum;
import com.example.datnbe.base.custom.CustomUserDetails;
import com.example.datnbe.base.entity.User;
import com.example.datnbe.base.repository.UserRepository;
import com.example.datnbe.dto.request.LoginRequest;
import com.example.datnbe.dto.request.ResetPassRequest;
import com.example.datnbe.dto.request.VerifyOTPRequest;
import com.example.datnbe.dto.response.ApiResponse;
import com.example.datnbe.dto.response.AuthResponse;
import com.example.datnbe.exception.ErrorResetPassword;
import com.example.datnbe.exception.LoginInfoNotCorrect;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

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

    @NonFinal
    @Value("${otp.expire}")
    int otpExpire;

    @NonFinal
    @Value("${otp.lock-account}")
    int lockAccountExpire;

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtServiceImpl jwtServiceImpl;
    TokenService tokenService;
    MailService mailService;

    public ApiResponse<AuthResponse> login(LoginRequest authRequest) {
        boolean checkUsername = userRepository.existsByUsername(authRequest.getUsername());
        if (!checkUsername) {
            throw new LoginInfoNotCorrect("Login info is not correct");
        }

        User user = userRepository.findByUsernameAndProvider(authRequest.getUsername(), ProviderEnum.LOCAL).orElse(null);
        boolean checkActive = user.isEnabled();
        if (!checkActive) {
            throw new RuntimeException("Your account is inactive");
        }
        boolean checkPassword = passwordEncoder.matches(authRequest.getPassword(), user.getPassword());
        String accessToken = "";
        String refreshToken = "";
        if (checkPassword) {
            accessToken = jwtServiceImpl.generateToken(user, true, accessExpire);
            refreshToken = jwtServiceImpl.generateToken(user, false, refreshExpire);

            tokenService.saveTokenInRedis("access", user.getUsername(), accessToken, accessExpiration);
            tokenService.saveTokenInRedis("refresh", user.getUsername(), refreshToken, refreshExpiration);
            log.info("User's Token in Redis: {}", tokenService.getTokenFromRedis("access", user.getUsername()));

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .role(user.getRole().toString())
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
            tokenService.deleteTokenFromRedis("access", username);
            tokenService.deleteTokenFromRedis("refresh", username);
        }
        return ApiResponse.builder()
                .code(200)
                .message("Log-out successfully")
                .build();
    }

    public ApiResponse getNewToken(String token) {
        String accessToken = "";

        String username = jwtServiceImpl.extractUsername(token);
        User user = userRepository.findByUsername(username);

        UserDetails userDetails = new CustomUserDetails(user);

        boolean checkToken = jwtServiceImpl.validateToken(token, userDetails);

        if (checkToken) {
            accessToken = jwtServiceImpl.generateToken(user, true, accessExpire);
            tokenService.saveTokenInRedis("access", user.getUsername(), accessToken, accessExpiration);
        }
        return ApiResponse.builder()
                .code(200)
                .message("Get new access token successfully")
                .result(accessToken)
                .build();
    }

    public ApiResponse resetPassword(ResetPassRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        boolean checkCurrentPassword = passwordEncoder.matches(request.getCurrentPassword(), user.getPassword());
        boolean checkNewPassword = request.getNewPassword().equals(request.getConfirmNewPassword());
        if (checkCurrentPassword && checkNewPassword) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            return ApiResponse.builder()
                    .code(200)
                    .message("Reset password successfully")
                    .build();
        } else {
            throw new ErrorResetPassword("Error reset password");
        }
    }

    public ApiResponse verifyOTP(VerifyOTPRequest request) {
        String username = request.getUsername();
        String otp = request.getOtp();

        if(tokenService.isOTPLocked(username)) {
            if (tokenService.isOTPLocked(username)) {
                return ApiResponse.builder()
                        .code(429)
                        .message("You have entered wrong OTP too many times. Please try again after 10 minutes.")
                        .build();
            }
        }

        boolean checkVerify = verifyOTP(request.getUsername(), request.getOtp());
        log.info(String.valueOf(verifyOTP(request.getUsername(), request.getOtp())));
        if (checkVerify) {
            User user = userRepository.findByUsername(request.getUsername());
            user.setEnabled(true);
            userRepository.save(user);
            log.info(user.toString());

            tokenService.deleteOTPFromRedis(user.getUsername());
            tokenService.resetOTPAttempt(user.getUsername());
            tokenService.unlockOTP(user.getUsername());

            return ApiResponse.builder()
                    .code(200)
                    .message("Verify OTP successfully")
                    .result(checkVerify)
                    .build();
        }else {
            tokenService.increaseOTPAttempt(username, otpExpire);
            int attempt = tokenService.getOTPAttemptFromRedis(username);

            if(attempt >= 5){
                tokenService.lockOTP(username , lockAccountExpire);
                return ApiResponse.builder()
                        .code(429)
                        .message("You have entered wrong OTP 5 times. Please try again after 10 minutes.")
                        .build();
            }
        }

        return ApiResponse.builder()
                .code(400)
                .message("Invalid OTP")
                .build();
    }

    public boolean verifyOTP(String username, String otp) {
        String OPTInRedis = tokenService.getOTPFromRedis(username);
        if (OPTInRedis == null) {
            return false;
        }
        if (!OPTInRedis.equals(otp)) {
            return false;
        }
        return true;
    }

    public void getNewPassword(String username) {
        User user = userRepository.findByUsername(username);
        String newPassword = generateRandomPassword(8);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        mailService.sendResetPasswordMail(user.getEmail(), newPassword);
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$!";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100 + random.nextInt(9000);
        return String.valueOf(otp);
    }

    public ApiResponse getNewOTP(String username) {
        String newOTP = generateOTP();
        User user = userRepository.findByUsername(username);
        mailService.sendOTP(user.getEmail(), newOTP);
        tokenService.saveOTPInRedis(username, newOTP, otpExpire);
        return ApiResponse.builder()
                .code(200)
                .message("Get new OTP successfully")
                .build();
    }

}
