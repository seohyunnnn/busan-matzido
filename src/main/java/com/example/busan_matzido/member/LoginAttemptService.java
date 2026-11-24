package com.example.busan_matzido.member;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/** 로그인 실패 횟수를 세어 5회 연속 실패하면 10분간 로그인을 막는다. (서버 메모리에 저장) */
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_TIME = Duration.ofMinutes(10);

    private record Attempt(int count, Instant lockedUntil) {}

    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();

    public void loginFailed(String username) {
        attempts.merge(username, new Attempt(1, null), (old, ignore) -> {
            // 잠금 시간이 지났으면 처음부터 다시 센다
            if (old.lockedUntil() != null && Instant.now().isAfter(old.lockedUntil())) {
                return new Attempt(1, null);
            }
            int next = old.count() + 1;
            return next >= MAX_ATTEMPTS
                    ? new Attempt(next, Instant.now().plus(LOCK_TIME))
                    : new Attempt(next, null);
        });
    }

    public void loginSucceeded(String username) {
        attempts.remove(username);
    }

    public boolean isLocked(String username) {
        Attempt attempt = attempts.get(username);
        if (attempt == null || attempt.lockedUntil() == null) {
            return false;
        }
        if (Instant.now().isAfter(attempt.lockedUntil())) {
            attempts.remove(username);
            return false;
        }
        return true;
    }
}
