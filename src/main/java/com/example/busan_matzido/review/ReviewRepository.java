package com.example.busan_matzido.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = "member")
    List<Review> findByRestaurant_IdOrderByCreatedAtDesc(Long restaurantId);

    @EntityGraph(attributePaths = "restaurant")
    List<Review> findByMember_IdOrderByCreatedAtDesc(Long memberId);

    boolean existsByRestaurant_IdAndMember_Id(Long restaurantId, Long memberId);

    @Modifying
    @Query("delete from Review r where r.restaurant.id = :restaurantId")
    void deleteAllByRestaurantId(@Param("restaurantId") Long restaurantId);

    /** 관리자 리뷰 관리 화면용 */
    @Override
    @EntityGraph(attributePaths = {"member", "restaurant"})
    Page<Review> findAll(Pageable pageable);
}
