# 🌊 부산 맛지도 (busan-matzido)

부산의 맛집을 소개하고 리뷰를 나누는 개인 프로젝트입니다.

| 항목 | 내용 |
|---|---|
| Framework | Spring Boot 4.1.2 (Spring MVC + Thymeleaf) |
| Language / Build | Java 21 / Gradle |
| DB | MySQL (JPA/Hibernate) — 테스트는 H2 |
| 보안 | Spring Security (폼 로그인, BCrypt, 역할: USER / ADMIN) |
| 지도 | Kakao 지도 JavaScript API (키 없이도 동작, 지도만 비활성화) |

## 주요 기능

**회원 관리**
- 회원가입(아이디/닉네임/이메일 중복 검사, 비밀번호 확인), 로그인/로그아웃
- 마이페이지: 내가 등록한 맛집 · 내가 쓴 리뷰 조회, 닉네임/비밀번호 변경

**관리자 계정** (`/admin`)
- 앱 최초 실행 시 관리자 계정 자동 생성 (기본값 `admin` / `admin1234!` → **반드시 변경**)
- 맛집 승인/반려/삭제, 회원 정지·정지 해제·관리자 권한 부여/해제, 리뷰 삭제, 통계 대시보드

**맛집 등록**
- 로그인한 회원이 등록 → **승인 대기** → 관리자 승인 후 공개 (관리자가 등록하면 즉시 공개)
- 일반 회원이 수정하면 다시 승인 대기로 돌아감
- 분류(돼지국밥/밀면/해산물/…), 부산 16개 구·군, 대표 메뉴, 영업시간, 위도·경도
- 목록: 키워드 검색 + 분류/지역 필터 + 최신/평점/리뷰순 정렬 + 페이징
- 지도 페이지(`/map`): 모든 맛집 마커 표시, 분류 필터

**맛집 리뷰**
- 별점(1~5) + 내용, 회원당 맛집별 1개, 본인 리뷰 수정/삭제 (관리자는 모두 삭제 가능)
- 맛집별 평균 평점/리뷰 수 자동 집계

## 실행 방법

1. **Java 21**, **MySQL 8+** 준비
2. IntelliJ → *Settings → Build, Execution, Deployment → Compiler → Annotation Processors → Enable annotation processing* 체크 (Lombok)
3. DB 접속 정보 설정 — `application.properties` 의 기본값을 수정하거나 환경변수 사용  
   `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`  
   (`createDatabaseIfNotExist=true` 라서 `busan_matzido` 스키마는 자동 생성됩니다)
4. (권장) 관리자 계정 환경변수: `ADMIN_USERNAME`, `ADMIN_PASSWORD`
5. (선택) 지도 사용: https://developers.kakao.com 에서 앱 생성 → **JavaScript 키** 발급 →
   *플랫폼 → Web* 에 `http://localhost:8080` 등록 → 환경변수 `KAKAO_MAP_KEY` 설정
6. `BusanMatzidoApplication` 실행 → http://localhost:8080

```bash
./gradlew bootRun
./gradlew test      # H2 인메모리 DB로 서비스 흐름 테스트
```

최초 실행 시 `app.seed.sample-data=true` 이면 샘플 맛집 4곳이 들어갑니다(위치/주소는 대략적인 값이니 확인 후 수정하세요). 필요 없으면 `false` 로 바꾸세요.

## 구조

```
com.example.busan_matzido
├─ global       공통 (BaseTimeEntity, 예외, 로그인 회원 모델 주입, 지도 설정)
├─ config       SecurityConfig, DataInitializer(관리자·샘플 데이터)
├─ member       Member, 가입/로그인(UserDetailsService)/마이페이지
├─ restaurant   Restaurant, 분류·지역·상태 enum, 검색/등록/수정/승인
├─ review       Review, 작성/수정/삭제 + 평점 집계
└─ admin        관리자 화면 컨트롤러
```

## URL 요약

| 경로 | 권한 | 설명 |
|---|---|---|
| `/`, `/restaurants`, `/restaurants/{id}`, `/map` | 모두 | 홈, 목록, 상세, 지도 |
| `/login`, `/signup` | 모두 | 로그인, 회원가입 |
| `/restaurants/new`, `/restaurants/{id}/edit` | 로그인 | 맛집 등록/수정 |
| `/restaurants/{id}/reviews`, `/reviews/{id}/…` | 로그인 | 리뷰 작성/수정/삭제 |
| `/mypage`, `/mypage/edit` | 로그인 | 마이페이지 |
| `/admin/**` | ADMIN | 관리자 |

## 확장 아이디어
- 이미지 파일 업로드(현재는 이미지 URL 입력), 즐겨찾기(찜), 리뷰 사진
- 리뷰 페이징, 맛집 신고 기능, 소셜 로그인
- 정지된 회원의 기존 세션 즉시 만료 (현재는 다음 로그인부터 차단)
