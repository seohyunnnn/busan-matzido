package com.example.busan_matzido.member;

import com.example.busan_matzido.restaurant.RestaurantService;
import com.example.busan_matzido.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ReviewService reviewService;
    private final RestaurantService restaurantService;

    // ---------- 로그인 (인증 처리는 Spring Security 가 담당) ----------

    @GetMapping("/login")
    public String loginForm(Principal principal) {
        return principal != null ? "redirect:/" : "login";
    }

    // ---------- 회원가입 ----------

    @GetMapping("/signup")
    public String signUpForm(Principal principal, Model model) {
        if (principal != null) {
            return "redirect:/";
        }
        model.addAttribute("form", new SignUpForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(@Valid @ModelAttribute("form") SignUpForm form, BindingResult bindingResult,
                         RedirectAttributes ra) {
        if (form.getPassword() != null && !form.getPassword().equals(form.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "mismatch", "비밀번호가 일치하지 않습니다.");
        }
        if (!bindingResult.hasFieldErrors("username") && memberService.usernameExists(form.getUsername())) {
            bindingResult.rejectValue("username", "duplicate", "이미 사용 중인 아이디입니다.");
        }
        if (!bindingResult.hasFieldErrors("nickname") && memberService.nicknameExists(form.getNickname())) {
            bindingResult.rejectValue("nickname", "duplicate", "이미 사용 중인 닉네임입니다.");
        }
        if (!bindingResult.hasFieldErrors("email") && memberService.emailExists(form.getEmail())) {
            bindingResult.rejectValue("email", "duplicate", "이미 가입된 이메일입니다.");
        }
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        memberService.signUp(form);
        ra.addFlashAttribute("flashMessage", "회원가입이 완료되었습니다. 로그인해 주세요.");
        return "redirect:/login";
    }

    // ---------- 마이페이지 ----------

    @GetMapping("/mypage")
    public String myPage(Principal principal, Model model) {
        Member member = memberService.getByUsername(principal.getName());
        model.addAttribute("reviews", reviewService.findByMember(member.getId()));
        model.addAttribute("myRestaurants", restaurantService.findByAuthor(member.getId()));
        return "members/mypage";
    }

    @GetMapping("/mypage/edit")
    public String editForm(Principal principal, Model model) {
        Member member = memberService.getByUsername(principal.getName());
        ProfileForm form = new ProfileForm();
        form.setNickname(member.getNickname());
        model.addAttribute("form", form);
        return "members/edit";
    }

    @PostMapping("/mypage/edit")
    public String edit(@Valid @ModelAttribute("form") ProfileForm form, BindingResult bindingResult,
                       Principal principal, RedirectAttributes ra) {
        Member member = memberService.getByUsername(principal.getName());

        if (!bindingResult.hasFieldErrors("nickname")
                && !member.getNickname().equals(form.getNickname().trim())
                && memberService.nicknameExists(form.getNickname())) {
            bindingResult.rejectValue("nickname", "duplicate", "이미 사용 중인 닉네임입니다.");
        }
        if (StringUtils.hasText(form.getNewPassword())) {
            if (!memberService.matchesPassword(member, form.getCurrentPassword())) {
                bindingResult.rejectValue("currentPassword", "wrong", "현재 비밀번호가 올바르지 않습니다.");
            }
            if (!form.getNewPassword().equals(form.getNewPasswordConfirm())) {
                bindingResult.rejectValue("newPasswordConfirm", "mismatch", "새 비밀번호가 일치하지 않습니다.");
            }
        }
        if (bindingResult.hasErrors()) {
            return "members/edit";
        }
        memberService.updateProfile(principal.getName(), form);
        ra.addFlashAttribute("flashMessage", "회원 정보가 수정되었습니다.");
        return "redirect:/mypage";
    }
}
