ARG SERVICE

FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
ARG SERVICE
WORKDIR /app
COPY . .
RUN mvn clean package -pl ${SERVICE} -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
ARG SERVICE
WORKDIR /app
COPY --from=builder /app/${SERVICE}/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]