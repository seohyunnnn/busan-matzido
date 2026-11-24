# 🌊 부산 맛지도 (busan-matzido)

부산의 맛집을 소개하고, 다녀온 사람들이 리뷰를 남길 수 있는 웹 서비스입니다.
개인 프로젝트로 기획부터 설계, 개발, 테스트까지 혼자 진행했습니다.

## 1. 개발 환경

| 구분 | 내용 |
|---|---|
| IDE | IntelliJ IDEA |
| Language | Java 21 |
| Framework | Spring Boot 4.1.2 (Spring MVC) |
| ORM | Spring Data JPA (Hibernate) |
| 보안 | Spring Security (폼 로그인, BCrypt 암호화) |
| 화면 | Thymeleaf, HTML / CSS / JavaScript |
| DB | MySQL 8 |
| 빌드 | Gradle |
| 외부 API | Kakao 지도 JavaScript API (선택) |
| 실행 환경 | 내 컴퓨터(localhost) |
| 배포 계획 | AWS Lightsail 배포 예정 |

## 2. 주요 기능

### 회원 관리
- 회원가입 (아이디·닉네임·이메일 중복 검사, 비밀번호 BCrypt 암호화)
- 로그인 / 로그아웃 (Spring Security), 정지된 계정은 로그인 차단
- 마이페이지: 내가 등록한 맛집과 내가 쓴 리뷰 확인
- 회원 정보 수정: 닉네임, 비밀번호 변경

### 관리자 계정
- 앱을 처음 실행하면 관리자 계정이 자동으로 만들어집니다.
- 맛집 승인 / 반려 / 삭제
- 회원 정지·해제, 관리자 권한 부여·해제
- 부적절한 리뷰 삭제, 통계 대시보드

### 맛집 등록
- 로그인한 회원이 맛집을 등록하면 **승인 대기** 상태가 되고, 관리자가 승인하면 공개됩니다. (관리자가 등록하면 바로 공개)
- 일반 회원이 수정하면 다시 승인 대기로 돌아갑니다.
- 분류(돼지국밥, 밀면, 해산물 등)와 부산 16개 구·군으로 구분합니다.
- 키워드(이름·대표 메뉴·주소) 검색, 분류·지역 필터, 최신순·평점순·리뷰순 정렬
- 대표 이미지는 이미지 주소(URL)를 입력하는 방식입니다.
- 지도 페이지에서 맛집 위치를 마커로 확인할 수 있습니다. (Kakao 지도 키 설정 시)

### 맛집 리뷰
- 별점(1~5)과 내용을 작성하며, 한 회원은 맛집마다 리뷰를 1개만 쓸 수 있습니다.
- 본인 리뷰는 수정·삭제할 수 있고, 관리자는 모든 리뷰를 삭제할 수 있습니다.
- 맛집별 평균 평점과 리뷰 수가 자동으로 계산됩니다.

## 3. 실행 방법

### 3-1. 준비물
- JDK 21
- MySQL 8

### 3-2. DB 설정
`application.properties`의 DB 설정을 내 MySQL에 맞게 사용합니다.
환경 변수로 바꿀 수도 있습니다.

| 환경 변수 | 설명 |
|---|---|
| `DB_URL` | MySQL 접속 주소 |
| `DB_USERNAME` | DB 계정 |
| `DB_PASSWORD` | DB 비밀번호 (코드에 직접 적지 않기) |
| `ADMIN_USERNAME` | 관리자 아이디 |
| `ADMIN_PASSWORD` | 관리자 비밀번호 (반드시 변경) |
| `KAKAO_MAP_KEY` | Kakao 지도 JavaScript 키 (없으면 지도만 비활성화) |

### 3-3. 실행
IntelliJ에서 `BusanMatzidoApplication`을 실행하거나 터미널에서 아래 명령을 입력합니다.

```bash
./gradlew bootRun
```

브라우저에서 http://localhost:8080 으로 접속합니다. (`server.port`를 바꿨다면 해당 포트)

### 3-4. 테스트
```bash
./gradlew test
```
테스트는 H2 인메모리 DB를 사용해서 MySQL 없이 실행됩니다.

## 4. 프로젝트 구조

```
com.example.busan_matzido
├─ global       공통 (BaseTimeEntity, 예외, 로그인 회원 모델 주입, 지도 설정)
├─ config       SecurityConfig, DataInitializer (관리자·샘플 데이터)
├─ member       회원 (가입, 로그인, 마이페이지)
├─ restaurant   맛집 (검색, 등록, 수정, 승인)
├─ review       리뷰 (작성, 수정, 삭제, 평점 집계)
└─ admin        관리자 화면

src/main/resources
├─ templates    Thymeleaf 화면
├─ static       css, js
└─ application.properties
```

## 5. 데이터베이스

테이블은 3개입니다. 자세한 ERD는 [기획·설계 문서](docs/planning-design.md)에 있습니다.

| 테이블 | 설명 |
|---|---|
| `members` | 회원 및 관리자 |
| `restaurants` | 맛집 정보, 승인 상태, 평균 평점·리뷰 수 |
| `reviews` | 맛집 리뷰 (회원당 맛집별 1개) |

## 6. 문서

- [기획·설계 문서](docs/planning-design.md): 요구사항 정의서, ERD, 화면 설계

## 7. AI 코드 검증 일지

AI가 작성한 코드를 그대로 쓰지 않고, 직접 실행하고 점검해서 찾은 결함과 개선 내용을 기록했습니다.

<!-- 검증일지 엑셀의 'README표' 시트에 모인 표를 이 줄 아래에 그대로 붙여넣으세요. -->

## 8. 개발하면서 신경 쓴 점

- 비밀번호는 BCrypt로 암호화해서 저장했습니다.
- DB 비밀번호, 관리자 비밀번호, 지도 키는 코드에 직접 쓰지 않고 환경 변수로 받습니다.
- 모든 POST 요청에 CSRF 토큰을 적용했고, 관리자 화면은 URL 단위로 권한을 막았습니다.
- 일반 회원이 등록한 맛집은 관리자 승인 후에만 공개해서 잘못된 정보를 걸러냅니다.
- 평균 평점과 리뷰 수를 맛집 테이블에 함께 저장해서 목록 정렬 시 집계 쿼리를 쓰지 않습니다.

## 9. 앞으로 추가하고 싶은 기능

- 이미지 파일 업로드
- 즐겨찾기, 리뷰 사진 첨부
- 소셜 로그인
