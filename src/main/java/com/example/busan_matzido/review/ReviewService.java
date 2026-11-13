package com.example.busan_matzido.review;

import com.example.busan_matzido.global.BusinessException;
import com.example.busan_matzido.global.ForbiddenException;
import com.example.busan_matzido.global.NotFoundException;
import com.example.busan_matzido.member.Member;
import com.example.busan_matzido.restaurant.Restaurant;
import com.example.busan_matzido.restaurant.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;

    public List<Review> findByRestaurant(Long restaurantId) {
        return reviewRepository.findByRestaurant_IdOrderByCreatedAtDesc(restaurantId);
    }

    public List<Review> findByMember(Long memberId) {
        return reviewRepository.findByMember_IdOrderByCreatedAtDesc(memberId);
    }

    public Page<Review> list(int page) {
        return reviewRepository.findAll(
                PageRequest.of(Math.max(page, 0), 15, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public long count() {
        return reviewRepository.count();
    }

    /** 리뷰가 달린 맛집 id (검증 실패 시 돌아갈 화면 결정용) */
    public Long findRestaurantId(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(NotFoundException::new)
                .getRestaurant().getId();
    }

    @Transactional
    public Review write(Long restaurantId, Member member, ReviewForm form) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(NotFoundException::new);
        if (!restaurant.isApproved()) {
            throw new BusinessException("공개된 맛집에만 리뷰를 작성할 수 있습니다.");
        }
        if (reviewRepository.existsByRestaurant_IdAndMember_Id(restaurantId, member.getId())) {
            throw new BusinessException("이미 이 맛집에 리뷰를 작성하셨습니다. 기존 리뷰를 수정해 주세요.");
        }
        Review review = reviewRepository.save(Review.builder()
                .restaurant(restaurant)
                .member(member)
                .rating(form.getRating())
                .content(form.getContent().trim())
                .build());
        restaurant.addRating(form.getRating());
        return review;
    }

    /** @return 리뷰가 달린 맛집 id (리다이렉트용) */
    @Transactional
    public Long update(Long reviewId, Member member, ReviewForm form) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(NotFoundException::new);
        if (!review.getMember().getId().equals(member.getId())) {
            throw new ForbiddenException();
        }
        Restaurant restaurant = review.getRestaurant();
        restaurant.changeRating(review.getRating(), form.getRating());
        review.update(form.getRating(), form.getContent().trim());
        return restaurant.getId();
    }

    /** 작성자 본인 또는 관리자만 삭제 가능. @return 맛집 id */
    @Transactional
    public Long delete(Long reviewId, Member member) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(NotFoundException::new);
        if (!member.isAdmin() && !review.getMember().getId().equals(member.getId())) {
            throw new ForbiddenException();
        }
        Restaurant restaurant = review.getRestaurant();
        restaurant.removeRating(review.getRating());
        reviewRepository.delete(review);
        return restaurant.getId();
    }
}
