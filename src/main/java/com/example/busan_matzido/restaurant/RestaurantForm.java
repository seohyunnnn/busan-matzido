package com.example.busan_matzido.restaurant;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantForm {

    @NotBlank(message = "가게 이름을 입력해 주세요.")
    @Size(max = 100, message = "가게 이름은 100자 이하로 입력해 주세요.")
    private String name;

    @NotNull(message = "분류를 선택해 주세요.")
    private Category category;

    @NotNull(message = "지역(구/군)을 선택해 주세요.")
    private District district;

    @NotBlank(message = "주소를 입력해 주세요.")
    @Size(max = 200, message = "주소는 200자 이하로 입력해 주세요.")
    private String address;

    @Size(max = 30, message = "전화번호는 30자 이하로 입력해 주세요.")
    private String phone;

    @Size(max = 100, message = "영업시간은 100자 이하로 입력해 주세요.")
    private String openingHours;

    @Size(max = 200, message = "대표 메뉴는 200자 이하로 입력해 주세요.")
    private String representativeMenu;

    @Size(max = 2000, message = "소개는 2000자 이하로 입력해 주세요.")
    private String description;

    @Size(max = 500, message = "이미지 주소는 500자 이하로 입력해 주세요.")
    @Pattern(regexp = "^$|^https?://.*", message = "이미지 주소는 http:// 또는 https:// 로 시작해야 합니다.")
    private String imageUrl;

    @DecimalMin(value = "33.0", message = "위도 값이 올바르지 않습니다.")
    @DecimalMax(value = "39.0", message = "위도 값이 올바르지 않습니다.")
    private Double latitude;

    @DecimalMin(value = "124.0", message = "경도 값이 올바르지 않습니다.")
    @DecimalMax(value = "132.0", message = "경도 값이 올바르지 않습니다.")
    private Double longitude;

    public static RestaurantForm from(Restaurant r) {
        RestaurantForm f = new RestaurantForm();
        f.name = r.getName();
        f.category = r.getCategory();
        f.district = r.getDistrict();
        f.address = r.getAddress();
        f.phone = r.getPhone();
        f.openingHours = r.getOpeningHours();
        f.representativeMenu = r.getRepresentativeMenu();
        f.description = r.getDescription();
        f.imageUrl = r.getImageUrl();
        f.latitude = r.getLatitude();
        f.longitude = r.getLongitude();
        return f;
    }
}
