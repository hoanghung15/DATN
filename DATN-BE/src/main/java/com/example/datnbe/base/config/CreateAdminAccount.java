package com.example.datnbe.base.config;

import com.example.datnbe.Enum.ProviderEnum;
import com.example.datnbe.Enum.Role;
import com.example.datnbe.base.entity.User;
import com.example.datnbe.base.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateAdminAccount {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    @PostConstruct
    public void createAdminAccount() {
        if (userRepository.findByUsername("admin") == null) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("12345"));
            user.setEmail("admin@example.com");
            user.setEnabled(true);
            user.setProvider(ProviderEnum.LOCAL);
            user.setRole(Role.ADMIN);
            userRepository.save(user);
            log.info("Created admin account" + user.getUsername());
        }
    }
}
