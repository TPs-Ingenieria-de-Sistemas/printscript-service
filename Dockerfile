# syntax=docker/dockerfile:1.4
FROM --platform=linux/amd64 gradle:8.7.0-jdk17-jammy AS build
COPY  . /app
WORKDIR /app
RUN chmod +x gradlew

ARG USERNAME
ARG TOKEN

ENV USERNAME ${USERNAME}
ENV TOKEN ${TOKEN}

RUN ./gradlew bootJar

FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080
RUN mkdir /app

# Add New Relic configuration
ADD ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ADD ./newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml

COPY --from=build /app/build/libs/*.jar /app/printscript-service.jar
ENTRYPOINT ["java", "-javaagent:/usr/local/newrelic/newrelic.jar", "-jar", "-Dspring.profiles.active=production","/app/printscript-service.jar"]
