package com.example.busan_matzido.review;

import com.example.busan_matzido.global.BaseTimeEntity;
import com.example.busan_matzido.member.Member;
import com.example.busan_matzido.restaurant.Restaurant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 한 회원은 한 맛집에 리뷰를 1개만 작성할 수 있습니다. */
@Entity
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(name = "uk_review_restaurant_member", columnNames = {"restaurant_id", "member_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = 1000)
    private String content;

    @Builder
    public Review(Restaurant restaurant, Member member, int rating, String content) {
        this.restaurant = restaurant;
        this.member = member;
        this.rating = rating;
        this.content = content;
    }

    public void update(int rating, String content) {
        this.rating = rating;
        this.content = content;
    }

    /** ★★★★☆ 형태의 표시용 문자열 */
    public String getStars() {
        return "★".repeat(rating) + "☆".repeat(Math.max(0, 5 - rating));
    }
}
