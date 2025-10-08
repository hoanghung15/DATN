package com.example.datnbe.test;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisTestRunner implements CommandLineRunner {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void run(String... args) {
        redisTemplate.opsForValue().set("testKey", "hello");
        String value = redisTemplate.opsForValue().get("testKey");
        System.out.println("🔸 Redis test value: " + value);
    }
}
