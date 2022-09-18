package com.barnackles.ApplicationSecurity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LoginAttemptService {
    private final int MAX_ATTEMPT = 5;
    private LoadingCache<String, Integer> attemptsCache;

    public LoginAttemptService() {
        super();
        attemptsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS).build(new CacheLoader<>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    public void loginSucceeded(String key) {
        log.info("Key invalidated: {}", key);
        attemptsCache.invalidate(key);
    }

    public void loginFailed(String key) {
        int attempts;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        log.info("Login attempts: {}", attempts);
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(String key) {
        try {
            if (attemptsCache.get(key) >= MAX_ATTEMPT) {
                boolean exceededMaxAttempts = true;
                log.info("User exceeded max login attempts: {}", exceededMaxAttempts);
                return exceededMaxAttempts;
            }
        } catch (ExecutionException e) {
            log.info("User not exceeded max attempts.");
            return false;
        }
        return false;
    }

}
