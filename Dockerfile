FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src/main ./src/main
RUN mvn -f pom.xml clean package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/rest-api-0.0.1.jar /app/rest-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/rest-api.jar"]