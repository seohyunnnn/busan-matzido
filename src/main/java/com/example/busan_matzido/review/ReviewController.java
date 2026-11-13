package com.example.busan_matzido.review;

import com.example.busan_matzido.global.BusinessException;
import com.example.busan_matzido.member.Member;
import com.example.busan_matzido.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final MemberService memberService;

    @PostMapping("/restaurants/{restaurantId}/reviews")
    public String write(@PathVariable Long restaurantId,
                        @Valid ReviewForm form, BindingResult bindingResult,
                        Principal principal, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("flashError", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/restaurants/" + restaurantId + "#reviews";
        }
        Member member = memberService.getByUsername(principal.getName());
        try {
            reviewService.write(restaurantId, member, form);
            ra.addFlashAttribute("flashMessage", "리뷰가 등록되었습니다.");
        } catch (BusinessException e) {
            ra.addFlashAttribute("flashError", e.getMessage());
        }
        return "redirect:/restaurants/" + restaurantId + "#reviews";
    }

    @PostMapping("/reviews/{reviewId}/edit")
    public String edit(@PathVariable Long reviewId,
                       @Valid ReviewForm form, BindingResult bindingResult,
                       Principal principal, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("flashError", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/restaurants/" + reviewService.findRestaurantId(reviewId) + "#reviews";
        }
        Member member = memberService.getByUsername(principal.getName());
        Long restaurantId = reviewService.update(reviewId, member, form);
        ra.addFlashAttribute("flashMessage", "리뷰가 수정되었습니다.");
        return "redirect:/restaurants/" + restaurantId + "#reviews";
    }

    @PostMapping("/reviews/{reviewId}/delete")
    public String delete(@PathVariable Long reviewId, Principal principal, RedirectAttributes ra) {
        Member member = memberService.getByUsername(principal.getName());
        Long restaurantId = reviewService.delete(reviewId, member);
        ra.addFlashAttribute("flashMessage", "리뷰가 삭제되었습니다.");
        return "redirect:/restaurants/" + restaurantId + "#reviews";
    }
}
