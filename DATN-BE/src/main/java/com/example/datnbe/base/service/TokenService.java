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

    RedisTemplate<String, String> redisTemplate;
    ValueOperations<String, String> valueOps;

    public void saveTokenInRedis(String typeToken ,String username, String token, int expire) {
        String key = REDIS_KEY_PREFIX + username + "-" + typeToken;
        valueOps.set(key, token, expire, TimeUnit.SECONDS);
        log.info("Saving token to Redis: key={}, token={}", key, token);
    }

    public String getTokenFromRedis(String typeToken , String username) {
        String key = REDIS_KEY_PREFIX + username + "-" + typeToken;
        return valueOps.get(key);
    }

    public void deleteTokenFromRedis(String typeToken , String username) {
        String key = REDIS_KEY_PREFIX + username + "-" + typeToken;
        redisTemplate.delete(key);
    }
}

