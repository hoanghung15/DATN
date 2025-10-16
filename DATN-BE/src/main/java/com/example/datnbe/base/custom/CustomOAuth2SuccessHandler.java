package com.example.datnbe.base.custom;

import com.example.datnbe.Enum.ProviderEnum;
import com.example.datnbe.Enum.Role;
import com.example.datnbe.base.entity.User;
import com.example.datnbe.base.repository.UserRepository;
import com.example.datnbe.base.service.JwtServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    UserRepository userRepository;
    JwtServiceImpl jwtService;
    @NonFinal
    @Value("${jwt.access.expire}")
    int accessExpire;
    @NonFinal
    @Value("${jwt.refresh.expire}")
    int refreshExpire;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        log.info(oAuth2User.toString());
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        ProviderEnum providerEnum = ProviderEnum.valueOf(token.getAuthorizedClientRegistrationId().toUpperCase());

        if (providerEnum == ProviderEnum.FACEBOOK) {
            String facebookId = oAuth2User.getAttribute("id");
             picture = "https://graph.facebook.com/" + facebookId + "/picture?type=large";
        }

        User user = userRepository.findByEmailAndProvider(email, providerEnum)
                .orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(name);
            user.setImage(picture);
            user.setRole(Role.USER);
            user.setProvider(providerEnum);
            user.setEnabled(true);
            userRepository.save(user);
        }

        String accessToken = jwtService.generateToken(user, true, accessExpire);
        String refreshToken = jwtService.generateToken(user, false, refreshExpire);

        String redirectUrl = UriComponentsBuilder
                .fromUriString("http://localhost:5500/index.html")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
        log.info("Redirect to {}", redirectUrl);
    }

}
