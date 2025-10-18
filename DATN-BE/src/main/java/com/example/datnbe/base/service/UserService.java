package com.example.datnbe.base.service;

import com.example.datnbe.Enum.ProviderEnum;
import com.example.datnbe.Enum.Role;
import com.example.datnbe.base.entity.User;
import com.example.datnbe.base.entity.VerificationToken;
import com.example.datnbe.base.repository.UserRepository;
import com.example.datnbe.base.repository.ValidateTokenRepo;
import com.example.datnbe.dto.request.UserCreationRequest;
import com.example.datnbe.dto.response.ApiResponse;
import com.example.datnbe.exception.ErrorCreateUser;
import com.example.datnbe.exception.UserExistedException;
import com.example.datnbe.mapper.CommonMapper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    @NonFinal
    @Value("${otp.expire}")
    int otpExpire;

    CommonMapper commonMapper;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    MailService mailService;
    TokenService tokenService;
    AuthService authService;
    KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public ApiResponse createUser(UserCreationRequest request) {
        boolean checkEmail = userRepository.existsByEmail((request.getEmail()));
        boolean checkUsername = userRepository.existsByUsername(((request.getUsername())));
        boolean checkPassword = request.getPassword().equals(request.getConfirmPassword());

        if (checkEmail || checkUsername) {
            throw new UserExistedException("User existed");
        }

        if (!checkPassword) {
            throw new ErrorCreateUser("Login Info Not Correct");
        }

        User user = commonMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(false);
        user.setProvider(ProviderEnum.LOCAL);
        userRepository.save(user);

        String otp = authService.generateOTP();
        tokenService.saveOTPInRedis(user.getUsername(), otp, otpExpire);
        if (user != null) {
            String message = user.getEmail() + "-" + otp;
            kafkaTemplate.send("confirm-account-topic", message);
        }


        return ApiResponse.builder()
                .code(201)
                .message("User created successfully. Please check your email to verify your account.")
                .result(user)
                .build();
    }
}
