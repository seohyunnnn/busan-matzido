package com.example.busan_matzido;

import com.example.busan_matzido.global.BusinessException;
import com.example.busan_matzido.global.ForbiddenException;
import com.example.busan_matzido.member.Member;
import com.example.busan_matzido.member.MemberService;
import com.example.busan_matzido.member.SignUpForm;
import com.example.busan_matzido.restaurant.Category;
import com.example.busan_matzido.restaurant.District;
import com.example.busan_matzido.restaurant.Restaurant;
import com.example.busan_matzido.restaurant.RestaurantForm;
import com.example.busan_matzido.restaurant.RestaurantService;
import com.example.busan_matzido.restaurant.RestaurantStatus;
import com.example.busan_matzido.review.ReviewForm;
import com.example.busan_matzido.review.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/** 회원 → 맛집 등록(승인) → 리뷰 → 평점 집계 흐름 검증 */
@SpringBootTest
@Transactional
class ReviewFlowTests {

    @Autowired MemberService memberService;
    @Autowired RestaurantService restaurantService;
    @Autowired ReviewService reviewService;

    private Member newUser(String username, String nickname) {
        SignUpForm f = new SignUpForm();
        f.setUsername(username);
        f.setPassword("password123");
        f.setPasswordConfirm("password123");
        f.setNickname(nickname);
        f.setEmail(username + "@test.com");
        return memberService.signUp(f);
    }

    private Member newAdmin() {
        return memberService.createAdminIfAbsent("test_admin", "adminpass123", "testadmin", "ta@test.com");
    }

    private RestaurantForm restaurantForm(String name) {
        RestaurantForm f = new RestaurantForm();
        f.setName(name);
        f.setCategory(Category.PORK_SOUP);
        f.setDistrict(District.BUSANJIN);
        f.setAddress("부산광역시 부산진구 어딘가 1");
        return f;
    }

    private ReviewForm reviewForm(int rating, String content) {
        ReviewForm f = new ReviewForm();
        f.setRating(rating);
        f.setContent(content);
        return f;
    }

    @Test
    void userRestaurantIsPendingUntilAdminApproves() {
        Member user = newUser("user_a", "유저A");
        Member admin = newAdmin();

        Restaurant byUser = restaurantService.create(restaurantForm("유저 국밥"), user);
        Restaurant byAdmin = restaurantService.create(restaurantForm("관리자 국밥"), admin);

        assertEquals(RestaurantStatus.PENDING, byUser.getStatus());
        assertEquals(RestaurantStatus.APPROVED, byAdmin.getStatus());

        restaurantService.approve(byUser.getId());
        assertEquals(RestaurantStatus.APPROVED, byUser.getStatus());
    }

    @Test
    void ratingAverageFollowsReviewChanges() {
        Member admin = newAdmin();
        Member u1 = newUser("user_b", "유저B");
        Member u2 = newUser("user_c", "유저C");
        Restaurant r = restaurantService.create(restaurantForm("평점 테스트집"), admin);

        reviewService.write(r.getId(), u1, reviewForm(5, "정말 맛있어요!"));
        var second = reviewService.write(r.getId(), u2, reviewForm(3, "보통이에요."));
        assertEquals(2, r.getReviewCount());
        assertEquals(4.0, r.getAverageRating(), 0.001);

        reviewService.update(second.getId(), u2, reviewForm(4, "다시 가보니 좋았어요."));
        assertEquals(4.5, r.getAverageRating(), 0.001);

        reviewService.delete(second.getId(), admin);
        assertEquals(1, r.getReviewCount());
        assertEquals(5.0, r.getAverageRating(), 0.001);
    }

    @Test
    void onlyOneReviewPerMemberAndOnlyOwnerCanEdit() {
        Member admin = newAdmin();
        Member u1 = newUser("user_d", "유저D");
        Member u2 = newUser("user_e", "유저E");
        Restaurant r = restaurantService.create(restaurantForm("중복 테스트집"), admin);

        var review = reviewService.write(r.getId(), u1, reviewForm(4, "좋아요 좋아요"));
        assertThrows(BusinessException.class, () -> reviewService.write(r.getId(), u1, reviewForm(5, "또 쓰기")));
        assertThrows(ForbiddenException.class, () -> reviewService.update(review.getId(), u2, reviewForm(1, "남의 리뷰 수정")));
        assertThrows(ForbiddenException.class, () -> reviewService.delete(review.getId(), u2));
    }

    @Test
    void cannotReviewPendingRestaurant() {
        Member user = newUser("user_f", "유저F");
        Restaurant pending = restaurantService.create(restaurantForm("대기중 가게"), user);
        assertThrows(BusinessException.class, () -> reviewService.write(pending.getId(), user, reviewForm(5, "승인 전 리뷰")));
    }
}
