FROM openjdk:20-jdk-slim
WORKDIR /app

COPY target/styleswap-0.0.1-SNAPSHOT.jar /app/styleswap.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "styleswap.jar"]