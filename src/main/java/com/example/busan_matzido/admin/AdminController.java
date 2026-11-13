package com.example.busan_matzido.admin;

import com.example.busan_matzido.global.BusinessException;
import com.example.busan_matzido.member.Member;
import com.example.busan_matzido.member.MemberService;
import com.example.busan_matzido.restaurant.RestaurantService;
import com.example.busan_matzido.restaurant.RestaurantStatus;
import com.example.busan_matzido.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/** /admin/** 은 SecurityConfig 에서 ROLE_ADMIN 만 접근 가능하도록 막혀 있습니다. */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;
    private final RestaurantService restaurantService;
    private final ReviewService reviewService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("memberCount", memberService.count());
        model.addAttribute("approvedCount", restaurantService.countByStatus(RestaurantStatus.APPROVED));
        model.addAttribute("pendingCount", restaurantService.countByStatus(RestaurantStatus.PENDING));
        model.addAttribute("reviewCount", reviewService.count());
        return "admin/dashboard";
    }

    // ---------- 맛집 승인 ----------

    @GetMapping("/restaurants")
    public String restaurants(@RequestParam(defaultValue = "PENDING") RestaurantStatus status,
                              @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("page", restaurantService.findByStatus(status, page));
        model.addAttribute("status", status);
        model.addAttribute("statuses", RestaurantStatus.values());
        return "admin/restaurants";
    }

    @PostMapping("/restaurants/{id}/approve")
    public String approve(@PathVariable Long id, @RequestParam(defaultValue = "PENDING") RestaurantStatus status,
                          RedirectAttributes ra) {
        restaurantService.approve(id);
        ra.addFlashAttribute("flashMessage", "맛집을 승인했습니다.");
        return "redirect:/admin/restaurants?status=" + status;
    }

    @PostMapping("/restaurants/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam(defaultValue = "PENDING") RestaurantStatus status,
                         RedirectAttributes ra) {
        restaurantService.reject(id);
        ra.addFlashAttribute("flashMessage", "맛집을 반려했습니다.");
        return "redirect:/admin/restaurants?status=" + status;
    }

    @PostMapping("/restaurants/{id}/delete")
    public String deleteRestaurant(@PathVariable Long id, @RequestParam(defaultValue = "PENDING") RestaurantStatus status,
                                   Principal principal, RedirectAttributes ra) {
        Member admin = memberService.getByUsername(principal.getName());
        restaurantService.delete(id, admin);
        ra.addFlashAttribute("flashMessage", "맛집을 삭제했습니다.");
        return "redirect:/admin/restaurants?status=" + status;
    }

    // ---------- 회원 관리 ----------

    @GetMapping("/members")
    public String members(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("page", memberService.list(page));
        return "admin/members";
    }

    @PostMapping("/members/{id}/toggle-enabled")
    public String toggleEnabled(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        try {
            memberService.toggleEnabled(id, principal.getName());
            ra.addFlashAttribute("flashMessage", "회원 상태를 변경했습니다.");
        } catch (BusinessException e) {
            ra.addFlashAttribute("flashError", e.getMessage());
        }
        return "redirect:/admin/members";
    }

    @PostMapping("/members/{id}/toggle-role")
    public String toggleRole(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        try {
            memberService.toggleRole(id, principal.getName());
            ra.addFlashAttribute("flashMessage", "회원 권한을 변경했습니다.");
        } catch (BusinessException e) {
            ra.addFlashAttribute("flashError", e.getMessage());
        }
        return "redirect:/admin/members";
    }

    // ---------- 리뷰 관리 ----------

    @GetMapping("/reviews")
    public String reviews(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("page", reviewService.list(page));
        return "admin/reviews";
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        Member admin = memberService.getByUsername(principal.getName());
        reviewService.delete(id, admin);
        ra.addFlashAttribute("flashMessage", "리뷰를 삭제했습니다.");
        return "redirect:/admin/reviews";
    }
}
