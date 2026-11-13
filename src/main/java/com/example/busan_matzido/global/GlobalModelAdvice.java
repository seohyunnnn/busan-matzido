package com.example.busan_matzido.global;

import com.example.busan_matzido.member.Member;
import com.example.busan_matzido.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

/** 모든 화면에서 로그인한 회원을 'loginMember' 로 꺼내 쓸 수 있게 해줍니다. */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final MemberRepository memberRepository;

    @ModelAttribute("loginMember")
    public Member loginMember(Principal principal) {
        if (principal == null) {
            return null;
        }
        return memberRepository.findByUsername(principal.getName()).orElse(null);
    }
}
