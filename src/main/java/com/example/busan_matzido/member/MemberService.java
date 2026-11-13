package com.example.busan_matzido.member;

import com.example.busan_matzido.global.BusinessException;
import com.example.busan_matzido.global.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member signUp(SignUpForm form) {
        Member member = Member.builder()
                .username(form.getUsername().trim())
                .password(passwordEncoder.encode(form.getPassword()))
                .nickname(form.getNickname().trim())
                .email(form.getEmail().trim().toLowerCase())
                .role(Role.USER)
                .build();
        return memberRepository.save(member);
    }

    /** 관리자 계정이 없을 때만 생성 (앱 시작 시 DataInitializer 에서 호출) */
    @Transactional
    public Member createAdminIfAbsent(String username, String rawPassword, String nickname, String email) {
        return memberRepository.findByUsername(username).orElseGet(() ->
                memberRepository.save(Member.builder()
                        .username(username)
                        .password(passwordEncoder.encode(rawPassword))
                        .nickname(nickname)
                        .email(email)
                        .role(Role.ADMIN)
                        .build()));
    }

    public Member getByUsername(String username) {
        return memberRepository.findByUsername(username).orElseThrow(NotFoundException::new);
    }

    public boolean usernameExists(String username) {
        return memberRepository.existsByUsername(username.trim());
    }

    public boolean nicknameExists(String nickname) {
        return memberRepository.existsByNickname(nickname.trim());
    }

    public boolean emailExists(String email) {
        return memberRepository.existsByEmail(email.trim().toLowerCase());
    }

    public boolean matchesPassword(Member member, String rawPassword) {
        return rawPassword != null && passwordEncoder.matches(rawPassword, member.getPassword());
    }

    @Transactional
    public void updateProfile(String username, ProfileForm form) {
        Member member = getByUsername(username);
        member.changeNickname(form.getNickname().trim());
        if (form.getNewPassword() != null && !form.getNewPassword().isBlank()) {
            member.changePassword(passwordEncoder.encode(form.getNewPassword()));
        }
    }

    // ---------- 관리자 기능 ----------

    public Page<Member> list(int page) {
        return memberRepository.findAll(PageRequest.of(Math.max(page, 0), 15, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public long count() {
        return memberRepository.count();
    }

    @Transactional
    public void toggleEnabled(Long targetId, String actingUsername) {
        Member target = memberRepository.findById(targetId).orElseThrow(NotFoundException::new);
        if (target.getUsername().equals(actingUsername)) {
            throw new BusinessException("본인 계정은 정지할 수 없습니다.");
        }
        target.toggleEnabled();
    }

    @Transactional
    public void toggleRole(Long targetId, String actingUsername) {
        Member target = memberRepository.findById(targetId).orElseThrow(NotFoundException::new);
        if (target.getUsername().equals(actingUsername)) {
            throw new BusinessException("본인 권한은 변경할 수 없습니다.");
        }
        target.toggleRole();
    }
}
