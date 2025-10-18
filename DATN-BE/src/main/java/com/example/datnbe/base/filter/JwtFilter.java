package com.example.datnbe.base.filter;

import com.example.datnbe.base.custom.CustomUserDetails;
import com.example.datnbe.base.entity.User;
import com.example.datnbe.base.repository.UserRepository;
import com.example.datnbe.base.service.JwtService;
import com.example.datnbe.base.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtFilter extends OncePerRequestFilter {

    JwtService jwtService;
    UserRepository userRepository;
    TokenService tokenService;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/auth/login",
            "/auth/new-accessToken",
            "/auth/reset-password",
            "/auth/verify",
            "/auth/get-new-password",
            "/auth/verify-otp",
            "/auth/get-new-otp",
            "/swagger-ui.html",
            "/swagger-ui/",
            "/swagger-ui/index.html",
            "/v3/api-docs/",
            "/swagger-resources/",
            "/webjars/",
            "/face/",
            "/login/",
            "/login/oauth2/",
            "/oauth2/"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                String accessToken = header.substring(7);
                String username = jwtService.extractUsername(accessToken);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User user = userRepository.findByUsername(username);
                    UserDetails userDetails = new CustomUserDetails(user);

                    List<String> listTokenStored = new ArrayList<>();
                    String accessTokenFromRedis = tokenService.getTokenFromRedis("access", username);
                    String refreshTokenFromRedis = tokenService.getTokenFromRedis("refresh", username);

                    if (accessTokenFromRedis != null) listTokenStored.add(accessTokenFromRedis);
                    if (refreshTokenFromRedis != null) listTokenStored.add(refreshTokenFromRedis);

                    if (listTokenStored.contains(accessToken) && jwtService.validateToken(accessToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                log.warn("JWT filter error: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        for (String endpoint : PUBLIC_ENDPOINTS) {
            if (path.startsWith(endpoint)) {
                return true;
            }
        }
        return false;
    }
}
