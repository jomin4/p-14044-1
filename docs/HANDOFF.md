# 인수인계 — 어디까지 했고, 무엇이 남았나

이 문서 하나만 읽으면 이 프로젝트를 이어서 진행할 수 있도록 정리했습니다.
마지막 갱신: 2026-08-10

---

## 한 줄 요약

강의 37강 중 **34강까지 완료**했고, **AWS 인프라는 비용 문제로 `terraform destroy` 하여 내려간 상태**입니다.
남은 것은 **백엔드 CI/CD 자동 실행 검증**(18·32강) 하나이며, 코드는 완성되어 있고 GitHub 계정 결제 문제로 실행만 못 했습니다.

---

## 1. 진행 상황

### 목표 체크리스트

| # | 목표 | 상태 |
|---|---|---|
| 1 | 도메인 구매/연결 (`api.` / `www.`) | ✅ |
| 2 | 백엔드 HTTPS + HTTP/3 | ✅ |
| 3 | 무중단 CI/CD — 프론트(Vercel) | ✅ 자동 배포 동작 |
| 3 | 무중단 CI/CD — 백엔드(Actions) | ⚠️ **코드 완성 · 자동 실행 미검증** |
| 4 | 운영 프론트에서 카카오 로그인 | ✅ |

### 강의별

| 구간 | 강 | 상태 |
|---|---|---|
| 프로젝트·AWS·테라폼·도메인·SSL | 1~13 | ✅ |
| 스프링부트 운영모드 · 수동 빌드/실행 | 14~16 | ✅ |
| 리포 세팅 (권한 · 시크릿) | 17 | ✅ |
| `deploy.yml` (중단 있는 배포) | 18 | ⚠️ 코드 완성, **미실행** |
| Blue/Green 무중단 | 19 | ✅ 코드 + **서버에서 직접 실행하여 실측 검증** |
| 자원 제거 | 20 | ✅ destroy 완료 |
| 복습 (처음부터 배포까지) | 21~25 | ✅ 내용상 완료 |
| 프론트 Vercel 배포 · 도메인 | 26~29 | ✅ |
| Swagger 가입·로그인·글 작성 | 30 | ✅ |
| 소셜 로그인 (카카오) | 31 | ✅ 카카오만. 구글·네이버 미설정 |
| 백/프 분리 배포 확인 | 32 | ⬜ Actions 복구 후 |
| MySQL · 로그 · Redis 접속 | 33~35 | ✅ |
| 루트 → www 리다이렉트 | 36 | ✅ |
| `npm.도메인` 관리 콘솔 | 37 | ✅ |

### 무중단 배포 실측 기록

전환 중 외부에서 0.25초 간격 요청:

| | 총 요청 | 200 | 실패 |
|---|---|---|---|
| 업스트림 전환만 | 139 | 139 | 0 |
| 전체 사이클 (`_2` → `_1`) | 133 | 133 | 0 |
| 전환 실패 케이스(400) | 87 | 87 | 0 |

세 번째가 중요합니다 — **전환이 실패했는데도 서비스가 끊기지 않았습니다.** blue 를 건드리기 전에 중단하는 설계가 실제로 동작했습니다.

---

## 2. 남은 작업

### A. 백엔드 CI/CD 자동 실행 (18·32강) — 유일한 미완주

**막힌 이유:** GitHub 계정이 결제 문제로 잠겨(`your account is locked due to a billing issue`) Actions 잡이 시작조차 못 했습니다. 워크플로 코드·시크릿·권한은 모두 준비돼 있습니다.

**재개 절차:**

1. https://github.com/settings/billing/summary 에서 잠금 해제 확인
2. 워크플로에 수동 트리거를 추가하면 의미 없는 커밋 없이 검증할 수 있습니다:
   ```yaml
   on:
     workflow_dispatch:     # 추가
     push:
       paths: ...
   ```
3. `gh workflow run deploy` → `gh run watch`
4. 3개 잡이 순서대로 초록불인지, 배포 중 `https://api.<도메인>` 이 계속 200인지 확인

**주의:** CI 가 도는 경로는 서버의 `.env` 를 읽지 않습니다. GitHub Secret `DOT_ENV` 를 씁니다. 두 값이 다르면 CI 배포가 수동 배포를 덮어씁니다.

### B. 구글 · 네이버 로그인 (선택)

`back/.env` 의 `NEED_TO_SET` 4개. 카카오와 같은 패턴이고 각 개발자 콘솔에서 리다이렉트 URI 를 등록하면 됩니다.

```
구글   https://console.cloud.google.com
네이버 https://developers.naver.com/apps/#/list
Redirect URI: https://api.<도메인>/login/oauth2/code/{google|naver}
```

### C. 보안 그룹 좁히기 (선택)

`infra/main.tf` 의 보안 그룹이 **모든 포트를 0.0.0.0/0 에 개방**합니다. 학습용 설정이며, 계속 운영한다면 좁혀야 합니다.

```
80, 443   0.0.0.0/0     서비스
3306      내 IP만        DB 툴 접속용
6379, 81  닫음           (npm.<도메인> 경유로 콘솔 접근)
```

---

## 3. 인프라 재구축 절차

destroy 한 상태에서 다시 올릴 때의 순서입니다. **DNS 와 인증서를 다시 맞춰야 하는 게 가장 번거롭습니다.**

### 사전 준비

| 필요한 것 | 비고 |
|---|---|
| AWS IAM 액세스 키 | 정리했다면 재발급 → `aws configure` |
| GitHub PAT (`read:packages`, 300일 이하) | 폐기했으므로 재발급 |
| `infra/secrets.tf` | **gitignore 라 리포에 없습니다. 새로 작성 필요** |
| 도메인 | `jomin4.cloud` (가비아 등록 · DNSZi 위임) 유지 중 |

`infra/secrets.tf` 형식:
```hcl
variable "app_1_db_name"               { default = "p-14044-1" }
variable "password_1"                  { default = "" }   # NPM·MySQL·Redis 공용, ASCII 권장
variable "github_access_token_1_owner" { default = "" }   # GitHub 계정 소문자
variable "github_access_token_1"       { default = "" }   # read:packages only
```

### 순서

```bash
# 1. 인프라
cd infra
terraform init
terraform apply            # 약 3~5분, 새 공인 IP 발급됨

# 2. DNS (DNSZi)
#    A     api  → 새 IP
#    A     @    → 새 IP
#    CNAME www  → (Vercel 값, 변경 불필요)
#    CNAME npm  → api.<도메인>   (변경 불필요)

# 3. npmplus 설정  https://<새IP>:81   (admin@npm.com / password_1)
#    Proxy Host      api.<도메인>  → http  → p-14044-1_1:8080   + TLS 발급
#    Redirection     <도메인>      → https → www.<도메인>        + TLS 발급  ※ Scheme 은 https
#    Proxy Host      npm.<도메인>  → https → npm_1:81           + TLS 발급  ※ Scheme 은 https

# 4. 앱 배포 (Actions 가 살아 있다면 git push 로 자동)
sudo su
dnf install -y git
mkdir -p ~/temp/source && cd ~/temp/source
git clone https://github.com/jomin4/p-14044-1 .
cd back
cat > .env << 'EOF'
... 로컬 back/.env 내용 붙여넣기 ...
EOF
docker build -t p-14044-1:v0.0.1 .
docker run -d --restart unless-stopped --network common \
  --name p-14044-1_1 -e TZ=Asia/Seoul p-14044-1:v0.0.1

# 5. 초기 데이터
#    Swagger 에서 system / admin 가입 (username 이 곧 관리자 권한)
```

**Let's Encrypt 발급 한도**에 주의하세요. 동일 도메인 조합 주당 5회입니다. 재구축을 반복하면 걸립니다.

---

## 4. 무중단 배포 수동 실행

Actions 없이도 서버에서 직접 돌릴 수 있습니다. 워크플로 3번 잡과 동일한 로직입니다.

```bash
# 서버에서
cd ~/temp/source && git pull
vi back/.env                      # 설정 변경이 있다면
cd back
docker build -t p-14044-1:v0.0.N .          # 태그를 반드시 올릴 것 (롤백 대상 보존)
docker run --rm --entrypoint cat p-14044-1:v0.0.N /app/.env | head -1   # 이미지 검증
bash /root/blue-green.sh p-14044-1:v0.0.N
```

> `/root/blue-green.sh` 는 destroy 와 함께 사라졌습니다. `.github/workflows/deploy.yml` 의 3번 잡 heredoc 안에 같은 스크립트가 들어 있으니 거기서 복원하면 됩니다.

**롤백**은 같은 스크립트에 옛 태그를 주면 됩니다. 헬스체크 후 전환이므로 롤백도 무중단입니다.
```bash
bash /root/blue-green.sh p-14044-1:v0.0.2
```

---

## 5. 반드시 알아야 할 함정

강의를 그대로 따라 하면 걸리는 것들입니다. 실제로 다 겪었습니다.

### ① 강의보다 최신 스택 — 코드를 복사하지 말 것

강의는 Spring Boot 3.5 / Kotlin 1.9 / 폴더명 `backend` 기준입니다. 이 프로젝트는 **Boot 4.1 / Kotlin 2.3 / 폴더명 `back`** 입니다.

| | 강의 | 실제 필요 |
|---|---|---|
| 세션 스타터 | `org.springframework.session:spring-session-data-redis` | `org.springframework.boot:spring-boot-starter-session-data-redis` |
| Redis 자동설정 | `...autoconfigure.data.redis.RedisAutoConfiguration` | `...boot.data.redis.autoconfigure.DataRedisAutoConfiguration` |
| 세션 자동설정 | `...autoconfigure.session.SessionAutoConfiguration` | `...boot.session.autoconfigure.SessionAutoConfiguration` |
| `@EnableCaching` | 그냥 붙이면 됨 | `spring-boot-starter-cache` 필요 (없으면 테스트 대량 실패) |
| 워크플로 경로 | `backend/...` | `back/...` |

의존성 좌표나 자동설정 클래스명은 추측하지 말고 `spring-boot-dependencies-<버전>.pom` 과 jar 안의 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 로 확인하세요.

### ② npmplus 는 jc21 NPM 이 아니다

`infra/main.tf` 는 강의의 `jc21/nginx-proxy-manager` 대신 **`zoeyvid/npmplus`** 를 씁니다 (HTTP/3 지원).

- 관리 콘솔이 **HTTPS**입니다. `http://IP:81` 은 308 리다이렉트만 돌려줍니다
- **토큰이 응답 JSON 에 없습니다.** `POST /api/tokens` 는 `{"expires":...}` 만 주고 토큰은 `__Host-Http-token` **HttpOnly 쿠키**로 옵니다 → `jq -r '.token'` + `Bearer` 헤더 대신 **쿠키 항아리(`curl -c` / `-b`)** 사용
- `/api/tokens` 는 **5분에 10회** 요청 제한
- 업스트림 전환 PUT 은 스키마가 `additionalProperties:false` 입니다. **`{forward_host, forward_port}` 두 필드만** 보내야 하며, 전체 객체를 되돌려 보내면 `400 data must NOT have additional properties`. 두 필드만 보내도 인증서·SSL·HTTP2/3 설정은 서버가 유지합니다
- Redirection Host 의 Scheme 을 `auto` 로 두면 `Location: auto://...` 라는 깨진 헤더가 나갑니다. **`https` 명시** 필요
- 인증서를 발급하지 않으면 그 이름의 `:443` 서버 블록이 아예 생성되지 않아 기본 호스트("Congratulations!" 페이지)로 떨어집니다

### ③ 카카오 — 클라이언트 시크릿이 기본 활성화

카카오가 정책을 바꿔 **REST API 키 발급 시 클라이언트 시크릿이 기본 ON** 입니다. `application.yaml` 은 카카오만 `client-secret` 없이 설정하므로, 그대로 두면 동의 화면까지 통과하고 **토큰 교환에서 실패**합니다.

- 증상: 동의 후 `api.<도메인>/login?error` (formLogin 을 꺼서 Whitelabel 404 로 보임). 운영 로그 레벨이 INFO 라 서버 로그엔 아무것도 안 남음
- **원인 판별법** (자격증명 불필요):
  ```
  POST https://kauth.kakao.com/oauth/token
  grant_type=authorization_code&client_id=<키>&redirect_uri=<등록한 URI>&code=DUMMY
  ```
  `401 KOE010` → 시크릿 요구 중 / `400 KOE320` → 시크릿 요구 없음(정상)
- 해결 A(권장): 콘솔에서 시크릿 **비활성화**. 재빌드 불필요
- 해결 B: `client-secret` + **`client-authentication-method: client_secret_post`** 추가 (카카오는 헤더가 아닌 POST 본문으로 받음)

또한 이번 콘솔 개편으로 **리다이렉트 URI 가 앱이 아니라 REST API 키 단위**입니다 (`앱 > 플랫폼 키 > REST API 키 > 리다이렉트 URI`). 키를 여러 개 만들면 `.env` 의 키와 URI 를 등록한 키가 달라 `KOE006` 이 납니다.

### ④ `.env` 는 세 곳에 따로 산다

```
① 로컬 back/.env               내 PC 개발용. 운영에 영향 없음
② 서버 ~/temp/source/back/.env  수동 배포 시의 반영 경로
③ GitHub Secret DOT_ENV         CI/CD 배포 시의 반영 경로
```

`.env` 는 Dockerfile 의 `COPY .env .env` 로 **이미지에 구워집니다.** 설정을 바꾸려면 재빌드가 필요하고, ①만 고치면 운영은 바뀌지 않습니다. 실제로 `client_id=NEED_TO_SET` 인 채로 배포되어 헤맸습니다.

**소셜 로그인 디버깅은 브라우저 주소창부터 보세요.** 카카오로 넘어가는 URL 에 `client_id`·`redirect_uri`·`scope` 가 그대로 드러납니다.

### ⑤ DNS 네거티브 캐시

이 존의 SOA minimum 이 **7200초(2시간)** 입니다. 없는 이름을 한 번 조회하면 그 "없음"이 최대 2시간 캐싱됩니다. 레코드를 추가해도 한동안 안 보이는 이유이며, 실제로 여러 번 겪었습니다.

- 권한 서버 확인: `nslookup <이름> ns22.dnszi.com` — 캐시 무관
- 공개 리졸버 확인: `nslookup <이름> 8.8.8.8`
- DNSZi 는 노드 간 동기화에 수 분 걸립니다 (ns11/ns22/ns37 이 시차를 두고 반영)

### ⑥ PowerShell 에서 terraform 플래그

`-replace=aws_instance.ec2_1` 처럼 `-이름=값` 형태는 PowerShell 이 `=` 뒤를 잘라먹습니다. **따옴표로 감싸세요.**
```powershell
terraform apply "-replace=aws_instance.ec2_1"
```

---

## 6. 외부 자산

| | 값 | 비고 |
|---|---|---|
| 도메인 | `jomin4.cloud` | 가비아 등록 · **DNSZi 로 네임서버 위임** |
| DNS 레코드 | `A @`, `A api`, `CNAME www`(Vercel), `CNAME npm`→`api` | `api`·`@` 는 재구축 시 새 IP 로 갱신 필요 |
| 프론트 | Vercel `p-14044-1` | Root Directory = `front` (모노레포라 필수) |
| 리포 | `github.com/jomin4/p-14044-1` (public) | |
| AWS | 계정 `691981917623` · 리전 `ap-northeast-2` | IAM `admin`, 액세스 키는 정리됨 |
| 카카오 앱 | `p-14044-1` | REST API 키 · 리다이렉트 URI 등록됨 |

**커밋 규칙:** `001`, `002` … 3자리 일련번호 + 설명. 현재 `011` 까지.

---

## 7. 참고

- 강의 원문: https://www.slog.gg/p/14044
- 아키텍처 다이어그램: [docs/diagrams/](diagrams/) — 브라우저로 열면 됩니다
- 백엔드 코드 구조: [back/CLAUDE.md](../back/CLAUDE.md)
