package com.example.busan_matzido.restaurant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /** 목록 검색: 키워드(이름/대표메뉴/주소) + 분류 + 지역. keyword 는 빈 문자열이면 전체. */
    @Query("""
            select r from Restaurant r
            where r.status = :status
              and (:category is null or r.category = :category)
              and (:district is null or r.district = :district)
              and (:keyword = ''
                   or r.name like concat('%', :keyword, '%')
                   or r.representativeMenu like concat('%', :keyword, '%')
                   or r.address like concat('%', :keyword, '%'))
            """)
    Page<Restaurant> search(@Param("status") RestaurantStatus status,
                            @Param("keyword") String keyword,
                            @Param("category") Category category,
                            @Param("district") District district,
                            Pageable pageable);

    List<Restaurant> findTop6ByStatusOrderByCreatedAtDesc(RestaurantStatus status);

    List<Restaurant> findTop6ByStatusAndReviewCountGreaterThanOrderByAverageRatingDescReviewCountDesc(
            RestaurantStatus status, int minReviews);

    List<Restaurant> findByStatusAndLatitudeIsNotNullAndLongitudeIsNotNull(RestaurantStatus status);

    @EntityGraph(attributePaths = "author")
    Page<Restaurant> findByStatus(RestaurantStatus status, Pageable pageable);

    List<Restaurant> findByAuthor_IdOrderByCreatedAtDesc(Long authorId);

    long countByStatus(RestaurantStatus status);
}
