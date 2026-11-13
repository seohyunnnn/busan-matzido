package com.example.busan_matzido.restaurant;

import com.example.busan_matzido.member.Member;
import com.example.busan_matzido.member.MemberService;
import com.example.busan_matzido.review.Review;
import com.example.busan_matzido.review.ReviewForm;
import com.example.busan_matzido.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final ReviewService reviewService;
    private final MemberService memberService;

    // ---------- 목록 / 홈 / 지도 ----------

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("latest", restaurantService.latest());
        model.addAttribute("topRated", restaurantService.topRated());
        return "index";
    }

    @GetMapping("/restaurants")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Category category,
                       @RequestParam(required = false) District district,
                       @RequestParam(defaultValue = "latest") String sort,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        Page<Restaurant> result = restaurantService.search(keyword, category, district, sort, page);
        model.addAttribute("page", result);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("district", district);
        model.addAttribute("sort", sort);
        return "restaurants/list";
    }

    @GetMapping("/map")
    public String map(Model model) {
        model.addAttribute("restaurants", restaurantService.findMappable());
        return "restaurants/map";
    }

    // ---------- 상세 ----------

    @GetMapping("/restaurants/{id}")
    public String detail(@PathVariable Long id, Principal principal, Model model) {
        Member viewer = principal == null ? null : memberService.getByUsername(principal.getName());
        Restaurant restaurant = restaurantService.getForView(id, viewer);
        List<Review> reviews = reviewService.findByRestaurant(id);

        Review myReview = null;
        if (viewer != null) {
            myReview = reviews.stream()
                    .filter(r -> r.getMember().getId().equals(viewer.getId()))
                    .findFirst().orElse(null);
        }
        ReviewForm reviewForm = new ReviewForm();
        if (myReview != null) {
            reviewForm.setRating(myReview.getRating());
            reviewForm.setContent(myReview.getContent());
        }

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("reviews", reviews);
        model.addAttribute("myReview", myReview);
        model.addAttribute("reviewForm", reviewForm);
        model.addAttribute("canManage", restaurantService.canManage(restaurant, viewer));
        return "restaurants/detail";
    }

    // ---------- 등록 ----------

    @GetMapping("/restaurants/new")
    public String createForm(Model model) {
        model.addAttribute("form", new RestaurantForm());
        model.addAttribute("formTitle", "맛집 등록");
        model.addAttribute("formAction", "/restaurants");
        return "restaurants/form";
    }

    @PostMapping("/restaurants")
    public String create(@Valid @ModelAttribute("form") RestaurantForm form, BindingResult bindingResult,
                         Principal principal, Model model, RedirectAttributes ra) {
        validateCoordinates(form, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "맛집 등록");
            model.addAttribute("formAction", "/restaurants");
            return "restaurants/form";
        }
        Member member = memberService.getByUsername(principal.getName());
        Restaurant saved = restaurantService.create(form, member);
        ra.addFlashAttribute("flashMessage", member.isAdmin()
                ? "맛집이 등록되었습니다."
                : "맛집 등록을 신청했습니다. 관리자 승인 후 공개됩니다.");
        return "redirect:/restaurants/" + saved.getId();
    }

    // ---------- 수정 / 삭제 ----------

    @GetMapping("/restaurants/{id}/edit")
    public String editForm(@PathVariable Long id, Principal principal, Model model) {
        Member member = memberService.getByUsername(principal.getName());
        Restaurant restaurant = restaurantService.getForEdit(id, member);
        model.addAttribute("form", RestaurantForm.from(restaurant));
        model.addAttribute("formTitle", "맛집 수정");
        model.addAttribute("formAction", "/restaurants/" + id + "/edit");
        model.addAttribute("editingByMember", !member.isAdmin());
        return "restaurants/form";
    }

    @PostMapping("/restaurants/{id}/edit")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("form") RestaurantForm form, BindingResult bindingResult,
                       Principal principal, Model model, RedirectAttributes ra) {
        validateCoordinates(form, bindingResult);
        Member member = memberService.getByUsername(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "맛집 수정");
            model.addAttribute("formAction", "/restaurants/" + id + "/edit");
            model.addAttribute("editingByMember", !member.isAdmin());
            return "restaurants/form";
        }
        restaurantService.update(id, form, member);
        ra.addFlashAttribute("flashMessage", member.isAdmin()
                ? "맛집 정보가 수정되었습니다."
                : "수정되었습니다. 관리자 재승인 후 다시 공개됩니다.");
        return "redirect:/restaurants/" + id;
    }

    @PostMapping("/restaurants/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        Member member = memberService.getByUsername(principal.getName());
        restaurantService.delete(id, member);
        ra.addFlashAttribute("flashMessage", "맛집이 삭제되었습니다.");
        return "redirect:/restaurants";
    }

    /** 위도/경도는 둘 다 입력하거나 둘 다 비워야 함 */
    private void validateCoordinates(RestaurantForm form, BindingResult bindingResult) {
        if ((form.getLatitude() == null) != (form.getLongitude() == null)) {
            bindingResult.rejectValue("latitude", "coordinates.pair", "위도와 경도는 함께 입력해 주세요.");
        }
    }
}
