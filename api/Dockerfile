FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle :api:buildFatJar --no-daemon

FROM openjdk:19
RUN mkdir -p /app/api
COPY --from=build /app/api/build/libs/api-all.jar /app/api/api-all.jar
EXPOSE 8080:8080
ENTRYPOINT ["java", "-jar", "/app/api/api-all.jar"]
