package com.example.busan_matzido;

import com.example.busan_matzido.member.LoginAttemptService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceTests {

    @Test
    void 다섯번_실패하면_잠기고_성공하면_초기화된다() {
        LoginAttemptService service = new LoginAttemptService();

        for (int i = 0; i < 4; i++) service.loginFailed("user");
        assertFalse(service.isLocked("user"));      // 4회까지는 안 잠김

        service.loginFailed("user");
        assertTrue(service.isLocked("user"));       // 5회째 잠김

        service.loginSucceeded("other");
        assertFalse(service.isLocked("other"));     // 다른 아이디는 영향 없음
    }

    @Test
    void 성공하면_실패_횟수가_초기화된다() {
        LoginAttemptService service = new LoginAttemptService();
        for (int i = 0; i < 4; i++) service.loginFailed("user");
        service.loginSucceeded("user");
        for (int i = 0; i < 4; i++) service.loginFailed("user");
        assertFalse(service.isLocked("user"));
    }
}