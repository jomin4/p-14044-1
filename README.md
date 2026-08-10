# p-14044-1 — 코프링 AWS 배포 + Blue/Green 무중단 CI/CD

[slog.gg/p/14044](https://www.slog.gg/p/14044) 강의를 따라가며 **백엔드를 AWS EC2에, 프론트를 Vercel에 배포하고 무중단 배포 파이프라인을 구축**한 실습 프로젝트입니다.

> **현재 AWS 인프라는 `terraform destroy` 로 내려간 상태입니다.**
> 재개 방법과 남은 작업은 [docs/HANDOFF.md](docs/HANDOFF.md) 를 참고하세요.

---

## 구성

```
p-14044-1/
├── back/     Spring Boot 4.1 / Kotlin 2.3 / Java 21   → AWS EC2 (Docker)
├── front/    Next.js 16 / React 19                    → Vercel
├── infra/    Terraform                                → AWS VPC · EC2 · IAM
├── .github/workflows/deploy.yml                       → Blue/Green CI/CD
└── docs/     아키텍처 다이어그램 · 인수인계 문서
```

## 아키텍처

한 도메인 아래 네 개의 이름이 서로 다른 곳을 가리킵니다.

```
jomin4.cloud       →  EC2 npmplus  →  301 → www
www.jomin4.cloud   →  Vercel       →  Next.js 프론트
api.jomin4.cloud   →  EC2 npmplus  →  Spring Boot → MySQL / Redis
npm.jomin4.cloud   →  EC2 npmplus  →  리버스 프록시 관리 콘솔
```

프론트(Vercel)와 백엔드(EC2)는 **서로 직접 통신하지 않습니다.** 브라우저가 Vercel에서 받은 JS를 실행하며 `api.jomin4.cloud` 를 직접 호출하는 구조입니다.

### 서버 내부

```
EC2 (t3.micro)
└── docker network: common (172.18.0.0/16)
    ├── npm_1          npmplus — TLS 종료 · 리버스 프록시 (80/443/81)
    ├── p-14044-1_1    Spring Boot   슬롯 1  ┐ Blue/Green 교대
    ├── p-14044-1_2    Spring Boot   슬롯 2  ┘
    ├── mysql_1        MySQL
    └── redis_1        Redis — 세션 · 캐시
```

앱 컨테이너는 포트를 호스트에 게시하지 않습니다. 같은 슬롯 포트(8080)를 쓰는 두 컨테이너가 **동시에 살아 있어야** 무중단 전환이 성립하기 때문입니다.

## 무중단 배포

`.github/workflows/deploy.yml` 의 3번 잡이 SSM으로 EC2에서 실행합니다.

```
1. 새 이미지 pull
2. nginx 가 현재 가리키는 슬롯을 읽어 blue/green 역할 판정
3. 반대편(green) 슬롯에 새 컨테이너 기동
4. /actuator/health 가 200 될 때까지 대기 (최대 180초)
   → 실패하면 전환하지 않고 중단. blue 가 계속 서비스
5. nginx 업스트림을 green 으로 전환
6. 이전 blue 제거
```

**실측 결과** — 전환 중 외부에서 0.25초 간격으로 요청:

| | 총 요청 | 성공 | 실패 |
|---|---|---|---|
| 전환만 | 139 | 139 | **0** |
| 전체 사이클 (`_2` → `_1`) | 133 | 133 | **0** |

응답시간 p50 42ms / max 115ms. 전환 실패 케이스(400 에러)에서도 87건 전부 200 — **새 버전이 고장 나면 전환하지 않는** 설계가 의도대로 동작했습니다.

## 아키텍처 다이어그램

브라우저로 열어보세요. 라이트/다크 테마를 지원합니다.

| | 내용 |
|---|---|
| [01 이름 → IP](docs/diagrams/01-dns-name-to-ip.html) | DNS 재귀 질의 · 위임 사슬 · 캐시 계층 |
| [02 IP → 컨테이너](docs/diagrams/02-packet-ip-to-container.html) | VPC · 보안그룹 · 도커 네트워크 · 이름의 유효 범위 |
| [03 TLS 1.3 핸드셰이크](docs/diagrams/03-tls13-handshake.html) | 어디서부터 암호화되는가 |
| [04 리다이렉트 체인](docs/diagrams/04-redirect-chain.html) | http → https → www, 세 번의 독립된 왕복 |
| [05 npmplus와 Vercel](docs/diagrams/05-npmplus-vercel-routing.html) | 한 도메인 네 목적지 · 라우팅 분담 |

## 기술 스택

**백엔드** Spring Boot 4.1.0 · Kotlin 2.3.21 · Java 21 · Gradle 9.5.1 · JPA/QueryDSL · Spring Security(JWT + OAuth2) · MySQL · Redis(세션·캐시) · springdoc

**프론트** Next.js 16.2.9 · React 19.2.4 · TypeScript · Tailwind 4 · openapi-fetch

**인프라** Terraform · AWS EC2/VPC/IAM/SSM · Docker · npmplus(nginx) · Let's Encrypt

**CI/CD** GitHub Actions · GHCR · AWS SSM Send-Command · Vercel

## 로컬 실행

```bash
# 백엔드
cd back
cp .env.default .env      # CUSTOM__JWT__SECRET_KEY 는 필수
./gradlew bootRun         # http://localhost:8080

# 프론트
cd front
pnpm install
pnpm dev                  # http://localhost:3000
```

API 문서: `http://localhost:8080/swagger-ui/index.html`

---

## 이 프로젝트에서 다룬 것

- Terraform으로 VPC부터 EC2까지 인프라 코드화
- 도메인 등록·위임·DNS 레코드 (레지스트라와 DNS 호스팅의 분리)
- Let's Encrypt 자동 발급 · HTTP/2 · HTTP/3
- Spring Boot 프로필 분리와 운영 설정 (OSIV · Redis 세션 · 도메인 하드코딩 제거)
- 도커 멀티스테이지 빌드 · 레이어 캐시
- GitHub Actions → GHCR → SSM 파이프라인
- **Blue/Green 무중단 배포와 그 실측 검증**
- 프론트/백엔드 분리 배포와 CORS · 쿠키 도메인 설계
- OAuth2 소셜 로그인 (카카오)
