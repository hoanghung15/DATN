package com.example.datnbe.base.controller;

import com.example.datnbe.base.service.UserService;
import com.example.datnbe.dto.request.UserCreationRequest;
import com.example.datnbe.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("user")
public class UserController {
    UserService userService;

    @Operation(summary = "Create new User", description = "Create new User")
    @PostMapping
    public ApiResponse createUser(@RequestBody UserCreationRequest userCreationRequest) {
        return userService.createUser(userCreationRequest);
    }

}
