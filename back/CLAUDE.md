# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 빌드 및 개발 명령어

```bash
./gradlew build          # 프로젝트 빌드
./gradlew bootRun        # Spring Boot 애플리케이션 실행
./gradlew test           # 전체 테스트 실행
./gradlew clean          # 빌드 결과물 삭제
```

### 단일 테스트 실행

```bash
./gradlew test --tests "com.back.domain.post.post.controller.ApiV1PostControllerTest.t1"
```

### 환경 설정

`.env` 파일은 `application.yaml`의 `spring.config.import: optional:file:.env[.properties]`로 로드됨.
`.env.default`를 `.env`로 복사 후 설정 (변수명은 `__` 이중 언더스코어 규칙):

- `CUSTOM__JWT__SECRET_KEY`
- Kakao, Google, Naver OAuth 클라이언트 ID/시크릿

### 프로필 및 데이터베이스

- 기본 활성 프로필: `dev` — 파일 기반 H2 (`./db_dev.mv.db`, MySQL 모드, `ddl-auto: update`)
- `test` 프로필 — 인메모리 H2
- 시드 데이터: `NotProdInitData`(테스트 계정 생성), `DevInitData`. OAuth 테스트 계정은 `application.yaml`의 `custom.notProdMembers`에 정의
  (apiKey: `kakao__1` 등)

## 기술 스택

- **Kotlin 2.3.21** (main) / **Java 25** / **Spring Boot 4.1.0**
- **주의**: 프로덕션 코드는 Kotlin (`src/main/kotlin`), 테스트 코드는 Java (`src/test/java`)로 작성됨
- **Gradle 9.5.1** (Kotlin DSL), allOpen 플러그인으로 JPA 엔티티 open 처리
- **H2** 데이터베이스 (MySQL 모드)
- **Spring Security** - JWT (JJWT) + OAuth2 (Kakao, Google, Naver)
- **SpringDoc OpenAPI 3.0.3** - API 문서화

## 아키텍처

### 패키지 구조

```
com.back/
├── domain/              # 기능 모듈 (비즈니스 로직)
│   ├── member/member/   # 회원 관리 및 인증
│   └── post/            # 게시글 및 댓글 관리
│       ├── post/
│       └── postComment/
├── global/              # 공통 관심사
│   ├── aspect/          # ResponseAspect - RsData.statusCode를 HTTP 상태로 반영
│   ├── exception/       # ServiceException
│   ├── globalExceptionHandler/
│   ├── initData/        # NotProdInitData, DevInitData (시드 데이터)
│   ├── jpa/entity/      # BaseEntity (감사 필드)
│   ├── rq/              # Rq - 요청 컨텍스트 (현재 사용자, 쿠키, 헤더)
│   ├── rsData/          # RsData<T> - 표준 응답 래퍼
│   └── security/        # SecurityConfig, CustomAuthenticationFilter, OAuth2 핸들러
└── standard/            # 유틸리티 (Ut.kt) 및 Kotlin 확장 함수
```

### 각 도메인 모듈 구성

- `controller/` - REST 엔드포인트 (ApiV1 *Controller, ApiV1Adm*Controller)
- `service/` - 비즈니스 로직
- `repository/` - Spring Data JPA 리포지토리
- `entity/` - BaseEntity를 상속하는 JPA 엔티티
- `dto/` - 데이터 전송 객체

### 주요 패턴

**응답 래퍼**: 컨트롤러는 `RsData<T>`를 그대로 반환:

```kotlin
RsData("201-1", "글이 작성되었습니다.", PostDto(post))
// resultCode 형식: "{상태코드}-{순번}"
```

HTTP 상태 코드는 `ResponseAspect`(AOP)가 resultCode의 앞부분을 파싱해 자동 설정함. `ResponseEntity` 사용하지 않음.

**예외 처리**: `ServiceException(resultCode, message)` throw - `GlobalExceptionHandler`가 RsData 응답으로 변환.

**요청 컨텍스트**: `Rq` 주입하여 현재 인증된 사용자 (`rq.actor` - SecurityContext 기반 비영속 객체, `rq.actorFromDb` - DB 조회), 쿠키, 헤더 접근.

**권한 검사**: `post.checkActorCanModify(actor)` / `checkActorCanDelete(actor)` 같은 엔티티 메서드가 권한 없으면 ServiceException throw.

### 인증 흐름

`CustomAuthenticationFilter`가 `/api/**` 요청에서 인증 처리:

- 헤더: `Authorization: Bearer {apiKey} {accessToken}` (accessToken 생략 가능)
- 또는 쿠키: `apiKey`, `accessToken`
- accessToken이 만료/무효이면 apiKey로 회원 조회 후 새 accessToken을 쿠키와 `Authorization` 응답 헤더에 자동 재발급

URL별 인가 규칙은 `SecurityConfig`에 중앙 집중됨 — 새 공개 API 추가 시 여기에 등록 필요:

- `/api/*/adm/**` → ADMIN 역할
- `/api/*/**` → 인증 필요 (화이트리스트 제외)
- API 외 경로 → permitAll

### 결과 코드

- `200-1`: 성공
- `201-1`: 생성됨
- `400-1`: 잘못된 요청
- `401-1`: 로그인 필요
- `401-2`: Authorization 헤더 형식 오류
- `401-3`: 잘못된 API 키
- `403-1`: 권한 없음
- `404-1`: 찾을 수 없음
- `409-1`: 충돌/중복

## API 엔드포인트

- 공개: `GET /api/v1/posts`, `GET /api/v1/posts/{id}`, 댓글 조회, 로그인/로그아웃, 회원가입
- 인증 필요: `POST/PUT/DELETE /api/v1/posts/*`, 댓글 작성/수정/삭제
- 관리자 전용: `/api/v1/adm/**`

## 테스트

JUnit 5와 Spring MockMvc 사용 (Java로 작성, `src/test/java`):

- `@SpringBootTest` + `@AutoConfigureMockMvc` (import는
  `org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc` — Spring Boot 4 패키지)
- `@ActiveProfiles("test")` - 인메모리 H2 사용
- `@WithUserDetails("user1")` - NotProdInitData가 생성한 계정으로 인증된 요청 테스트
- `@Transactional` - 자동 롤백
