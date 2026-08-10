# CLAUDE.md

이 저장소에서 작업할 때 참고할 지침입니다.

## 이 프로젝트가 무엇인가

[slog.gg/p/14044](https://www.slog.gg/p/14044) 강의(37강)를 따라가는 **AWS 배포 + Blue/Green 무중단 CI/CD 실습**입니다. 학습이 목적이므로, 사용자가 직접 해보는 것이 우선입니다.

**작업을 시작하기 전에 [docs/HANDOFF.md](docs/HANDOFF.md) 를 읽으세요.** 진행 상황, 남은 작업, 인프라 재구축 절차, 그리고 이 프로젝트 특유의 함정 6가지가 정리되어 있습니다.

## 현재 상태

- 강의 37강 중 34강 완료. 남은 것은 **백엔드 CI/CD 자동 실행 검증(18·32강)** 하나
- **AWS 인프라는 `terraform destroy` 로 내려간 상태입니다.** 재구축 절차는 HANDOFF 3장 참고
- 프론트(Vercel)와 도메인은 살아 있습니다

## 구조

```
back/      Spring Boot 4.1 / Kotlin 2.3 / Java 21   (상세: back/CLAUDE.md)
front/     Next.js 16 / React 19
infra/     Terraform — VPC · EC2 · IAM
docs/      다이어그램 · 인수인계 문서
.github/workflows/deploy.yml   Blue/Green CI/CD
```

**폴더명이 `backend`/`frontend` 가 아니라 `back`/`front` 입니다.** 강의 코드를 옮길 때 경로를 반드시 바꿔야 합니다.

## 반드시 지킬 것

**강의 코드를 그대로 복사하지 마세요.** 베이스가 강의보다 최신(Boot 4.1 vs 3.5)이라 의존성 좌표·자동설정 클래스명·스타터 이름이 다릅니다. 추가되는 항목만 골라 반영하고, 좌표는 `spring-boot-dependencies-<버전>.pom` 과 jar 의 `AutoConfiguration.imports` 로 실제 값을 확인한 뒤 `./gradlew build` 로 테스트까지 통과시키세요.

**리버스 프록시는 npmplus 입니다.** 강의의 jc21 NPM 과 API 가 다릅니다 — 관리 콘솔이 HTTPS, 토큰이 HttpOnly 쿠키, PUT 스키마가 엄격. 자세한 내용은 HANDOFF 5장 ②.

**비밀은 커밋하지 마세요.** `infra/secrets.tf`, `back/.env` 는 gitignore 되어 있습니다. terraform 의 `user_data` 는 민감정보로 취급되지 않아 `plan` 출력과 `tfstate` 에 평문으로 남습니다 — 화면 공유나 로그 붙여넣기 시 주의.

## 자주 쓰는 명령

```bash
# 백엔드
cd back && ./gradlew build          # 테스트 포함
cd back && ./gradlew bootRun        # 로컬 실행 (dev 프로필)

# 프론트
cd front && pnpm dev

# 인프라
cd infra && terraform plan
cd infra && terraform apply
```

PowerShell 에서 `-플래그=값` 형태는 따옴표로 감싸세요: `terraform apply "-replace=aws_instance.ec2_1"`

## 커밋 규칙

3자리 일련번호 + 설명. 현재 `011` 까지 진행됨.

```
012 : 18강 CI/CD 자동 실행 검증
```

## 서버 진단

인프라가 살아 있을 때, EC2 에 SSH 없이 AWS SSM 으로 접근합니다 (11강에서 `AmazonEC2RoleforSSM` 부여).

```bash
aws ssm send-command --instance-ids <id> --document-name "AWS-RunShellScript" \
  --parameters file://params.json
```

SSM 파라미터에 한글이 섞이면 Windows AWS CLI 가 인코딩 오류를 냅니다. 스크립트를 base64 로 인코딩해 보내면 안전합니다.
