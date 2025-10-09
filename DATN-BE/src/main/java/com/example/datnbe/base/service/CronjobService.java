package com.example.datnbe.base.service;


import com.example.datnbe.base.entity.User;
import com.example.datnbe.base.entity.VerificationToken;
import com.example.datnbe.base.repository.UserRepository;
import com.example.datnbe.base.repository.ValidateTokenRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CronjobService {
    ValidateTokenRepo validateTokenRepo;
    UserRepository userRepo;

    @Scheduled(cron = "0 0 0  * * ?")
//    @Scheduled(fixedRate = 15000)
    public void clearExpiredTokens() {
        List<VerificationToken> lst = validateTokenRepo.getAllTokenExpiry(LocalDateTime.now().minusMinutes(1));
        List<User> lstUser = new ArrayList<>();
        for (VerificationToken v : lst) {
            User user = userRepo.findById(v.getUser().getId()).orElse(null);
            if (user != null) {
                lstUser.add(user);
            }
        }
        validateTokenRepo.deleteAll(lst);
        userRepo.deleteAll(lstUser);

        log.info(validateTokenRepo.getAllTokenExpiry(LocalDateTime.now().minusMinutes(1)).toString());
    }
}

