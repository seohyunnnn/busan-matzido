package com.example.busan_matzido.restaurant;

import com.example.busan_matzido.global.ForbiddenException;
import com.example.busan_matzido.global.NotFoundException;
import com.example.busan_matzido.member.Member;
import com.example.busan_matzido.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private static final int PAGE_SIZE = 9;

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;

    // ---------- 조회 ----------

    public Page<Restaurant> search(String keyword, Category category, District district, String sort, int page) {
        Sort order = switch (sort == null ? "latest" : sort) {
            case "rating" -> Sort.by(Sort.Direction.DESC, "averageRating")
                    .and(Sort.by(Sort.Direction.DESC, "reviewCount"));
            case "reviews" -> Sort.by(Sort.Direction.DESC, "reviewCount");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, order);
        String kw = keyword == null ? "" : keyword.trim();
        return restaurantRepository.search(RestaurantStatus.APPROVED, kw, category, district, pageable);
    }

    public List<Restaurant> latest() {
        return restaurantRepository.findTop6ByStatusOrderByCreatedAtDesc(RestaurantStatus.APPROVED);
    }

    public List<Restaurant> topRated() {
        return restaurantRepository
                .findTop6ByStatusAndReviewCountGreaterThanOrderByAverageRatingDescReviewCountDesc(
                        RestaurantStatus.APPROVED, 0);
    }

    public List<Restaurant> findMappable() {
        return restaurantRepository.findByStatusAndLatitudeIsNotNullAndLongitudeIsNotNull(RestaurantStatus.APPROVED);
    }

    public List<Restaurant> findByAuthor(Long memberId) {
        return restaurantRepository.findByAuthor_IdOrderByCreatedAtDesc(memberId);
    }

    public Page<Restaurant> findByStatus(RestaurantStatus status, int page) {
        return restaurantRepository.findByStatus(status,
                PageRequest.of(Math.max(page, 0), 15, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public long countByStatus(RestaurantStatus status) {
        return restaurantRepository.countByStatus(status);
    }

    /** 공개된 맛집은 누구나, 승인 대기/반려는 작성자와 관리자만 볼 수 있음 */
    public Restaurant getForView(Long id, Member viewer) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(NotFoundException::new);
        if (!restaurant.isApproved() && !canManage(restaurant, viewer)) {
            throw new NotFoundException();
        }
        return restaurant;
    }

    public Restaurant getForEdit(Long id, Member editor) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(NotFoundException::new);
        if (!canManage(restaurant, editor)) {
            throw new ForbiddenException();
        }
        return restaurant;
    }

    public boolean canManage(Restaurant restaurant, Member member) {
        return member != null && (member.isAdmin() || restaurant.getAuthor().getId().equals(member.getId()));
    }

    // ---------- 등록 / 수정 / 삭제 ----------

    /** 관리자가 등록하면 바로 공개, 일반 회원은 승인 대기 */
    @Transactional
    public Restaurant create(RestaurantForm form, Member author) {
        Restaurant restaurant = Restaurant.builder()
                .name(form.getName().trim())
                .category(form.getCategory())
                .district(form.getDistrict())
                .address(form.getAddress().trim())
                .phone(form.getPhone())
                .openingHours(form.getOpeningHours())
                .representativeMenu(form.getRepresentativeMenu())
                .description(form.getDescription())
                .imageUrl(form.getImageUrl())
                .latitude(form.getLatitude())
                .longitude(form.getLongitude())
                .status(author.isAdmin() ? RestaurantStatus.APPROVED : RestaurantStatus.PENDING)
                .author(author)
                .build();
        return restaurantRepository.save(restaurant);
    }

    /** 일반 회원이 수정하면 다시 승인 대기로 돌아감 (관리자는 그대로 유지) */
    @Transactional
    public Restaurant update(Long id, RestaurantForm form, Member editor) {
        Restaurant restaurant = getForEdit(id, editor);
        restaurant.update(form.getName().trim(), form.getCategory(), form.getDistrict(), form.getAddress().trim(),
                form.getPhone(), form.getOpeningHours(), form.getRepresentativeMenu(), form.getDescription(),
                form.getImageUrl(), form.getLatitude(), form.getLongitude());
        if (!editor.isAdmin()) {
            restaurant.markPending();
        }
        return restaurant;
    }

    @Transactional
    public void delete(Long id, Member member) {
        Restaurant restaurant = getForEdit(id, member);
        reviewRepository.deleteAllByRestaurantId(restaurant.getId());
        restaurantRepository.delete(restaurant);
    }

    // ---------- 관리자 승인 ----------

    @Transactional
    public void approve(Long id) {
        restaurantRepository.findById(id).orElseThrow(NotFoundException::new).approve();
    }

    @Transactional
    public void reject(Long id) {
        restaurantRepository.findById(id).orElseThrow(NotFoundException::new).reject();
    }
}
