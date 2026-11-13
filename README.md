# 🌊 부산 맛지도 (busan-matzido)

부산의 맛집을 소개하고, 다녀온 사람들이 리뷰를 남길 수 있는 웹 서비스입니다.
회원 관리, 관리자 계정, 맛집 등록, 맛집 리뷰를 핵심 기능으로 만들었고,
이후 사진 업로드와 AI 연동 기능을 추가했습니다.

## 1. 개발 환경

| 구분 | 내용 |
|---|---|
| IDE | IntelliJ IDEA |
| Language | Java 21 |
| Framework | Spring Boot 4.1.2 (Spring MVC, Spring Data JPA, Spring Security, Validation) |
| 화면 | Thymeleaf |
| 인증 | JWT 토큰 |
| DB | MySQL 8 |
| AI | Spring AI + OpenRouter (무료 모델만 사용) |
| 파일 저장 | 내 PC `uploads` 폴더 또는 AWS S3 |
| 빌드 | Gradle |

## 2. 주요 기능

### 회원 관리
- 회원가입(아이디·닉네임·이메일 중복 검사), 로그인/로그아웃
- 로그인 시 JWT 토큰 발급 (유효 시간 24시간)
- 마이페이지: 내가 등록한 맛집, 내가 쓴 리뷰 확인, 회원 정보 수정

### 관리자 계정
- 맛집 승인 / 반려 / 삭제
- 회원 정지 및 권한 관리
- 부적절한 리뷰 삭제, 통계 확인

### 맛집 등록
- 로그인한 회원이 맛집을 등록하면 관리자 승인 후 공개
- 분류(돼지국밥, 밀면, 해산물 등)와 부산 16개 구·군으로 구분
- 검색(이름·메뉴·주소), 분류/지역 필터, 최신·평점·리뷰순 정렬
- 대표 사진 업로드 (최대 5MB)

### 맛집 리뷰
- 별점(1~5)과 내용 작성, 회원당 맛집별 1개
- 본인 리뷰 수정/삭제, 맛집별 평균 평점 자동 계산

### AI 연동
- OpenRouter의 **무료 모델(`:free`)** 만 사용하도록 제한했습니다.
- 회원 1명당 하루 5회까지 호출할 수 있습니다.

## 3. 실행 방법

### 3-1. 준비물
- JDK 21
- MySQL 8

### 3-2. DB 만들기
MySQL에 접속해서 데이터베이스와 계정을 먼저 만들어 둬야 합니다.

```sql
CREATE DATABASE matzip DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'matzip'@'localhost' IDENTIFIED BY 'matzip1234';
GRANT ALL PRIVILEGES ON matzip.* TO 'matzip'@'localhost';
FLUSH PRIVILEGES;
```

> 계정 정보를 다르게 쓰고 싶으면 아래 환경 변수로 바꿔서 실행하면 됩니다.

### 3-3. 실행
IntelliJ에서 `BusanMatzidoApplication`을 실행하거나 터미널에서 아래 명령을 입력합니다.

```bash
./gradlew bootRun
```

실행 후 브라우저에서 **http://localhost:8082** 로 접속합니다.
(다른 프로젝트가 8080을 쓰고 있어도 겹치지 않게 8082로 정했습니다.)

### 3-4. 테스트
```bash
./gradlew test
```

## 4. 환경 변수

`application.properties`는 `${이름:기본값}` 형식이라, 환경 변수가 있으면 그 값을 쓰고 없으면 기본값을 씁니다.
IntelliJ에서는 *Run → Edit Configurations → Environment variables* 에 넣으면 됩니다.

| 환경 변수 | 기본값 | 설명 |
|---|---|---|
| `DB_HOST` | `localhost` | MySQL 주소 |
| `DB_NAME` | `matzip` | 데이터베이스 이름 |
| `DB_USERNAME` | `matzip` | DB 계정 |
| `DB_PASSWORD` | `matzip1234` | DB 비밀번호 (실제 서비스에서는 반드시 변경) |
| `DDL_AUTO` | `create` | 테이블 생성 방식 (아래 참고) |
| `SQL_INIT_MODE` | `always` | `data.sql` 예제 데이터 입력 여부 |
| `JWT_SECRET` | (빈 값) | JWT 비밀키. 비어 있으면 실행할 때마다 임의로 생성 |
| `OPENROUTER_API_KEY` | `no-key` | OpenRouter API 키. 없어도 서버는 켜지지만 AI 호출은 실패 |
| `OPENROUTER_MODEL` | `meta-llama/llama-3.3-70b-instruct:free` | 사용할 무료 모델 |
| `APP_STORAGE` | `local` | 사진 저장 위치: `local` 또는 `s3` |
| `S3_BUCKET` | (빈 값) | S3 버킷 이름 (`APP_STORAGE=s3` 일 때) |
| `AWS_REGION` | `ap-northeast-2` | S3 리전 |

### ⚠️ DDL_AUTO 주의
- `create`(기본값): **실행할 때마다 테이블을 지우고 다시 만든 뒤** `data.sql` 예제 데이터를 넣습니다. 개발이나 발표 연습용입니다.
- 데이터를 남기고 싶으면 `DDL_AUTO=update` 와 `SQL_INIT_MODE=never` 를 **함께** 설정하세요. (안 하면 예제 데이터가 중복으로 들어갑니다.)

### JWT 비밀키
- 비밀키를 코드에 적지 않고 환경 변수로만 받도록 했습니다.
- `JWT_SECRET` 이 없으면 개발용으로 임의 키를 만들기 때문에, **서버를 다시 켜면 이전에 발급된 토큰은 무효**가 됩니다.
- 실제 서비스에서는 32자 이상 임의 문자열을 넣어서 실행합니다.

```bash
JWT_SECRET=아주-긴-임의-문자열 ./gradlew bootRun
```

## 5. 파일 업로드와 저장 위치

- 업로드 한도: 파일 1개 5MB (휴대폰 사진 기준으로 기본값 1MB에서 늘렸습니다.)
- `APP_STORAGE=local` : 내 PC의 `uploads` 폴더에 저장
- `APP_STORAGE=s3` : AWS S3 버킷에 저장 (`S3_BUCKET`, `AWS_REGION` 설정 필요)

## 6. AI 기능 설정

1. [OpenRouter](https://openrouter.ai)에서 API 키를 발급받습니다.
2. `OPENROUTER_API_KEY` 환경 변수에 키를 넣고 실행합니다.
3. 모델은 반드시 `:free` 로 끝나는 무료 모델이어야 하고, 아니면 서버 시작 시 막히도록 했습니다.
4. 무료 모델 목록은 자주 바뀌어서, 오류가 나면 `OPENROUTER_MODEL` 만 다른 무료 모델로 바꿉니다.

## 7. 배포 (AWS EC2)

- MySQL 설치: `deploy/mysql-setup.sh`
- 접속 정보: `matzip.env` 의 `DB_HOST`, `DB_USERNAME`, `DB_PASSWORD`
- 사진은 EC2 디스크 대신 S3에 저장하도록 `APP_STORAGE=s3` 로 설정합니다.

## 8. 프로젝트 구조

```
com.example.busan_matzido
├─ global       공통 (BaseTimeEntity, 예외 처리, 공통 설정)
├─ config       보안, 초기 데이터 설정
├─ member       회원 (가입, 로그인, 마이페이지)
├─ restaurant   맛집 (검색, 등록, 수정, 승인)
├─ review       리뷰 (작성, 수정, 삭제, 평점 집계)
└─ admin        관리자

src/main/resources
├─ templates    Thymeleaf 화면
├─ static       css, js
├─ data.sql     예제 데이터
└─ application.properties
```

## 9. 문서

- [기획·설계 문서](docs/planning-design.md) — 요구사항 정의서, ERD, 화면 설계, GitHub 사용법
- [팀 프로젝트 변경 사항](docs/TEAM_PROJECT_CHANGES.md)

## 10. 개발하면서 신경 쓴 점

- 비밀번호는 BCrypt로 암호화해서 저장했습니다.
- JWT 비밀키, DB 비밀번호, API 키는 코드에 직접 쓰지 않고 환경 변수로 받습니다.
- 오류가 났을 때 스택 트레이스와 예외 클래스 이름이 브라우저로 나가지 않게 막았습니다. 자세한 내용은 Run 창 로그에서 확인합니다.
- 일반 회원이 등록한 맛집은 관리자 승인 후에만 공개되도록 해서 잘못된 정보를 걸러냅니다.

## 11. 앞으로 추가하고 싶은 기능

- 즐겨찾기(찜), 리뷰 사진 첨부
- 소셜 로그인
- 맛집 신고 기능
