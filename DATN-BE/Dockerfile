FROM maven:3.9.4-eclipse-temurin-17-alpine AS builder
WORKDIR /build

COPY . .

RUN mvn clean package -DskipTests -DskipITS

FROM openjdk:17-jdk-alpine
WORKDIR /app

COPY --from=builder /build/target/DATN-BE-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]