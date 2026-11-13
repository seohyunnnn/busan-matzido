package com.example.busan_matzido.global;

import com.example.busan_matzido.restaurant.Category;
import com.example.busan_matzido.restaurant.District;
import org.springframework.stereotype.Component;

/** 템플릿의 select 옵션용: ${@enums.categories()} / ${@enums.districts()} */
@Component("enums")
public class EnumOptions {

    public Category[] categories() {
        return Category.values();
    }

    public District[] districts() {
        return District.values();
    }
}
