package com.example.busan_matzido.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewForm {

    @NotNull(message = "별점을 선택해 주세요.")
    @Min(value = 1, message = "별점은 1~5점입니다.")
    @Max(value = 5, message = "별점은 1~5점입니다.")
    private Integer rating;

    @NotBlank(message = "리뷰 내용을 입력해 주세요.")
    @Size(min = 5, max = 1000, message = "리뷰는 5~1000자로 입력해 주세요.")
    private String content;
}
