
FROM maven:3.9.9-amazoncorretto-21-al2023 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install

FROM openjdk:24-ea-20-jdk-oracle
WORKDIR /app
COPY --from=build /app/target/StyleSwap-0.0.1-SNAPSHOT.jar /app/styleswap.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "styleswap.jar"]
