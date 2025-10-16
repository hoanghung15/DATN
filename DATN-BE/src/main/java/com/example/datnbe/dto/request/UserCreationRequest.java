package com.example.datnbe.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreationRequest {
    @NotNull
    String username;

    @NotNull
    String email;

    @NotNull
    String password;

    @NotNull
    String confirmPassword;

    @NotNull
    String firstName;

    @NotNull
    String lastName;

    @NotNull
    String image;
}
