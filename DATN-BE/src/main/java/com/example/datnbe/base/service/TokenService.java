package com.example.datnbe.base.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class TokenService {
    String REDIS_KEY_PREFIX = "jwt-token-";
    String OTP_KEY_PREFIX = "OTP-";
    String OTP_ATTEMPT_PREFIX = "OTP-ATTEMPT-";
    String OTP_LOCK_PREFIX = "OTP-LOCK-";

    RedisTemplate<String, String> redisTemplate;
    ValueOperations<String, String> valueOps;

    public void saveTokenInRedis(String typeToken, String username, String token, int expire) {
        String key = REDIS_KEY_PREFIX + username + "-" + typeToken;
        valueOps.set(key, token, expire, TimeUnit.SECONDS);
        log.info("Saving token to Redis: key={}, token={}", key, token);
    }

    public String getTokenFromRedis(String typeToken, String username) {
        String key = REDIS_KEY_PREFIX + username + "-" + typeToken;
        return valueOps.get(key);
    }

    public void deleteTokenFromRedis(String typeToken, String username) {
        String key = REDIS_KEY_PREFIX + username + "-" + typeToken;
        redisTemplate.delete(key);
    }

    /*
     *  OTP in Redis
     * */

    public void saveOTPInRedis(String username, String otp, int expire) {
        String key = OTP_KEY_PREFIX + username;
        valueOps.set(key, otp, expire, TimeUnit.SECONDS);
        log.info("Saving token to Redis: user={}, otp={}", key, otp);
    }

    public String getOTPFromRedis(String username) {
        String key = OTP_KEY_PREFIX + username;
        return valueOps.get(key);
    }

    public void deleteOTPFromRedis(String username) {
        String key = OTP_KEY_PREFIX + username;
        redisTemplate.delete(key);
    }

    public void increaseOTPAttempt(String username, int expire) {
        String key = OTP_ATTEMPT_PREFIX + username;
        String value = valueOps.get(key);
        int attempt = value == null ? 0 : Integer.parseInt(value);
        attempt++;
        valueOps.set(key, String.valueOf(attempt), expire, TimeUnit.SECONDS);
    }

    public int getOTPAttemptFromRedis(String username) {
        String key = OTP_ATTEMPT_PREFIX + username;
        String value = valueOps.get(key);
        return (value == null) ? 0 : Integer.parseInt(value);
    }

    public void resetOTPAttempt(String username) {
        redisTemplate.delete(OTP_ATTEMPT_PREFIX + username);
    }

    public void lockOTP(String username, int lockExpire) {
        String key = OTP_LOCK_PREFIX + username;
        valueOps.set(key, "LOCKED", lockExpire, TimeUnit.SECONDS);
    }

    public boolean isOTPLocked(String username) {
        String key = OTP_LOCK_PREFIX + username;
        return valueOps.get(key) != null;
    }

    public void unlockOTP(String username) {
        redisTemplate.delete(OTP_LOCK_PREFIX + username);
    }
}

