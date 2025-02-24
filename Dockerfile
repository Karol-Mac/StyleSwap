FROM maven:3.9.4-eclipse-temurin-20-alpine AS builder
WORKDIR /app

COPY pom.xml .
COPY src/ ./src/
RUN mvn clean package

FROM openjdk:20-jdk-slim
WORKDIR /app

COPY --from=builder /app/target/StyleSwap-0.0.1-SNAPSHOT.jar /app/styleswap.jar

ENTRYPOINT ["java", "-jar", "styleswap.jar"]