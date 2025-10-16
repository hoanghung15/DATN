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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    final static String PREFIX_URL_MAIL = "http://localhost:8080/auth/verify?token=";

    CommonMapper commonMapper;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    ValidateTokenRepo validateTokenRepo;
    private final MailService mailService;
@Transactional
    public ApiResponse createUser(UserCreationRequest request) {
        boolean checkEmail = userRepository.existsByEmail((request.getEmail()));
        boolean checkUsername = userRepository.existsByUsername(((request.getUsername())));
        boolean checkPassword = request.getPassword().equals(request.getConfirmPassword());

        if (checkEmail || checkUsername) {
            throw new UserExistedException("User existed");
        }

        if(!checkPassword) {
            throw  new ErrorCreateUser("Thông tin không chính xác");
        }

        User user = commonMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(false);
        user.setProvider(ProviderEnum.LOCAL);
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(1));
        validateTokenRepo.save(verificationToken);

        String verifyLink = PREFIX_URL_MAIL + token;
        mailService.sendVerificationEmail(request.getEmail(), verifyLink);

        return ApiResponse.builder()
                .code(201)
                .message("User created successfully. Please check your email to verify your account.")
                .result(user)
                .build();
    }
}
