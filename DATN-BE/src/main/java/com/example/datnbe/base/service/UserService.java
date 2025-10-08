package com.example.datnbe.base.service;

import com.example.datnbe.Enum.ProviderEnum;
import com.example.datnbe.Enum.Role;
import com.example.datnbe.base.entity.User;
import com.example.datnbe.base.repository.UserRepository;
import com.example.datnbe.dto.request.UserCreationRequest;
import com.example.datnbe.dto.response.ApiResponse;
import com.example.datnbe.exception.UserExistedException;
import com.example.datnbe.mapper.CommonMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    CommonMapper commonMapper;
    UserRepository userRepository;
     PasswordEncoder passwordEncoder;

    public ApiResponse createUser(UserCreationRequest request) {
        boolean checkEmail = userRepository.existsByEmail((request.getEmail()));
        boolean checkUsername = userRepository.existsByUsername(((request.getUsername())));

        if(checkEmail || checkUsername){
            throw new UserExistedException("User existed");
        }

        User user = commonMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setProvider(ProviderEnum.LOCAL);
        userRepository.save(user);

        return ApiResponse.builder()
                .code(201)
                .message("User created successfully")
                .result(user)
                .build();
    }
}
