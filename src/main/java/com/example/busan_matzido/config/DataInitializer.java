package com.example.busan_matzido.config;

import com.example.busan_matzido.member.Member;
import com.example.busan_matzido.member.MemberService;
import com.example.busan_matzido.restaurant.Category;
import com.example.busan_matzido.restaurant.District;
import com.example.busan_matzido.restaurant.Restaurant;
import com.example.busan_matzido.restaurant.RestaurantRepository;
import com.example.busan_matzido.restaurant.RestaurantStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/** 앱 시작 시 관리자 계정(없으면)과 샘플 맛집(옵션)을 만들어 줍니다. */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final String DEFAULT_ADMIN_PASSWORD = "admin1234!";

    private final MemberService memberService;
    private final RestaurantRepository restaurantRepository;
    private final TransactionTemplate transactionTemplate;

    @Value("${app.admin.username:admin}")
    private String adminUsername;
    @Value("${app.admin.password:admin1234!}")
    private String adminPassword;
    @Value("${app.admin.nickname:admin}")
    private String adminNickname;
    @Value("${app.admin.email:admin@busan-matzido.local}")
    private String adminEmail;
    @Value("${app.seed.sample-data:false}")
    private boolean seedSampleData;

    @Override
    public void run(ApplicationArguments args) {
        Member admin = memberService.createAdminIfAbsent(adminUsername, adminPassword, adminNickname, adminEmail);
        if (DEFAULT_ADMIN_PASSWORD.equals(adminPassword)) {
            log.warn("기본 관리자 비밀번호를 사용 중입니다. ADMIN_PASSWORD 환경변수로 반드시 변경하세요.");
        }
        if (seedSampleData) {
            transactionTemplate.executeWithoutResult(status -> seedSamples(admin));
        }
    }

    private void seedSamples(Member admin) {
        if (restaurantRepository.count() > 0) {
            return;
        }
        String note = "[샘플 데이터] 실제 정보로 수정하거나 삭제해서 사용하세요.";
        restaurantRepository.save(sample(admin, "자갈치시장", Category.SEAFOOD, District.JUNG,
                "부산광역시 중구 자갈치해안로 52", "활어회, 꼼장어, 장어구이",
                "부산을 대표하는 수산시장. 바다를 보며 신선한 회를 즐길 수 있어요. " + note, 35.0966, 129.0306));
        restaurantRepository.save(sample(admin, "국제시장 씨앗호떡", Category.SNACK, District.JUNG,
                "부산광역시 중구 BIFF광장 일대", "씨앗호떡",
                "견과류와 씨앗이 가득 든 부산식 호떡. " + note, 35.0983, 129.0285));
        restaurantRepository.save(sample(admin, "부평깡통시장", Category.SNACK, District.JUNG,
                "부산광역시 중구 부평동 일대", "유부주머니, 비빔당면, 어묵",
                "먹거리가 가득한 전통시장. " + note, 35.1009, 129.0261));
        restaurantRepository.save(sample(admin, "해운대시장", Category.KOREAN, District.HAEUNDAE,
                "부산광역시 해운대구 구남로41번길 22", "해산물, 분식, 먹자골목",
                "해운대 해수욕장 인근의 재래시장. " + note, 35.1627, 129.1596));
        log.info("샘플 맛집 데이터를 생성했습니다.");
    }

    private Restaurant sample(Member admin, String name, Category category, District district, String address,
                              String menu, String description, double lat, double lng) {
        return Restaurant.builder()
                .name(name).category(category).district(district).address(address)
                .representativeMenu(menu).description(description)
                .latitude(lat).longitude(lng)
                .status(RestaurantStatus.APPROVED).author(admin)
                .build();
    }
}
