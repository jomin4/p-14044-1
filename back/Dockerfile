# ---------- 1단계: 빌드 ----------
# 프로젝트의 Gradle 래퍼(9.5.1)를 그대로 쓰므로 gradle 이미지가 아닌 JDK 이미지를 쓴다.
# 툴체인이 Java 21 이므로 베이스도 21 로 맞춘다.
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# 래퍼와 빌드 스크립트를 먼저 복사 → 소스만 바뀌면 의존성 레이어는 캐시 재사용
COPY gradlew ./
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# 소스 복사 후 실행 가능 jar 생성
# bootJar 만 실행하므로 -plain.jar 가 생기지 않아 아래 와일드카드가 모호해지지 않는다
COPY .env ./
COPY src src
RUN ./gradlew bootJar --no-daemon

# ---------- 2단계: 실행 ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
COPY --from=builder /app/.env .env

# t3.micro(메모리 1GB)에서 Blue/Green 전환 중에는 두 컨테이너가 동시에 살아 있다.
# 힙 상한을 두지 않으면 둘이 메모리를 두고 경합하다 OOM 으로 죽을 수 있다.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=35.0", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
