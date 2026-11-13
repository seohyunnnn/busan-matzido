package com.example.busan_matzido.restaurant;

import com.example.busan_matzido.global.BaseTimeEntity;
import com.example.busan_matzido.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "restaurants", indexes = {
        @Index(name = "idx_restaurant_status", columnList = "status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private District district;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(length = 30)
    private String phone;

    @Column(length = 100)
    private String openingHours;

    @Column(length = 200)
    private String representativeMenu;

    @Column(length = 2000)
    private String description;

    @Column(length = 500)
    private String imageUrl;

    private Double latitude;

    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RestaurantStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    // 리뷰 집계값 (목록 정렬/표시를 빠르게 하려고 함께 저장)
    @Column(nullable = false)
    private int reviewCount;

    @Column(nullable = false)
    private int ratingTotal;

    @Column(nullable = false)
    private double averageRating;

    @Builder
    public Restaurant(String name, Category category, District district, String address, String phone,
                      String openingHours, String representativeMenu, String description, String imageUrl,
                      Double latitude, Double longitude, RestaurantStatus status, Member author) {
        this.name = name;
        this.category = category;
        this.district = district;
        this.address = address;
        this.phone = blankToNull(phone);
        this.openingHours = blankToNull(openingHours);
        this.representativeMenu = blankToNull(representativeMenu);
        this.description = blankToNull(description);
        this.imageUrl = blankToNull(imageUrl);
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status == null ? RestaurantStatus.PENDING : status;
        this.author = author;
    }

    public void update(String name, Category category, District district, String address, String phone,
                       String openingHours, String representativeMenu, String description, String imageUrl,
                       Double latitude, Double longitude) {
        this.name = name;
        this.category = category;
        this.district = district;
        this.address = address;
        this.phone = blankToNull(phone);
        this.openingHours = blankToNull(openingHours);
        this.representativeMenu = blankToNull(representativeMenu);
        this.description = blankToNull(description);
        this.imageUrl = blankToNull(imageUrl);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // ---------- 상태 ----------
    public void approve() {
        this.status = RestaurantStatus.APPROVED;
    }

    public void reject() {
        this.status = RestaurantStatus.REJECTED;
    }

    public void markPending() {
        this.status = RestaurantStatus.PENDING;
    }

    public boolean isApproved() {
        return status == RestaurantStatus.APPROVED;
    }

    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }

    // ---------- 리뷰 집계 ----------
    public void addRating(int rating) {
        this.reviewCount++;
        this.ratingTotal += rating;
        recalculate();
    }

    public void changeRating(int oldRating, int newRating) {
        this.ratingTotal += (newRating - oldRating);
        recalculate();
    }

    public void removeRating(int rating) {
        this.reviewCount = Math.max(0, this.reviewCount - 1);
        this.ratingTotal = Math.max(0, this.ratingTotal - rating);
        recalculate();
    }

    private void recalculate() {
        this.averageRating = reviewCount == 0 ? 0.0 : Math.round(ratingTotal * 10.0 / reviewCount) / 10.0;
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
