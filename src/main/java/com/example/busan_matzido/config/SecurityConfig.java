package com.example.busan_matzido.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 정적 리소스, 에러 페이지
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/error").permitAll()
                // 로그인 / 회원가입
                .requestMatchers("/login", "/signup").permitAll()
                // 누구나 볼 수 있는 화면 (숫자 id 만 허용 → /restaurants/new 는 로그인 필요)
                .requestMatchers(HttpMethod.GET, "/", "/restaurants", "/restaurants/{id:\\d+}", "/map").permitAll()
                // 관리자 전용
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 나머지(맛집 등록·수정, 리뷰 작성, 마이페이지 등)는 로그인 필요
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", false)
                .permitAll())
            .logout(logout -> logout
                .logoutSuccessUrl("/"));
        return http.build();
    }
}
