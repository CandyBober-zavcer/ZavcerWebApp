FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

COPY build/libs/web-application-1.0.jar app.jar
COPY data data
COPY public public

ENV APP_CONFIG_PATH=/data/app.properties

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]